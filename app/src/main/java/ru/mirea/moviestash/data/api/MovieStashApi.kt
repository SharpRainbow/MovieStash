package ru.mirea.moviestash.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mirea.moviestash.data.api.dto.AddReviewDto
import ru.mirea.moviestash.data.api.dto.AddUserStarDto
import ru.mirea.moviestash.data.api.dto.BanRequestDto
import ru.mirea.moviestash.data.api.dto.BannedUserDto
import ru.mirea.moviestash.data.api.dto.CelebrityDto
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.data.api.dto.CollectionDto
import ru.mirea.moviestash.data.api.dto.ContentDto
import ru.mirea.moviestash.data.api.dto.CreateCollectionDto
import ru.mirea.moviestash.data.api.dto.CredentialsDto
import ru.mirea.moviestash.data.api.dto.GenreDto
import ru.mirea.moviestash.data.api.dto.NewsDto
import ru.mirea.moviestash.data.api.dto.OpinionDto
import ru.mirea.moviestash.data.api.dto.RegisterDto
import ru.mirea.moviestash.data.api.dto.ReviewDto
import ru.mirea.moviestash.data.api.dto.TokenDto
import ru.mirea.moviestash.data.api.dto.UpdateReviewDto
import ru.mirea.moviestash.data.api.dto.UpdateUserDto
import ru.mirea.moviestash.data.api.dto.UserDto
import ru.mirea.moviestash.data.api.dto.UserStarDto

interface MovieStashApi {

    @GET("celebrities")
    suspend fun getCelebrities(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("name") name: String = ""
    ): List<CelebrityDto>

    @GET("celebrities/{id}")
    suspend fun getCelebrityById(
        @Path("id") id: Int
    ): CelebrityDto

    @GET("celebrities/{id}/contents")
    suspend fun getContentsByCelebrityId(
        @Path("id") celebrityId: Int,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
    ): List<ContentDto>

    @GET("collections")
    suspend fun getPublicCollections(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<CollectionDto>

    @GET("collections/personal")
    suspend fun getPersonalCollections(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): List<CollectionDto>

    @POST("collections/personal")
    suspend fun addPersonalCollection(
        @Header("Authorization") token: String,
        @Body createCollectionDto: CreateCollectionDto
    )

    @GET("collections/personal/{id}")
    suspend fun getPersonalCollectionById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): CollectionDto

    @DELETE("collections/personal/{id}")
    suspend fun deletePersonalCollection(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

    @PATCH("collections/personal/{id}")
    suspend fun updatePersonalCollection(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body createCollectionDto: CreateCollectionDto
    )

    @GET("collections/personal/{id}/contents")
    suspend fun getContentsFromUserCollection(
        @Header("Authorization") token: String,
        @Path("id") collectionId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<ContentDto>

    @POST("collections/personal/{id}/contents")
    suspend fun addContentToPersonalCollection(
        @Header("Authorization") token: String,
        @Path("id") collectionId: Int,
        @Query("content") contentId: Int
    )

    @DELETE("collections/personal/{id}/contents")
    suspend fun deleteContentFromPersonalCollection(
        @Header("Authorization") token: String,
        @Path("id") collectionId: Int,
        @Query("content") contentId: Int
    )

    @PATCH("collections/personal/{id}/own")
    suspend fun takeOwnershipOfCollection(
        @Header("Authorization") token: String,
        @Path("id") collectionId: Int
    )

    @PATCH("collections/personal/{id}/publish")
    suspend fun publishCollection(
        @Header("Authorization") token: String,
        @Path("id") collectionId: Int
    )

    @GET("collections/{id}")
    suspend fun getPublicCollectionInfoById(
        @Path("id") collectionId: Int
    ): CollectionDto

    @GET("collections/{id}/contents")
    suspend fun getContentsFromPublicCollection(
        @Path("id") collectionId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<ContentDto>

    @GET("contents")
    suspend fun getContents(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("name") name: String? = null,
        @Query("genre") genre: Int = -1,
    ): List<ContentDto>

    @GET("contents/best")
    suspend fun getBestContents(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): List<ContentDto>

    @GET("contents/{id}")
    suspend fun getContentById(
        @Path("id") contentId: Int
    ): ContentDto

    @GET("contents/{id}/celebrities")
    suspend fun getCelebrityByContentId(
        @Path("id") contentId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<CelebrityInContentDto>

    @GET("contents/{id}/cast")
    suspend fun getCastByContentId(
        @Path("id") contentId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<CelebrityInContentDto>

    @GET("contents/{id}/crew")
    suspend fun getCrewByContentId(
        @Path("id") contentId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<CelebrityInContentDto>

    @GET("contents/{id}/reviews")
    suspend fun getReviewsByContentId(
        @Path("id") contentId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<ReviewDto>

    @GET("genres")
    suspend fun getGenres(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<GenreDto>

    @GET("genres/present")
    suspend fun getPresentGenres(): List<GenreDto>

    @GET("genres/{id}")
    suspend fun getGenreById(
        @Path("id") genreId: Int
    ): GenreDto

    @POST("login")
    suspend fun login(
        @Body credentials: CredentialsDto
    ): TokenDto

    @POST("register")
    suspend fun register(
        @Body registerDto: RegisterDto
    )

    @GET("news")
    suspend fun getNews(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<NewsDto>

    @Multipart
    @POST("news")
    suspend fun addNews(
        @Header("Authorization") token: String,
        @PartMap partMap: @JvmSuppressWildcards Map<String, RequestBody>,
        @Part image: MultipartBody.Part? = null
    )

    @GET("news/{id}")
    suspend fun getNewsById(
        @Path("id") newsId: Int
    ): NewsDto

    @DELETE("news/{id}")
    suspend fun deleteNews(
        @Header("Authorization") token: String,
        @Path("id") newsId: Int
    )

    @Multipart
    @PATCH("news/{id}")
    suspend fun updateNews(
        @Header("Authorization") token: String,
        @Path("id") newsId: Int,
        @PartMap partMap: @JvmSuppressWildcards Map<String, RequestBody>,
        @Part image: MultipartBody.Part? = null
    )

    @GET("opinions")
    suspend fun getOpinions(): List<OpinionDto>

    @POST("reviews")
    suspend fun addReview(
        @Header("Authorization") token: String,
        @Body reviewDto: AddReviewDto
    )

    @GET("reviews/{id}")
    suspend fun getReviewById(
        @Path("id") reviewId: Int
    ): ReviewDto

    @DELETE("reviews/{id}")
    suspend fun deleteReview(
        @Header("Authorization") token: String,
        @Path("id") reviewId: Int
    )

    @PATCH("reviews/{id}")
    suspend fun updateReview(
        @Header("Authorization") token: String,
        @Path("id") reviewId: Int,
        @Body reviewDto: UpdateReviewDto
    )

    @GET("stars")
    suspend fun getUserStarByContentId(
        @Header("Authorization") token: String,
        @Query("content_id") contentId: Int,
    ): UserStarDto

    @POST("stars")
    suspend fun addUserStar(
        @Header("Authorization") token: String,
        @Body userStarDto: AddUserStarDto
    )

    @DELETE("stars/{id}")
    suspend fun deleteUserStar(
        @Header("Authorization") token: String,
        @Path("id") starId: Int
    )

    @PATCH("stars/{id}")
    suspend fun updateUserStar(
        @Header("Authorization") token: String,
        @Path("id") starId: Int,
        @Query("rating") rating: Int
    )

    @GET("users/banned")
    suspend fun getBannedUsers(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<BannedUserDto>

    @GET("users/personal")
    suspend fun getCurrentUserData(
        @Header("Authorization") token: String,
    ): UserDto

    @PATCH("users/personal")
    suspend fun updateUserData(
        @Header("Authorization") token: String,
        @Body userDto: UpdateUserDto
    )

    @PATCH("users/{id}/ban")
    suspend fun banUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body banReason: BanRequestDto
    )

    @PATCH("users/{id}/unban")
    suspend fun unbanUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
    )
}