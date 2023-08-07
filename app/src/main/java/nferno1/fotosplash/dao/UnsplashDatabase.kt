package nferno1.fotosplash.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import nferno1.fotosplash.data.database.*

@Database(
    entities = [
        Collections::class,
        MyCollections::class,
        MyPhotos::class,
        Photos::class,
        PhotosLikesMe::class,
        User::class,
        TopPhotos::class,
        CollectionsPhotos::class
    ], version = 1
)
abstract class UnsplashDatabase : RoomDatabase() {
    abstract fun getCollectionsDao(): CollectionsDao
    abstract fun getMyCollectionsDao(): MyCollectionsDao
    abstract fun getMyPhotosDao(): MyPhotosDao
    abstract fun getPhotosDao(): PhotosDao
    abstract fun getPhotosLikesMeDao(): PhotosLikesMeDao
    abstract fun getUserDao(): UserDao
    abstract fun getTopPhotosDao(): TopPhotosDao
    abstract fun getCollectionsPhotoDao(): CollectionsPhotoDao

}