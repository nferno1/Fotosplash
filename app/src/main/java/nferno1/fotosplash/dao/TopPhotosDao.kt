package nferno1.fotosplash.dao

import androidx.room.*
import nferno1.fotosplash.data.database.TopPhotos

@Dao
interface TopPhotosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopPhoto(photos: TopPhotos)

    @Query("SELECT * FROM `topPhotos` WHERE id = :id")
    suspend fun getTopPhotos(id: String): List<TopPhotos>

    @Delete
    suspend fun deleteTopPhotos(photos: TopPhotos)


}