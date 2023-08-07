package nferno1.fotosplash.api

import nferno1.fotosplash.data.Results
import nferno1.fotosplash.data.Token
import nferno1.fotosplash.data.UnsplashSearchPhotoResponse
import nferno1.fotosplash.data.collections.Collections
import nferno1.fotosplash.data.download.Download
import nferno1.fotosplash.data.me.Me
import retrofit2.Call
import retrofit2.http.*

interface UnsplashApi {
    @GET("search/photos")
    suspend fun searchPhotos(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Accept-Version") acceptVersion: String = "v1",
    ): UnsplashSearchPhotoResponse

    @GET("/photos")
    suspend fun getPhotos(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<Results>

    @GET("/photos/{id}")
    suspend fun getPhotosDetails(
        @Path("id") id: String,
        @Header("Authorization") authorization: String,
    ): Results

    @GET("/photos/{id}/download")
    suspend fun getDownload(
        @Path("id") id: String,
        @Header("Authorization") authorization: String,
    ): Download

    @GET("/collections")
    suspend fun getCollections(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<Collections>

    @GET("me")
    suspend fun getMe(
        @Header("Authorization") authorization: String,
    ): Me

    @POST
    fun getToken(
        @Url url: String,
        @Query("client_id") clientID: String,
        @Query("client_secret") clientSecret: String,
        @Query("redirect_uri") redirectURI: String,
        @Query("code") code: String?,
        @Query("grant_type") grantType: String,
    ): Call<Token>

    @POST("photos/{id}/like")
    suspend fun postLike(
        @Path("id") id: String,
        @Header("Authorization") authorization: String,
    )

    @DELETE("photos/{id}/like")
    suspend fun deleteLike(
        @Path("id") id: String,
        @Header("Authorization") authorization: String,
    )

    @GET("/users/{username}/{items}")
    suspend fun getMeLikes(
        @Path("username") username: String,
        @Header("Authorization") authorization: String,
        @Path("items") items: String = "likes"
    ): List<Results>

    @GET("/users/{username}/collections")
    suspend fun getMeCollections(
        @Path("username") username: String,
        @Header("Authorization") authorization: String
    ): List<Collections>

    @GET("/collections/{id}/photos")
    suspend fun getPhotoFromCollection(
        @Path("id") id: String,
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<Results>

}

