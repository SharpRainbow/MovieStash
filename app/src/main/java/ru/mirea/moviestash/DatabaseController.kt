package ru.mirea.moviestash

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.postgresql.util.PSQLException
import ru.mirea.moviestash.Crypto.decrypt
import ru.mirea.moviestash.Crypto.encrypt
import ru.mirea.moviestash.Crypto.getShaHash
import ru.mirea.moviestash.Crypto.toHex
import ru.mirea.moviestash.userManagment.LoggedUser
import java.security.MessageDigest
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Types

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

object DatabaseController {
    private const val jdbcUrl =
        "jdbc:postgresql://dpg-ce74kcqrrk01au196kpg-a.frankfurt-postgres.render.com:5432/cinema_u49e"
    private const val defUserLogin = "no_login_user"
    private const val defUserPass = "d82036d9397089c870c8bccf127b1beaa335269147e6223fab3f38e5d506df8b"
    private val mutex = Mutex()
    private lateinit var connection: Connection
    private var sPassword: String = defUserPass
    private var sLogin: String = defUserLogin
    var user: LoggedUser? = null

    fun logOut(){
        user = null
        sLogin = defUserLogin
        sPassword = defUserPass
    }

    suspend fun login(login: String = sLogin, password: String
    = sPassword): Result<String> {
        var message: Result<String>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                try {
                    var realPass = password
                    if (realPass != defUserPass) {
                        realPass = getShaHash(password, login)
                        sPassword = password
                        sLogin = login
                    }
                    if (::connection.isInitialized && !connection.isClosed)
                        connection.close()
                    connection = DriverManager.getConnection(
                        jdbcUrl, login,
                        realPass
                    )
                    message = if (connection.isValid(0)) {
                        if (login != defUserLogin) {
                            val query = connection.prepareStatement("SELECT uid, nickname, email " +
                                    "FROM site_user WHERE login = ?")
                            query.setString(1, login)
                            val dataSet = query.executeQuery()
                            if (dataSet.next()){
                                user = LoggedUser(dataSet.getInt("uid"), login,
                                    dataSet.getString("email"), dataSet.getString("nickname"))
                            }
                            //id = dataSet.getInt("employee_id")
                            //manager = when(isManager()){
                            //    Result.Success(true) -> true
                            //    else -> false
                            //}
                        }
                        Result.Success("Вход выполнен!")
                    } else
                        Result.Error(Exception("Ошибка соединения"))
                } catch (e: PSQLException) {
                    message = Result.Error(e)
                }
            }
        }
        return message
    }



    suspend fun registerNewUser(login: String,
                                password: String, username: String, email: String): Result<Boolean> {
        var message: Result<Boolean>
        mutex.withLock {
            withContext(Dispatchers.IO) {
                if (connection.isValid(0)) {
                    try {
                        var query = connection.prepareStatement("SELECT start_registration(?, ?)")
                        query.setString(1, login)
                        val bytes =
                            MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
                        query.setString(2, bytes.toHex())
                        val rs = query.executeQuery()
                        var hsh = ""
                        while (rs.next())
                            hsh = rs.getString("start_registration")
                        val randNum = decrypt(hsh, bytes)
                        query = connection.prepareCall("CALL register_user(?, ?, ?, ?)")
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
                } else
                    message = Result.Error(Exception("Ошибка соединения"))
            }
        }
        return message
    }

    suspend fun modUserData(nick: String, email: String): Result<Boolean> {
        var result: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                result = try {
                    if (connection.isValid(0)) {
                        connection.prepareCall("CALL update_user(?, ?, ?)").apply {
                            setString(1, nick)
                            setString(2, email)
                            setInt(3, user!!.uid)
                            execute()
                        }
                        Result.Success(true)
                    }
                    else
                        Result.Error(Exception("Ошибка запроса 1"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return result
    }

    suspend fun refreshUserData() {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                if (connection.isValid(0)) {
                    val query = connection.prepareStatement(
                        "SELECT nickname, email " +
                                "FROM site_user WHERE login = current_user"
                    )
                    val dataSet = query.executeQuery()
                    if (dataSet.next()) {
                        user?.nickname = dataSet.getString("nickname")
                        user?.email = dataSet.getString("email")
                    }
                }
            }
        }
    }

    suspend fun checkConnection(): Result<Boolean> {
        var valid: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                if (::connection. isInitialized && connection.isValid(0)) {
                    valid = Result.Success(true)
                    return@withContext
                }
                valid = try {
                    if (::connection.isInitialized)
                        connection.close()
                    var tmpPass = sPassword
                    if (tmpPass != defUserPass)
                        tmpPass = getShaHash(sPassword, sLogin)
                    connection = DriverManager.getConnection(jdbcUrl, sLogin, tmpPass)
                    if (sLogin != defUserLogin && user == null) {
                        val query = connection.prepareStatement(
                            "SELECT uid, nickname, email " +
                                    "FROM site_user WHERE login = ?"
                        )
                        query.setString(1, sLogin)
                        val dataSet = query.executeQuery()
                        if (dataSet.next()) {
                            user = LoggedUser(
                                dataSet.getInt("uid"), sLogin,
                                dataSet.getString("email"), dataSet.getString("nickname")
                            )
                        }
                    }
                    Result.Success(connection.isValid(0))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return valid
    }

    suspend fun searchForMovie(filmName: String, offset: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT * FROM content WHERE " +
                                "name ILIKE ? LIMIT 20 OFFSET ?")
                        query.setString(1, "%$filmName%")
                        query.setInt(2, offset)
                        Log.d("DEBUG", query.toString())
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 2"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun searchForPerson(pName: String, offset: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT cid, name, birthday," +
                                " img_link FROM celebrity WHERE name ILIKE ? LIMIT 20 OFFSET ?")
                        query.setString(1, "%$pName%")
                        query.setInt(2, offset)
                        Log.d("DEBUG", query.toString())
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 3"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getMainPageCont(): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.createStatement()
                        Result.Success(query.executeQuery("SELECT * FROM content ORDER BY RANDOM() LIMIT 6"))
                    } else
                        Result.Error(Exception("Ошибка запроса 4"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getTop(limit: Int, offset: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.
                        prepareStatement("SELECT * FROM best_films LIMIT ? OFFSET ?").apply {
                            setInt(1, limit)
                            setInt(2, offset)
                        }
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getLastNews(): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.createStatement()
                        Result.Success(query.executeQuery("SELECT * FROM recent_news"))
                    } else
                        Result.Error(Exception("Ошибка запроса 5"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getBannedUsers(): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.createStatement()
                        Result.Success(query.executeQuery("SELECT * FROM site_user WHERE is_banned = true"))
                    } else
                        Result.Error(Exception("Ошибка запроса 6"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun isBanned(): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.createStatement()
                        Result.Success(query.executeQuery("SELECT is_banned, " +
                                "ban_date, ban_reason FROM site_user WHERE login = current_user"))
                    } else
                        Result.Error(Exception("Ошибка запроса 7"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getRatingForContent(cid: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT rating FROM content WHERE " +
                                "content_id = ?")
                        query.setInt(1, cid)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 8"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getRating(cid: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        if (user == null)
            return Result.Error(Exception("Not logged in"))
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT * FROM user_stars WHERE " +
                                "content_id = ? AND uid = ?")
                        query.setInt(1, cid)
                        query.setInt(2, user!!.uid)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 9"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun setRating(id: Int, rating: Short, update: Boolean): Result<Boolean> {
        var data: Result<Boolean>
        if (user == null)
            return Result.Error(Exception("Войдите в аккаунт"))
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query =
                            if (rating.toInt() == 0){
                                connection.prepareCall("CALL delete_star(?)").apply {
                                    setInt(1, id)
                                }
                            }
                        else if (update)
                                connection.prepareCall("CALL update_star(?, ?)").apply {
                                    setInt(1, id)
                                    setShort(2, rating)
                                }
                        else
                            connection.prepareCall("CALL add_star(?, ?, ?)").apply {
                                setInt(1, id)
                                setInt(2, user!!.uid)
                                setShort(3, rating)
                            }
                        Result.Success(query.execute())
                    } else
                        Result.Error(Exception("Ошибка запроса 10"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }



    suspend fun getFilmByActor(aid: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT * FROM (SELECT DISTINCT ON " +
                                "(c.content_id) c.* FROM content c LEFT JOIN celebrity_in_content " +
                                "cic on c.content_id = cic.content_id WHERE cic.cid = ?) as " +
                                "content ORDER BY release_date DESC")
                        query.setInt(1, aid)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 11"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getGenres(filmId: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT g.name FROM " +
                                "genres_of_content LEFT JOIN genre g on g.genre_id = " +
                                "genres_of_content.genre_id WHERE content_id = ?")
                        query.setInt(1, filmId)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 12"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getCountries(filmId: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT c.name FROM " +
                                "countries_of_content LEFT JOIN country c on c.country_id " +
                                "= countries_of_content.country_id WHERE content_id = ?")
                        query.setInt(1, filmId)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 13"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getActors(filmId: Int, offset: Int, size: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT c.cid, c.name as name, " +
                                "rc.name as role, cic.description, c.img_link " +
                                "FROM celebrity c, celebrity_in_content cic, role_classifier rc " +
                                "WHERE cic.cid = c.cid AND cic.role = rc.role_id AND content_id" +
                                " = ? AND role = 4 ORDER BY priority LIMIT ? OFFSET ?")
                        query.setInt(1, filmId)
                        query.setInt(2, size)
                        query.setInt(3, offset)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 14"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getContentFromCollection(filmId: Int, offset: Int, size: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT c.* FROM " +
                                "content_in_collection cic LEFT JOIN content c on c.content_id " +
                                "= cic.content_id WHERE collection_id = ? ORDER BY cic.film_number " +
                                "LIMIT ? OFFSET ?")
                        query.setInt(1, filmId)
                        query.setInt(2, size)
                        query.setInt(3, offset)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 15"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getCreators(filmId: Int, offset: Int, size: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT c.cid, c.name as name, " +
                                "rc.name as role, cic.description, c.img_link " +
                                "FROM celebrity c, celebrity_in_content cic, role_classifier rc " +
                                "WHERE cic.cid = c.cid AND cic.role = rc.role_id AND content_id" +
                                " = ? AND role != 4 ORDER BY priority LIMIT ? OFFSET ?")
                        query.setInt(1, filmId)
                        query.setInt(2, size)
                        query.setInt(3, offset)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 16"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getMovieByGenre(genre: Int, offset: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT content.* FROM content " +
                                "LEFT JOIN genres_of_content g ON g.content_id = content.content_id " +
                                "LEFT JOIN genre gr ON gr.genre_id = g.genre_id WHERE gr.genre_id = ? " +
                                "ORDER BY content.content_id LIMIT 10 OFFSET ?")
                        query.setInt(1, genre)
                        query.setInt(2, offset)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 17"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getEditorCols(): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.createStatement()
                        Result.Success(query.executeQuery("SELECT * FROM collection " +
                                "WHERE uid is null"))
                    } else
                        Result.Error(Exception("Ошибка запроса 18"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getUserCols(uid: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT * FROM collection " +
                                "WHERE uid = ?")
                        query.setInt(1, uid)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 19"))
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
                    if (connection.isValid(0)) {
                        val query = connection.prepareCall("CALL delete_collection(?)")
                        query.setInt(1, cid)
                        query.execute()
                        Result.Success(true)
                    } else
                        Result.Error(Exception("Ошибка запроса"))
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
                    if (connection.isValid(0)) {
                        val query = connection.prepareCall("CALL delete_review(?)")
                        query.setInt(1, rid)
                        Result.Success(query.execute())
                    } else
                        Result.Error(Exception("Ошибка запроса"))
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
                    if (connection.isValid(0)) {
                        val query =
                            if (ban)
                                connection.prepareCall("CALL ban_user_by_id(?, ?)").apply {
                                    setInt(1, uid)
                                    setString(2, reason)
                                }
                            else
                                connection.prepareCall("CALL unban_user_by_id(?)").apply {
                                    setInt(1, uid)
                                }
                        Result.Success(query.execute())
                    } else
                        Result.Error(Exception("Ошибка запроса 22"))
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
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("UPDATE collection SET uid = null WHERE collection_id = ?")
                        query.setInt(1, cid)
                        query.executeUpdate()
                        Result.Success(true)
                    } else
                        Result.Error(Exception("Ошибка запроса 23"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun hideCols(cid: Int): Result<Boolean> {
        if (user == null)
            return Result.Error(Exception("Войдите в аккаунт"))
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("UPDATE collection SET uid = ? " +
                                "WHERE collection_id = ?").apply {
                            setInt(1, user!!.uid)
                            setInt(2, cid)
                        }
                        query.executeUpdate()
                        Log.d("DEBUG", query.toString())
                        Result.Success(true)
                    } else
                        Result.Error(Exception("Ошибка запроса 23"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun addModNews(title: String, description: String, id: Int, image: String?): Result<Boolean> {
        var data: Result<Boolean>
        if (user == null){
            return Result.Error(Exception("Для создания обзора надо войти в аккаунт!"))
        }
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query =
                            if (id != 0)
                                connection.prepareCall("CALL update_new(?, ?, ?, ?)").apply {
                                    setString(1, description)
                                    setString(2, title)
                                    setInt(3, id)
                                    if (image != null)
                                        setString(4, image)
                                    else
                                        setNull(4, Types.VARCHAR)
                                }
                            else
                                connection.prepareCall("CALL add_new(?, ?, ?, ?)").apply {
                                    setString(1, title)
                                    setString(2, description)
                                    setInt(3, user!!.uid)
                                    if (image != null)
                                        setString(4, image)
                                    else
                                        setNull(4, Types.VARCHAR)
                                }
                        Result.Success(query.execute())
                    } else
                        Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    if (e.message.toString().contains("duplicate key"))
                        Result.Error(Exception("Вы уже делали обзор на этот фильм"))
                    else
                        Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getNews(limit: Int, offset: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT * FROM news ORDER BY " +
                                "news_date DESC LIMIT ? OFFSET ?")
                        query.setInt(1, limit)
                        query.setInt(2, offset)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 25"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getNewsById(nid: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT * FROM news WHERE nid = ?")
                        query.setInt(1, nid)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 26"))
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
                    if (connection.isValid(0)) {
                        val query = connection.prepareCall("CALL delete_new(?)")
                        query.setInt(1, nid)
                        Result.Success(query.execute())
                    } else
                        Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun addModReview(title: String, description: String, id: Int, opinion: Int,
        update: Boolean): Result<Boolean> {
        var data: Result<Boolean>
        if (user == null){
            return Result.Error(Exception("Для создания обзора надо войти в аккаунт!"))
        }
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query =
                            if (update)
                                connection.prepareCall("CALL update_review(?, ?, ?, ?)").apply {
                                    setString(1, title)
                                    setString(2, description)
                                    setShort(3, opinion.toShort())
                                    setInt(4, id)
                                }
                            else
                                connection.prepareCall("CALL add_review(?, ?, ?, ?, ?)").apply {
                                    setString(1, title)
                                    setString(2, description)
                                    setInt(3, id)
                                    setInt(4, user!!.uid)
                                    setShort(5, opinion.toShort())
                                }
                        Result.Success(query.execute())
                    } else
                        Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    if (e.message.toString().contains("duplicate key"))
                        Result.Error(Exception("Вы уже делали обзор на этот фильм"))
                    else
                        Result.Error(e)
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
                    if (connection.isValid(0)) {
                        val query = connection.createStatement()
                        val result = query.executeQuery("SELECT pg_has_role(current_user, 'moderator', 'MEMBER')")
                        if (result.next()) {
                            Result.Success(result.getBoolean("pg_has_role"))
                        }
                        else
                            Result.Error(Exception("Запрос не вернул результатов"))
                    } else
                        Result.Error(Exception("Ошибка запроса 29"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun addModUserCols(uid: Int, name: String, description: String, update: Boolean): Result<Boolean> {
        var data: Result<Boolean>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query =
                            if (update)
                                connection.prepareCall("CALL update_collection(?, ?, ?)")
                            else
                                connection.prepareCall("CALL add_collection(?, ?, ?)")
                        query.setString(1, name)
                        query.setString(2, description)
                        query.setInt(3, uid)
                        Result.Success(query.execute())
                    } else
                        Result.Error(Exception("Ошибка запроса 30"))
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
                    if (connection.isValid(0)) {
                        val query =
                            if (add)
                                connection.prepareCall("CALL add_film_to_collection(?, ?)")
                            else
                                connection.prepareCall("CALL delete_film_from_collection(?, ?)")
                        query.setInt(1, cid)
                        query.setInt(2, fid)
                        Result.Success(query.execute())
                    } else
                        Result.Error(Exception("Ошибка запроса"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun createGenreCol(): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.createStatement()
                        Result.Success(query.executeQuery("SELECT * FROM genre WHERE " +
                                "(SELECT count(genre_id) FROM genres_of_content WHERE " +
                                "genres_of_content.genre_id = genre.genre_id) <> 0"))
                    } else
                        Result.Error(Exception("Ошибка запроса 32"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getPerson(id: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT * FROM celebrity WHERE cid = ?")
                        query.setInt(1, id)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 33"))
                } catch (e: PSQLException) {
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getReviews(id: Int, limit: Int, offset: Int, preview: Boolean = false): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query =
                            if (preview)
                                connection.prepareStatement("SELECT r.*, su.nickname FROM " +
                                        "review r LEFT JOIN site_user su on r.uid = su.uid " +
                                        "WHERE r.content_id = ? ORDER BY su.login = current_user " +
                                        "DESC, rev_date DESC LIMIT ? OFFSET ?")
                            else
                                connection.prepareStatement("SELECT r.*, su.nickname FROM " +
                                "review r LEFT JOIN site_user su on r.uid = su.uid " +
                                "WHERE r.content_id = ? ORDER BY rev_date DESC LIMIT ? OFFSET ?")
                        query.setInt(1, id)
                        query.setInt(2, limit)
                        query.setInt(3, offset)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 34"))
                } catch (e: PSQLException) {
                    Log.d("DEBUG", e.stackTraceToString())
                    Result.Error(e)
                }
            }
        }
        return data
    }

    suspend fun getReviewById(rid: Int): Result<ResultSet> {
        var data: Result<ResultSet>
        withContext(Dispatchers.IO) {
            mutex.withLock {
                data = try {
                    if (connection.isValid(0)) {
                        val query = connection.prepareStatement("SELECT r.*, su.nickname FROM " +
                                "review r LEFT JOIN site_user su on r.uid = su.uid " +
                                "WHERE r.rid = ?")
                        query.setInt(1, rid)
                        Result.Success(query.executeQuery())
                    } else
                        Result.Error(Exception("Ошибка запроса 35"))
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
                    if (::connection.isInitialized)
                        connection.close()
                } catch (e: PSQLException) {
                    message = Result.Error(e)
                }
            }
        }
        return message
    }
}