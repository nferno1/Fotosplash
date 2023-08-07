package nferno1.fotosplash.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import nferno1.fotosplash.api.UnsplashApi
import nferno1.fotosplash.dao.CollectionsPhotoDao
import nferno1.fotosplash.data.*
import nferno1.fotosplash.data.database.CollectionsPhotos
import nferno1.fotosplash.utils.Constants.UNSPLASH_STARTING_PAGE_INDEX

class UnsplashPagingCollectionsPhoto(
    private val unsplashApi: UnsplashApi,
    private val id: String,
    private val token: String,
    private val collectionsPhotoDao: CollectionsPhotoDao,
) : PagingSource<Int, Results>() {
    override fun getRefreshKey(state: PagingState<Int, Results>) = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Results> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX
        var res: LoadResult<Int, Results>
        try {
            val response =
                unsplashApi.getPhotoFromCollection(id, "Bearer $token", position, params.loadSize)
            response.forEach {
                val tags = mutableListOf<String>()
                it.tags.forEach { tag ->
                    tags.add(tag.title ?: "")
                }

                val collectionPhoto = CollectionsPhotos(
                    id = it.id!!,
                    name = it.user!!.name ?: "",
                    userName = it.user!!.username!!,
                    description = it.description ?: "",
                    urlsRegular = it.urls!!.regular!!,
                    urlsFull = it.urls!!.full!!,
                    likes = it.likes!!,
                    likedByUser = it.likedByUser!!,
                    userProfileImageSmall = it.user!!.profileImage!!.small ?: "",
                    idCollection = this.id,
                    urlsRaw = it.urls!!.raw ?: "",
                    bio = it.user!!.bio ?: "",
                    location = it.user!!.location ?: "",
                    altDescription = it.altDescription ?: "",
                    tags = tags.joinToString("#")
                )
                collectionsPhotoDao.insertCollectionsPhotos(collectionPhoto)
            }
            res = LoadResult.Page(
                data = response,
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (response.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            val collectionsPhotosFromBD = collectionsPhotoDao.getCollectionsPhotos(id)
            if (collectionsPhotosFromBD.isEmpty()) res = LoadResult.Error(e)
            else {
                val photos = mutableListOf<Results>()
                collectionsPhotosFromBD.forEach {
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
                res = LoadResult.Page(data = photos, null, null)
            }
        }
        return res
    }
}