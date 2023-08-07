package nferno1.fotosplash.repository

import android.content.Context
import android.widget.Toast
import androidx.paging.PagingSource
import androidx.paging.PagingState
import nferno1.fotosplash.R
import nferno1.fotosplash.api.UnsplashApi
import nferno1.fotosplash.dao.PhotosDao
import nferno1.fotosplash.data.*
import nferno1.fotosplash.data.database.Photos

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String,
    private val token: String,
    private val photosDao: PhotosDao,
    private val context: Context,
) : PagingSource<Int, Results>() {
    override fun getRefreshKey(state: PagingState<Int, Results>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Results> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX
        var res: LoadResult<Int, Results>
        try {
            val photos: List<Results> = if (query != "") {
                val response =
                    unsplashApi.searchPhotos("Bearer $token", query, position, params.loadSize)
                response.results
            } else {
                unsplashApi.getPhotos("Bearer $token", position, params.loadSize)
            }

            photos.forEach {
                val tags = mutableListOf<String>()
                it.tags.forEach { tag ->
                    tags.add(tag.title ?: "")
                }
                val photo = Photos(
                    id = it.id!!,
                    name = it.user!!.name ?: "",
                    userName = it.user!!.username!!,
                    description = it.description ?: "",
                    urlsRegular = it.urls!!.regular!!,
                    urlsFull = it.urls!!.full!!,
                    likes = it.likes!!,
                    likedByUser = it.likedByUser!!,
                    userProfileImageSmall = it.user!!.profileImage!!.small ?: "",
                    urlsRaw = it.urls!!.raw ?: "",
                    bio = it.user!!.bio ?: "",
                    location = it.user!!.location ?: "",
                    altDescription = it.altDescription ?: "",
                    tags = tags.joinToString("#")
                )
                photosDao.insertPhotos(photo)
            }
            res = LoadResult.Page(
                data = photos,
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )

        } catch (e: Exception) {
            val photosFromDB = photosDao.getPhotos()
            val photos = mutableListOf<Results>()
            photosFromDB.forEach {
                val tags = mutableListOf<Tags>()
                it.tags.split("#").forEach { title ->
                    tags.add(Tags(title = title))
                }
                photos.add(
                    Results(
                        id = it.id,
                        user = User(
                            username = it.userName,
                            name = it.name,
                            profileImage = ProfileImage(small = it.userProfileImageSmall),
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
            Toast.makeText(context, R.string.cash, Toast.LENGTH_LONG).show()
            res = LoadResult.Page(
                data = photos,
                null, null
            )
            if (photosFromDB.isEmpty()) res = LoadResult.Error(e)
        }
        return res
    }
}