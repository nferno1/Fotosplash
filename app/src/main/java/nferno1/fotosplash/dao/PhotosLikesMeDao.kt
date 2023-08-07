package nferno1.fotosplash.dao

import androidx.room.*
import nferno1.fotosplash.data.database.PhotosLikesMe

@Dao
interface PhotosLikesMeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotosLikesMe(photosLikesMe: PhotosLikesMe)

    @Query("SELECT * FROM `photosLikesMe`")
    suspend fun getPhotosLikesMe(): List<PhotosLikesMe>

    @Delete
    suspend fun deletePhotosLikesMe(photosLikesMe: PhotosLikesMe)

    @Query("DELETE FROM `photosLikesMe`")
    suspend fun allDeletePhotosLikesMe()

}