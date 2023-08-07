package nferno1.fotosplash.dao

import androidx.room.*
import nferno1.fotosplash.data.database.Photos


@Dao
interface PhotosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: Photos)

    @Query("SELECT * FROM `photos`")
    suspend fun getPhotos(): List<Photos>

    @Delete
    suspend fun deletePhotos(photos: Photos)

    @Query("DELETE FROM `photos`")
    suspend fun allDeletePhotos()
}