package ru.mirea.moviestash

import android.util.Log
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.field.SqlType
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource
import com.j256.ormlite.stmt.SelectArg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.postgresql.util.PSQLException
import ru.mirea.moviestash.AppConstants.defUserLogin
import ru.mirea.moviestash.AppConstants.defUserPass
import ru.mirea.moviestash.AppConstants.jdbcUrl
import ru.mirea.moviestash.Crypto.decrypt
import ru.mirea.moviestash.Crypto.encrypt
import ru.mirea.moviestash.Crypto.getShaHash
import ru.mirea.moviestash.Crypto.toHex
import ru.mirea.moviestash.entites.BestFilms
import ru.mirea.moviestash.entites.Celebrity
import ru.mirea.moviestash.entites.CelebrityInContent
import ru.mirea.moviestash.entites.Collection
import ru.mirea.moviestash.entites.Content
import ru.mirea.moviestash.entites.ContentInCollection
import ru.mirea.moviestash.entites.Country
import ru.mirea.moviestash.entites.CountryOfContent
import ru.mirea.moviestash.entites.Genre
import ru.mirea.moviestash.entites.GenreOfContent
import ru.mirea.moviestash.entites.News
import ru.mirea.moviestash.entites.RecentNews
import ru.mirea.moviestash.entites.Review
import ru.mirea.moviestash.entites.RoleClassifier
import ru.mirea.moviestash.entites.SiteUser
import ru.mirea.moviestash.entites.UserStar
import java.security.MessageDigest
import java.sql.Connection
import java.sql.Date
import java.sql.Types

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

object DatabaseController {
    private val mutex = Mutex()
    private var connection: Connection? = null
    private lateinit var connectionSource: JdbcConnectionSource
    private var sPassword: String = defUserPass
    private var sLogin: String = defUserLogin
    var user: SiteUser? = null
        private set

    fun logOut() {
        user = null
        sLogin = defUserLogin
        sPassword = defUserPass
    }

    suspend fun login(
        login: String = sLogin, password: String = sPassword
    ): Result<String> {
        var message: Result<String>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                try {
                    var realPass = password
                    if (realPass != defUserPass) {
                        realPass = getShaHash(password, login)
                    }
                    val newConnectionSource = JdbcConnectionSource(jdbcUrl, login, realPass)
                    val newConnection = newConnectionSource.getReadWriteConnection("").underlyingConnection
                    connectionSource = newConnectionSource
                    connection = newConnection
                    message = if (connection?.isValid(0) == true) {
                        if (login != defUserLogin) {
                            sPassword = password
                            sLogin = login
                            val siteUserDao =
                                DaoManager.createDao(connectionSource, SiteUser::class.java)
                            val res = siteUserDao.queryForEq("login", login)
                            if (res.size == 1) user = res[0]
                        }
                        Result.Success("Вход выполнен!")
                    } else Result.Error(Exception("Ошибка соединения"))
                } catch (e: PSQLException) {
                    if (e.message?.contains("password authentication failed") == true) {
                        message = Result.Error(Exception("Неверные учетные данные!"))
                    }
                    else {
                        Log.e("ERROR", "Log in error", e)
                        message = Result.Error(Exception("Произошла непредвиденная ошибка"))
                    }
                }
            }
        }
        return message
    }

    suspend fun registerNewUser(
        login: String, password: String, username: String, email: String
    ): Result<Boolean> {
        var message: Result<Boolean>
        mutex.withLock {
            withContext(Dispatchers.IO) {
                if (connection?.isValid(0) == true) {
                    try {
                        var query = connection!!.prepareStatement("SELECT start_registration(?, ?)")
                        query.setString(1, login)
                        val bytes =
                            MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
                        query.setString(2, bytes.toHex())
                        val rs = query.executeQuery()
                        var hsh = ""
                        while (rs.next()) hsh = rs.getString("start_registration")
                        val randNum = decrypt(hsh, bytes)
                        query = connection!!.prepareCall("CALL register_user(?, ?, ?, ?)")
                        query.setString(1, username)
                        query.setString(2, email)
                        query.setString(3, login)
                        val pass = encrypt(password, randNum)
                        query.setString(4, pass)
                        message = Result.Success(query.execute())
                    } catch (e: PSQLException) {
                        message = Result.Error(e)
                        Log.d("DEBUG", e.message.toString())
                    }
                } else message = Result.Error(Exception("Ошибка соединения"))
            }
        }
        return message
    }

    suspend fun modUserData(nick: String, email: String): Result<Boolean> {
        var result: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                result = try {
                    if (connection?.isValid(0) == true) {
                        connection!!.prepareCall("CALL update_user(?, ?, ?)").apply {
                            setString(1, nick)
                            setString(2, email)
                            setInt(3, user!!.id)
                            execute()
                        }
                        Result.Success(true)
                    } else Result.Error(Exception("Ошибка запроса 1"))
                } catch (e: PSQLException) {
                    println(e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return result
    }

    suspend fun refreshUserData() {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                if (connection?.isValid(0) == true) {
                    val siteUserDao = DaoManager.createDao(connectionSource, SiteUser::class.java)
                    val siteUserQueryBuilder = siteUserDao.queryBuilder()
                    siteUserQueryBuilder.where().raw("login = current_user")
                    val res = siteUserDao.query(siteUserQueryBuilder.prepare())
                    if (res.size > 0) {
                        val newInfo = res[0]
                        user?.apply {
                            nickname = newInfo.nickname
                            email = newInfo.email
                            banned = newInfo.banned
                            banDate = newInfo.banDate
                            banReason = newInfo.banReason
                        }
                    }
                }
            }
        }
    }

    suspend fun checkConnection(): Result<Boolean> {
        var valid: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                if (connection?.isValid(0) == true) {
                    valid = Result.Success(true)
                    return@withContext
                }
                valid = try {
                    if (::connectionSource.isInitialized) connectionSource.close()
                    var tmpPass = sPassword
                    if (tmpPass != defUserPass) tmpPass = getShaHash(sPassword, sLogin)
                    connectionSource = JdbcPooledConnectionSource(jdbcUrl, sLogin, tmpPass)
                    connection = connectionSource.getReadWriteConnection("").underlyingConnection
                    if (sLogin != defUserLogin && user == null) {
                        val siteUserDao =
                            DaoManager.createDao(connectionSource, SiteUser::class.java)
                        val res = siteUserDao.queryForEq("login", sLogin)
                        if (res.size > 0) {
                            user = res[0]
                        }
                    }
                    Result.Success(connection?.isValid(0) == true)
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return valid
    }

    suspend fun searchForMovie(filmName: String, offset: Int): Result<List<Content>> {
        var data: Result<List<Content>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val contentDao = DaoManager.createDao(connectionSource, Content::class.java)
                        val contentQueryBuilder = contentDao.queryBuilder()
                        contentQueryBuilder.where()
                            .raw("name ILIKE ?", SelectArg(SqlType.STRING, "%$filmName%"))
                        contentQueryBuilder.limit(20)
                        contentQueryBuilder.offset(offset.toLong())
                        val res = contentDao.query(contentQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 2"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun searchForPerson(pName: String, offset: Int): Result<List<Celebrity>> {
        var data: Result<List<Celebrity>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val celebrityDao =
                            DaoManager.createDao(connectionSource, Celebrity::class.java)
                        val celebrityRequestBuilder = celebrityDao.queryBuilder()
                        celebrityRequestBuilder.where()
                            .raw("name ILIKE ?", SelectArg(SqlType.STRING, "%$pName%"))
                        celebrityRequestBuilder.limit(20)
                        celebrityRequestBuilder.offset(offset.toLong())
                        val res = celebrityDao.query(celebrityRequestBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 3"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getMainPageCont(): Result<List<Content>> {
        var data: Result<List<Content>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val contentDao = DaoManager.createDao(connectionSource, Content::class.java)
                        val contentQueryBuilder = contentDao.queryBuilder()
                        contentQueryBuilder.orderByRaw("RANDOM()")
                        contentQueryBuilder.limit(6)
                        val res = contentDao.query(contentQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 4"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getTop(limit: Int, offset: Int): Result<List<Content>> {
        var data: Result<List<Content>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val contentDao =
                            DaoManager.createDao(connectionSource, BestFilms::class.java)
                        val contentQueryBuilder = contentDao.queryBuilder()
                        contentQueryBuilder.limit(limit.toLong())
                        contentQueryBuilder.offset(offset.toLong())
                        val res = contentDao.query(contentQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getLastNews(): Result<List<News>> {
        var data: Result<List<News>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val newsDao = DaoManager.createDao(connectionSource, RecentNews::class.java)
                        Result.Success(newsDao.queryForAll())
                    } else Result.Error(Exception("Ошибка запроса 5"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getBannedUsers(): Result<List<SiteUser>> {
        var data: Result<List<SiteUser>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val siteUserDao =
                            DaoManager.createDao(connectionSource, SiteUser::class.java)
                        val res = siteUserDao.queryForEq("is_banned", true)
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 6"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getRatingForContent(cid: Int): Result<Float> {
        var data: Result<Float>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val contentDao = DaoManager.createDao(connectionSource, Content::class.java)
                        val contentQueryBuilder = contentDao.queryBuilder()
                        contentQueryBuilder.selectColumns("rating")
                        contentQueryBuilder.where().eq("content_id", cid)
                        val res = contentDao.query(contentQueryBuilder.prepare())
                        if (res.size > 0) Result.Success(res[0].rating)
                        else Result.Error(Exception("Ошибка запроса 8"))
                    } else Result.Error(Exception("Ошибка запроса 8"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getRating(cid: Int): Result<List<UserStar>> {
        var data: Result<List<UserStar>>
        if (user == null) return Result.Error(Exception("Not logged in"))
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val userStarDao =
                            DaoManager.createDao(connectionSource, UserStar::class.java)
                        val userStartRequestBuilder = userStarDao.queryBuilder()
                        userStartRequestBuilder.where().eq("content_id", cid).and()
                            .eq("uid", user!!.id)
                        val res = userStarDao.query(userStartRequestBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 9"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun setRating(id: Int, rating: Short, update: Boolean): Result<Boolean> {
        var data: Result<Boolean>
        if (user == null) return Result.Error(Exception("Войдите в аккаунт"))
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query = if (rating.toInt() == 0) {
                            connection!!.prepareCall("CALL delete_star(?)").apply {
                                setInt(1, id)
                            }
                        } else if (update) connection!!.prepareCall("CALL update_star(?, ?)")
                            .apply {
                                setInt(1, id)
                                setShort(2, rating)
                            }
                        else connection!!.prepareCall("CALL add_star(?, ?, ?)").apply {
                            setInt(1, id)
                            setInt(2, user!!.id)
                            setShort(3, rating)
                        }
                        Result.Success(query.execute())
                    } else Result.Error(Exception("Ошибка запроса 10"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }


    suspend fun getFilmByActor(aid: Int): Result<List<Content>> {
        var data: Result<List<Content>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val contentDao = DaoManager.createDao(connectionSource, Content::class.java)
                        val query = contentDao.queryBuilder()
                        query.distinct()
                        val celebrityInContentDao =
                            DaoManager.createDao(connectionSource, CelebrityInContent::class.java)
                        val queryBuilder = celebrityInContentDao.queryBuilder()
                        queryBuilder.where().eq("cid", aid)
                        query.leftJoin(queryBuilder)
                        val res = contentDao.query(query.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 11"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getGenres(filmId: Int): Result<List<Genre>> {
        var data: Result<List<Genre>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val genreOfContentDao =
                            DaoManager.createDao(connectionSource, GenreOfContent::class.java)
                        val genreOfContentQueryBuilder = genreOfContentDao.queryBuilder()
                        val genreDao = DaoManager.createDao(connectionSource, Genre::class.java)
                        val genreQueryBuilder = genreDao.queryBuilder()
                        genreOfContentQueryBuilder.where().eq("content_id", filmId)
                        genreQueryBuilder.leftJoin(genreOfContentQueryBuilder)
                        val res = genreDao.query(genreQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 12"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getCountries(filmId: Int): Result<List<Country>> {
        var data: Result<List<Country>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val countryDao = DaoManager.createDao(connectionSource, Country::class.java)
                        val countryQueryBuilder = countryDao.queryBuilder()
                        val countryOfContentDao =
                            DaoManager.createDao(connectionSource, CountryOfContent::class.java)
                        val countryOfContentQueryBuilder = countryOfContentDao.queryBuilder()
                        countryOfContentQueryBuilder.where().eq("content_id", filmId)
                        countryQueryBuilder.leftJoin(countryOfContentQueryBuilder)
                        val res = countryDao.query(countryQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 13"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getCelebrity(
        filmId: Int, offset: Int, size: Int, actors: Boolean = true
    ): Result<List<Celebrity>> {
        var data: Result<List<Celebrity>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val celebrityDao =
                            DaoManager.createDao(connectionSource, Celebrity::class.java)
                        val celebrityQueryBuilder = celebrityDao.queryBuilder()
                        val celebrityInContentDao =
                            DaoManager.createDao(connectionSource, CelebrityInContent::class.java)
                        val celebrityInContentQueryBuilder = celebrityInContentDao.queryBuilder()
                        val roleDao =
                            DaoManager.createDao(connectionSource, RoleClassifier::class.java)
                        val roleQueryBuilder = roleDao.queryBuilder()
                        celebrityQueryBuilder.setAlias("c")
                        celebrityInContentQueryBuilder.setAlias("cic")
                        roleQueryBuilder.setAlias("rc")
                        celebrityInContentQueryBuilder.leftJoin(roleQueryBuilder)
                        celebrityQueryBuilder.leftJoin(celebrityInContentQueryBuilder)
                        celebrityQueryBuilder.selectRaw(
                            "c.cid", "c.name", "rc.name as role", "cic.description", "c.img_link"
                        )
                        celebrityQueryBuilder.where().raw("cic.cid = c.cid").and()
                            .raw("cic.role = rc.role_id").and().raw("content_id = $filmId").and()
                            .raw(if (actors) "cic.role = 4" else "cic.role != 4")
                        celebrityQueryBuilder.orderByRaw("priority")
                        celebrityQueryBuilder.limit(size.toLong())
                        celebrityQueryBuilder.offset(offset.toLong())
                        val res = celebrityDao.queryRaw(
                            celebrityQueryBuilder.prepareStatementString(),
                            { names, columns ->
                                val celeb = Celebrity()
                                if (columns != null) {
                                    for (i in names.indices) {
                                        when (names[i]) {
                                            "cid" -> celeb.id = columns[i].toInt()
                                            "name" -> celeb.name = columns[i]
                                            "img_link" -> columns[i]?.let { celeb.img = it }
                                            "role" -> columns[i]?.let { celeb.role = it }
                                            "description" -> columns[i]?.let {
                                                celeb.description = it
                                            }
                                        }
                                    }
                                }
                                celeb
                            })
                        Result.Success(res.results)
                    } else Result.Error(Exception("Ошибка запроса 14"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getContentFromCollection(
        filmId: Int, offset: Int, size: Int
    ): Result<List<Content>> {
        var data: Result<List<Content>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val contentInCollectionDao =
                            DaoManager.createDao(connectionSource, ContentInCollection::class.java)
                        val contentInCollectionQueryBuilder = contentInCollectionDao.queryBuilder()
                        val contentDao = DaoManager.createDao(connectionSource, Content::class.java)
                        val contentQueryBuilder = contentDao.queryBuilder()
                        contentInCollectionQueryBuilder.where().eq("collection_id", filmId)
                        contentInCollectionQueryBuilder.orderBy("film_number", true)
                        contentQueryBuilder.leftJoin(contentInCollectionQueryBuilder)
                        contentQueryBuilder.limit(size.toLong())
                        contentQueryBuilder.offset(offset.toLong())
                        val res = contentDao.query(contentQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 15"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getMovieByGenre(genre: Int, offset: Int): Result<List<Content>> {
        var data: Result<List<Content>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val contentDao = DaoManager.createDao(connectionSource, Content::class.java)
                        val contentQueryBuilder = contentDao.queryBuilder()
                        val genresOfContentDao =
                            DaoManager.createDao(connectionSource, GenreOfContent::class.java)
                        val genreOfContentQueryBuilder = genresOfContentDao.queryBuilder()
                        val genresDao = DaoManager.createDao(connectionSource, Genre::class.java)
                        val genreQueryBuilder = genresDao.queryBuilder()
                        genreQueryBuilder.where().eq("genre_id", genre)
                        genreOfContentQueryBuilder.leftJoin(genreQueryBuilder)
                        contentQueryBuilder.leftJoin(genreOfContentQueryBuilder)
                        contentQueryBuilder.orderBy("content_id", false)
                        contentQueryBuilder.limit(10)
                        contentQueryBuilder.offset(offset.toLong())
                        val res = contentDao.query(contentQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 17"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getEditorCols(limit: Int, offset: Int): Result<List<Collection>> {
        var data: Result<List<Collection>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val collectionDao =
                            DaoManager.createDao(connectionSource, Collection::class.java)
                        val collectionQueryBuilder = collectionDao.queryBuilder()
                        collectionQueryBuilder.where().raw("uid is null")
                        collectionQueryBuilder.limit(limit.toLong())
                        collectionQueryBuilder.offset(offset.toLong())
                        val res = collectionDao.query(collectionQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 18"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getUserCols(uid: Int): Result<List<Collection>> {
        var data: Result<List<Collection>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val collectionDao =
                            DaoManager.createDao(connectionSource, Collection::class.java)
                        val res = collectionDao.queryForEq("uid", uid)
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 19"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun deleteUserCols(cid: Int): Result<Boolean> {
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query = connection!!.prepareCall("CALL delete_collection(?)")
                        query.setInt(1, cid)
                        query.execute()
                        Result.Success(true)
                    } else Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun deleteReview(rid: Int): Result<Boolean> {
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query = connection!!.prepareCall("CALL delete_review(?)")
                        query.setInt(1, rid)
                        Result.Success(query.execute())
                    } else Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun manageUser(uid: Int, reason: String, ban: Boolean): Result<Boolean> {
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query =
                            if (ban) connection!!.prepareCall("CALL ban_user_by_id(?, ?)").apply {
                                setInt(1, uid)
                                setString(2, reason)
                            }
                            else connection!!.prepareCall("CALL unban_user_by_id(?)").apply {
                                setInt(1, uid)
                            }
                        Result.Success(query.execute())
                    } else Result.Error(Exception("Ошибка запроса 22"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun publishUserCols(cid: Int): Result<Boolean> {
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query =
                            connection!!.prepareStatement("UPDATE collection SET uid = null WHERE collection_id = ?")
                        query.setInt(1, cid)
                        query.executeUpdate()
                        Result.Success(true)
                    } else Result.Error(Exception("Ошибка запроса 23"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun hideCols(cid: Int): Result<Boolean> {
        if (user == null) return Result.Error(Exception("Войдите в аккаунт"))
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query = connection!!.prepareStatement(
                            "UPDATE collection SET uid = ? WHERE collection_id = ?"
                        ).apply {
                            setInt(1, user!!.id)
                            setInt(2, cid)
                        }
                        query.executeUpdate()
                        Result.Success(true)
                    } else Result.Error(Exception("Ошибка запроса 23"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun addModNews(
        title: String, description: String, id: Int, image: String?
    ): Result<Boolean> {
        var data: Result<Boolean>
        if (user == null) {
            return Result.Error(Exception("Для создания обзора надо войти в аккаунт!"))
        }
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query =
                            if (id != 0) connection!!.prepareCall("CALL update_new(?, ?, ?, ?)")
                                .apply {
                                    setString(1, description)
                                    setString(2, title)
                                    setInt(3, id)
                                    if (image != null) setString(4, image)
                                    else setNull(4, Types.VARCHAR)
                                }
                            else connection!!.prepareCall("CALL add_new(?, ?, ?, ?)").apply {
                                setString(1, title)
                                setString(2, description)
                                setInt(3, user!!.id)
                                if (image != null) setString(4, image)
                                else setNull(4, Types.VARCHAR)
                            }
                        Result.Success(query.execute())
                    } else Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    if (e.message.toString()
                            .contains("duplicate key")
                    ) Result.Error(Exception("Вы уже делали обзор на этот фильм"))
                    else Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getNews(limit: Int, offset: Int): Result<List<News>> {
        var data: Result<List<News>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val newDao = DaoManager.createDao(connectionSource, News::class.java)
                        val newQueryBuilder = newDao.queryBuilder()
                        newQueryBuilder.orderBy("news_date", false)
                        newQueryBuilder.orderBy("nid", false)
                        newQueryBuilder.limit(limit.toLong())
                        newQueryBuilder.offset(offset.toLong())
                        val res = newDao.query(newQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 25"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getNewsById(nid: Int): Result<List<News>> {
        var data: Result<List<News>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val newDao = DaoManager.createDao(connectionSource, News::class.java)
                        val res = newDao.queryForEq("nid", nid)
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 26"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun deleteNews(nid: Int): Result<Boolean> {
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query = connection!!.prepareCall("CALL delete_new(?)")
                        query.setInt(1, nid)
                        Result.Success(query.execute())
                    } else Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun addModReview(
        title: String, description: String, id: Int, opinion: Int, update: Boolean
    ): Result<Boolean> {
        var data: Result<Boolean>
        if (user == null) {
            return Result.Error(Exception("Для создания обзора надо войти в аккаунт!"))
        }
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query =
                            if (update) connection!!.prepareCall("CALL update_review(?, ?, ?, ?)")
                                .apply {
                                    setString(1, title)
                                    setString(2, description)
                                    setShort(3, opinion.toShort())
                                    setInt(4, id)
                                }
                            else connection!!.prepareCall("CALL add_review(?, ?, ?, ?, ?)").apply {
                                setString(1, title)
                                setString(2, description)
                                setInt(3, id)
                                setInt(4, user!!.id)
                                setShort(5, opinion.toShort())
                            }
                        Result.Success(query.execute())
                    } else Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    if (e.message.toString()
                            .contains("duplicate key")
                    ) Result.Error(Exception("Вы уже делали обзор на этот фильм!"))
                    else if (e.message.toString()
                            .contains("valid_review")
                    ) Result.Error(Exception("Недопустимый текст рецензии!"))
                    else Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun isModerator(): Result<Boolean> {
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query = connection!!.createStatement()
                        val result =
                            query.executeQuery("SELECT pg_has_role(current_user, 'moderator', 'MEMBER')")
                        if (result.next()) {
                            Result.Success(result.getBoolean("pg_has_role"))
                        } else Result.Error(Exception("Запрос не вернул результатов"))
                    } else Result.Error(Exception("Ошибка запроса 29"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun addModUserCols(
        uid: Int, name: String, description: String, update: Boolean
    ): Result<Boolean> {
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query =
                            if (update) connection!!.prepareCall("CALL update_collection(?, ?, ?)")
                            else connection!!.prepareCall("CALL add_collection(?, ?, ?)")
                        query.setString(1, name)
                        query.setString(2, description)
                        query.setInt(3, uid)
                        Result.Success(query.execute())
                    } else Result.Error(Exception("Ошибка запроса 30"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun addDelFilmToCol(cid: Int, fid: Int, add: Boolean): Result<Boolean> {
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val query =
                            if (add) connection!!.prepareCall("CALL add_film_to_collection(?, ?)")
                            else connection!!.prepareCall("CALL delete_film_from_collection(?, ?)")
                        query.setInt(1, cid)
                        query.setInt(2, fid)
                        Result.Success(query.execute())
                    } else Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun createGenreCol(): Result<List<Genre>> {
        var data: Result<List<Genre>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val genreOfContentDao =
                            DaoManager.createDao(connectionSource, GenreOfContent::class.java)
                        val genreOfContentQueryBuilder = genreOfContentDao.queryBuilder()
                        genreOfContentQueryBuilder.selectColumns("genre_id")
                        genreOfContentQueryBuilder.distinct()
                        val genreDao = DaoManager.createDao(connectionSource, Genre::class.java)
                        val genreQueryBuilder = genreDao.queryBuilder()
                        genreQueryBuilder.where().`in`("genre_id", genreOfContentQueryBuilder)
                        val res = genreDao.query(genreQueryBuilder.prepare())
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 32"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getPerson(id: Int): Result<List<Celebrity>> {
        var data: Result<List<Celebrity>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val celebrityDao =
                            DaoManager.createDao(connectionSource, Celebrity::class.java)
                        val res = celebrityDao.queryForEq("cid", id)
                        Result.Success(res)
                    } else Result.Error(Exception("Ошибка запроса 33"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getReviews(
        id: Int, limit: Int, offset: Int, preview: Boolean = false
    ): Result<List<Review>> {
        var data: Result<List<Review>>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val reviewDao = DaoManager.createDao(connectionSource, Review::class.java)
                        val reviewQueryBuilder = reviewDao.queryBuilder()
                        val userDao = DaoManager.createDao(connectionSource, SiteUser::class.java)
                        val userQueryBuilder = userDao.queryBuilder()
                        reviewQueryBuilder.leftJoin(userQueryBuilder)
                        reviewQueryBuilder.selectRaw("\"site_user\".nickname", "\"review\".*")
                        reviewQueryBuilder.where().eq("content_id", id)
                        if (preview) {
                            reviewQueryBuilder.orderByRaw("login = current_user DESC")
                        }
                        reviewQueryBuilder.orderBy("rev_date", false)
                        reviewQueryBuilder.limit(limit.toLong())
                        reviewQueryBuilder.offset(offset.toLong())
                        val result = reviewDao.queryRaw(
                            reviewQueryBuilder.prepareStatementString(),
                            { names, columns ->
                                val review = if (columns != null) {
                                    Review(
                                        id = columns[1].toInt(),
                                        description = columns[2],
                                        date = Date.valueOf(columns[3]),
                                        title = columns[6],
                                        opinion = columns[7].toInt(),
                                        content = Content(
                                            id = columns[4].toInt()
                                        ),
                                        user = SiteUser(
                                            id = columns[5].toInt(), _nickname = columns[0]
                                        )
                                    )
                                } else Review()
                                review
                            })
                        Result.Success(result.results)
                    } else Result.Error(Exception("Ошибка запроса 34"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getReviewById(rid: Int): Result<Review> {
        var data: Result<Review>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection?.isValid(0) == true) {
                        val reviewDao = DaoManager.createDao(connectionSource, Review::class.java)
                        val reviewQueryBuilder = reviewDao.queryBuilder()
                        val userDao = DaoManager.createDao(connectionSource, SiteUser::class.java)
                        val userQueryBuilder = userDao.queryBuilder()
                        reviewQueryBuilder.leftJoin(userQueryBuilder)
                        reviewQueryBuilder.selectRaw("\"site_user\".nickname", "\"review\".*")
                        reviewQueryBuilder.where().eq("rid", rid)
                        val result = reviewDao.queryRaw(
                            reviewQueryBuilder.prepareStatementString(),
                            { names, columns ->
                                val review = if (columns != null) {
                                    Review(
                                        id = columns[1].toInt(),
                                        description = columns[2],
                                        date = Date.valueOf(columns[3]),
                                        title = columns[6],
                                        opinion = columns[7].toInt()
                                    ).apply {
                                        content = Content(
                                            id = columns[4].toInt()
                                        )
                                        user = SiteUser(
                                            id = columns[5].toInt(), _nickname = columns[0]
                                        )
                                    }
                                } else Review()
                                review
                            })
                        val res = result.results
                        if (res.size == 0)
                            Result.Error(Exception("Ошибка запроса 35"))
                        else {
                            Result.Success(res.first())
                        }
                    } else Result.Error(Exception("Ошибка запроса 35"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun closeConnection(): Result<String> {
        var message: Result<String> = Result.Success("Closed")
        withContext(Dispatchers.IO) {
            mutex.withLock {
                try {
                    if (::connectionSource.isInitialized) connectionSource.close()
                } catch (e: PSQLException) {
                    message = Result.Error(e)
                }
            }
        }
        return message
    }
}