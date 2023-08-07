package nferno1.fotosplash.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import nferno1.fotosplash.data.database.Collections

@Dao
interface CollectionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: Collections)

    @Query("SELECT * FROM `collections`")
    suspend fun getCollections(): List<Collections>

    @Delete
    suspend fun deleteCollection(collections: Collections)

    @Query("DELETE FROM `collections`")
    suspend fun allDeleteCollections()

}