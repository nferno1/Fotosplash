package nferno1.fotosplash.repository

import android.content.Context
import android.widget.Toast
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import nferno1.fotosplash.R
import nferno1.fotosplash.api.UnsplashApi
import nferno1.fotosplash.api.UnsplashResource
import nferno1.fotosplash.api.UnsplashResponseHandler
import nferno1.fotosplash.dao.*
import nferno1.fotosplash.data.*
import nferno1.fotosplash.data.collections.Collections
import nferno1.fotosplash.data.collections.CoverPhoto
import nferno1.fotosplash.data.database.MyPhotos
import nferno1.fotosplash.data.database.PhotosLikesMe
import nferno1.fotosplash.data.database.TopPhotos
import nferno1.fotosplash.data.database.User
import nferno1.fotosplash.data.download.Download
import nferno1.fotosplash.data.me.Me
import nferno1.fotosplash.data.me.ProfileImage
import nferno1.fotosplash.utils.Constants.ACCESS_KEY
import nferno1.fotosplash.utils.Constants.GEO
import nferno1.fotosplash.utils.Constants.SECRET
import nferno1.fotosplash.utils.Constants.redirectURI
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UnsplashRepository @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private var responseHandler: UnsplashResponseHandler,
    private val collectionsDao: CollectionsDao,
    private val myCollectionsDao: MyCollectionsDao,
    private val myPhotosDao: MyPhotosDao,
    private val photosDao: PhotosDao,
    private val photosLikesMeDao: PhotosLikesMeDao,
    private val userDao: UserDao,
    private val topPhotosDao: TopPhotosDao,
    val context: Context,
    private val collectionsPhotoDao: CollectionsPhotoDao,
) {

    fun getSearchResults(query: String, token: String = "Client-ID $ACCESS_KEY") =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 60,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UnsplashPagingSource(
                    unsplashApi,
                    query,
                    token,
                    photosDao,
                    context
                )
            }
        ).liveData


    fun getCollections(token: String) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UnsplashPagingCollections(
                    unsplashApi,
                    token,
                    collectionsDao,
                    context
                )
            }
        ).liveData

    fun getToken(
        code: String?,
    ): Call<Token> {
        return unsplashApi.getToken(
            "https://unsplash.com/oauth/token",
            ACCESS_KEY, SECRET, redirectURI, code, "authorization_code"
        )
    }


    suspend fun getMe(token: String): Me? {
        val result = try {
            val res = unsplashApi.getMe("Bearer $token")

            val user = User(
                id = res.id!!,
                name = res.name ?: "",
                userName = res.username!!,
                twitterUserName = res.twitterUsername ?: "",
                profileImageLarge = res.profileImage!!.large ?: "",
                followersCount = res.followersCount ?: 0,
                location = res.location ?: "",
                email = res.email ?: "",
                downloads = res.downloads ?: 0,
                totalPhotos = res.totalPhotos ?: 0,
                totalLikes = res.totalLikes ?: 0,
                totalCollections = res.totalCollections ?: 0,
            )
            userDao.insertUser(user)
            responseHandler.handleSuccess(res)
        } catch (e: Exception) {
            val res = userDao.getUser()
            if (res == null) {
                responseHandler.handleException(e)
            } else {
                val me = Me(
                    id = res.id,
                    name = res.name,
                    username = res.userName,
                    twitterUsername = res.twitterUserName,
                    profileImage = ProfileImage(large = res.profileImageLarge),
                    followersCount = res.followersCount,
                    location = res.location,
                    email = res.email,
                    downloads = res.downloads,
                    totalPhotos = res.totalPhotos,
                    totalLikes = res.totalLikes,
                    totalCollections = res.totalCollections,
                )
                Toast.makeText(context, R.string.cash, Toast.LENGTH_LONG).show()
                responseHandler.handleSuccess(me)

            }

        }

        return if (result is UnsplashResource.Success) {
            result.data
        } else {
            null
        }
    }

    suspend fun getMeLikes(username: String, token: String, items: String): List<Results>? {
        val result = try {
            val res = unsplashApi.getMeLikes(username, "Bearer $token", items)
            res.forEach {
                val tags = mutableListOf<String>()
                it.tags.forEach { tag ->
                    tags.add(tag.title ?: "")
                }
                if (items == "likes") {
                    val photosLikesMe = PhotosLikesMe(
                        id = it.id!!,
                        name = it.user!!.name ?: "",
                        userName = it.user!!.username ?: "",
                        description = it.description ?: "",
                        urlsRegular = it.urls!!.regular ?: "",
                        urlsFull = it.urls!!.full ?: "",
                        likes = it.likes ?: 0,
                        likedByUser = it.likedByUser ?: false,
                        userProfileImageSmall = it.user!!.profileImage!!.small ?: "",
                        urlsRaw = it.urls!!.raw ?: "",
                        bio = it.user!!.bio ?: "",
                        location = it.user!!.location ?: "",
                        altDescription = it.altDescription ?: "",
                        tags = tags.joinToString("#")
                    )
                    photosLikesMeDao.insertPhotosLikesMe(photosLikesMe)
                }
                if (items == "photos") {
                    val myPhotos = MyPhotos(
                        id = it.id!!,
                        name = it.user!!.name ?: "",
                        userName = it.user!!.username ?: "",
                        description = it.description ?: "",
                        urlsRegular = it.urls!!.regular ?: "",
                        urlsFull = it.urls!!.full ?: "",
                        likes = it.likes ?: 0,
                        likedByUser = it.likedByUser ?: false,
                        userProfileImageSmall = it.user!!.profileImage!!.small ?: "",
                        urlsRaw = it.urls!!.raw ?: "",
                        bio = it.user!!.bio ?: "",
                        location = it.user!!.location ?: "",
                        altDescription = it.altDescription ?: "",
                        tags = tags.joinToString("#")
                    )
                    myPhotosDao.insertMyPhotos(myPhotos)
                }
            }
            responseHandler.handleSuccess(res)
        } catch (e: Exception) {
            if (items == "likes") {
                val res = photosLikesMeDao.getPhotosLikesMe()

                if (res.isEmpty()) {
                    responseHandler.handleException(e)
                } else {
                    val photo = mutableListOf<Results>()
                    res.forEach {
                        val tags = mutableListOf<Tags>()
                        it.tags.split("#").forEach { title ->
                            tags.add(Tags(title = title))
                        }
                        photo.add(
                            Results(
                                id = it.id,
                                user = User(
                                    username = it.userName,
                                    name = it.name,
                                    profileImage = nferno1.fotosplash.data.ProfileImage(small = it.userProfileImageSmall),
                                    bio = it.bio,
                                    location = it.location
                                ),
                                description = it.description,
                                altDescription = it.altDescription,
                                urls = Urls(
                                    regular = it.urlsRegular,
                                    full = it.urlsFull,
                                    raw = it.urlsRaw
                                ),
                                likes = it.likes,
                                likedByUser = it.likedByUser,
                                tags = ArrayList(tags)
                            )
                        )
                    }
                    responseHandler.handleSuccess(photo)
                }

            } else {
                val res = myPhotosDao.getMyPhotos()

                if (res.isEmpty()) {
                    responseHandler.handleException(e)
                } else {
                    val photo = mutableListOf<Results>()
                    res.forEach {
                        photo.add(
                            Results(
                                id = it.id,
                                user = User(
                                    name = it.name,
                                    username = it.userName,
                                    profileImage = nferno1.fotosplash.data.ProfileImage(
                                        small = it.userProfileImageSmall
                                    )
                                ),
                                description = it.description,
                                urls = Urls(regular = it.urlsRegular, full = it.urlsFull),
                                likes = it.likes,
                                likedByUser = it.likedByUser
                            )
                        )
                    }
                    responseHandler.handleSuccess(photo)
                }
            }
        }
        return if (result is UnsplashResource.Success) {
            result.data
        } else {
            null
        }
    }

    suspend fun liked(id: String, token: String, like: Boolean) {
        when (like) {
            true -> unsplashApi.postLike(id, "Bearer $token")
            else -> unsplashApi.deleteLike(id, "Bearer $token")
        }
    }


    suspend fun allDeleteInDB() {
        collectionsDao.allDeleteCollections()
        collectionsPhotoDao.allDeleteCollectionsPhotos()
        myCollectionsDao.allDeleteMyCollections()
        myPhotosDao.allDeleteMyPhotos()
        photosDao.allDeletePhotos()
        photosLikesMeDao.allDeletePhotosLikesMe()
        userDao.allDeleteUser()
    }

    suspend fun getMeCollections(username: String, token: String): List<Collections>? {
        val result = try {
            val res = unsplashApi.getMeCollections(username, "Bearer $token")
            res.forEach {
                val collections = nferno1.fotosplash.data.database.MyCollections(
                    id = it.id!!,
                    title = it.title ?: "",
                    totalPhotos = it.totalPhotos ?: 0,
                    name = it.user!!.name ?: "",
                    userName = it.user!!.username!!,
                    description = it.description ?: "",
                    urlsRegular = it.coverPhoto!!.urls!!.regular!!,
                    urlsFull = it.coverPhoto!!.urls!!.full!!,
                    coverPhotoLikes = it.coverPhoto!!.likes!!,
                    coverPhotoLikedByUser = it.coverPhoto!!.likedByUser!!,
                    profileImageMedium = it.user!!.profileImage!!.medium.toString()
                )
                myCollectionsDao.insertMyCollections(collections)
            }
            responseHandler.handleSuccess(res)
        } catch (e: Exception) {
            val res = myCollectionsDao.getMyCollections()
            if (res.isEmpty()) {
                responseHandler.handleException(e)
            } else {
                val myCollections = mutableListOf<Collections>()
                res.forEach {
                    myCollections.add(
                        Collections(
                            id = it.id,
                            title = it.title,
                            totalPhotos = it.totalPhotos,
                            user = nferno1.fotosplash.data.collections.User(
                                username = it.userName,
                                name = it.name,
                                profileImage = nferno1.fotosplash.data.collections.ProfileImage(
                                    medium = it.profileImageMedium
                                )
                            ),
                            description = it.description,
                            coverPhoto = CoverPhoto(
                                urls = nferno1.fotosplash.data.collections.Urls(
                                    regular = it.urlsRegular,
                                    full = it.urlsFull
                                ),
                                likes = it.coverPhotoLikes,
                                likedByUser = it.coverPhotoLikedByUser
                            ),
                        )
                    )
                }
                responseHandler.handleSuccess(myCollections)
            }
        }

        return if (result is UnsplashResource.Success) {
            result.data
        } else {
            null
        }
    }

    suspend fun likedFromMyLikesPhoto(id: String, token: String, photo: Results?) {
        unsplashApi.deleteLike(id, "Bearer $token")
        val tags = mutableListOf<String>()
        photo!!.tags.forEach { tag ->
            tags.add(tag.title ?: "")
        }
        val photosLikesMe = PhotosLikesMe(
            id = photo.id!!,
            name = photo.user!!.name ?: "",
            userName = photo.user!!.username ?: "",
            description = photo.description ?: "",
            urlsFull = photo.urls!!.full ?: "",
            urlsRegular = photo.urls!!.regular ?: "",
            likes = photo.likes ?: 0,
            likedByUser = photo.likedByUser ?: false,
            userProfileImageSmall = photo.user!!.profileImage!!.small ?: "",
            urlsRaw = photo.urls!!.raw ?: "",
            bio = photo.user!!.bio ?: "",
            location = photo.user!!.location ?: "",
            altDescription = photo.altDescription ?: "",
            tags = tags.joinToString("#")
        )
        photosLikesMeDao.deletePhotosLikesMe(photosLikesMe)
    }

    fun getPhotoFromCollection(id: String, token: String) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UnsplashPagingCollectionsPhoto(
                    unsplashApi,
                    id,
                    token,
                    collectionsPhotoDao
                )
            }
        ).liveData

    suspend fun getPhotoDetails(id: String, token: String): Results? {


        val result = try {
            val res = unsplashApi.getPhotosDetails(id, "Bearer $token")
            val tags = mutableListOf<String>()
            res.tags.forEach {
                tags.add(it.title ?: "")
            }
            val photo = TopPhotos(
                id = res.id!!,
                name = res.user!!.name ?: "",
                userName = res.user!!.username!!,
                bio = res.user!!.bio ?: "",
                locationName = res.location!!.name ?: "",
                locationCity = res.location!!.city ?: "",
                locationCountry = res.location!!.country ?: "",
                locationLat = res.location!!.position!!.latitude ?: "0",
                locationLon = res.location!!.position!!.longitude ?: "0",
                description = res.description ?: "",
                altDescription = res.altDescription ?: "",
                urlsRegular = res.urls!!.regular ?: "",
                urlsFull = res.urls!!.full ?: "",
                urlsRaw = res.urls!!.raw ?: "",
                likes = res.likes ?: 0,
                likedByUser = res.likedByUser ?: false,
                userProfileImageSmall = res.user!!.profileImage!!.small ?: "",
                tags = tags.joinToString("#"),
                downloads = res.downloads ?: 0,
                exifMake = res.exif!!.make ?: "",
                exifModel = res.exif!!.model ?: "",
                exifName = res.exif!!.name ?: "",
                exifExposureTime = res.exif!!.exposureTime ?: "",
                exifAperture = res.exif!!.aperture ?: "",
                exifFocalLength = res.exif!!.focalLength ?: "",
                exifIso = res.exif!!.iso ?: "",
            )
            topPhotosDao.insertTopPhoto(photo)
            responseHandler.handleSuccess(res)
        } catch (e: Exception) {
            val resList = topPhotosDao.getTopPhotos(id)
            if (resList.isEmpty()) responseHandler.handleException(e)
            else {
                val res = resList.first()
                val tags = mutableListOf<Tags>()
                res.tags.split("#").forEach { title ->
                    tags.add(Tags(title = title))
                }
                val photo = Results(
                    id = res.id,
                    user = User(
                        name = res.name,
                        username = res.userName,
                        bio = res.bio,
                        profileImage = nferno1.fotosplash.data.ProfileImage(small = res.userProfileImageSmall)
                    ),
                    location = Location(
                        name = res.locationName,
                        city = res.locationCity,
                        country = res.locationCountry,
                        position = Position(latitude = res.locationLat, longitude = res.locationLon)
                    ),
                    description = res.description,
                    altDescription = res.altDescription,
                    urls = Urls(regular = res.urlsRegular, raw = res.urlsRaw, full = res.urlsFull),
                    likes = res.likes,
                    likedByUser = res.likedByUser,
                    tags = ArrayList(tags),
                    downloads = res.downloads,
                    exif = Exif(
                        make = res.exifMake,
                        model = res.exifModel,
                        name = res.exifName,
                        exposureTime = res.exifExposureTime,
                        aperture = res.exifAperture,
                        focalLength = res.exifFocalLength,
                        iso = res.exifIso
                    )
                )
                responseHandler.handleSuccess(photo)
            }

        }

        return if (result is UnsplashResource.Success) {
            result.data
        } else {
            null
        }

    }

    suspend fun getDownload(id: String, token: String): Download? {
        val result = try {
            val res = unsplashApi.getDownload(id, "Bearer $token")
            responseHandler.handleSuccess(res)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }

        return if (result is UnsplashResource.Success) {
            result.data
        } else {
            null
        }
    }

    fun location(location: Location?): String {
        val lon: String
        val lat: String
        if (location == null) return ""
        else {
            if (location.position!!.latitude != null && location.position!!.longitude != null) {
                lat = location.position!!.latitude!!
                lon = location.position!!.longitude!!
                return "geo:0,0?q=$lat,$lon(${location.name ?: ""})"
            } else {
                return if (location.country == null) {
                    if (location.city == null) {
                        if (location.name == null) {
                            ""
                        } else {
                            "$GEO${location.name}"
                        }
                    } else {
                        "$GEO${location.name}+${location.city}"
                    }
                } else {
                    "$GEO${location.name}+${location.city}+${location.country}"
                }
            }
        }
    }
}