package nferno1.fotosplash.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collectionsPhoto")
data class CollectionsPhotos(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "userName") val userName: String,
    @ColumnInfo(name = "bio") val bio: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "altDescription") val altDescription: String,
    @ColumnInfo(name = "urlsRegular") val urlsRegular: String,
    @ColumnInfo(name = "urlsFull") val urlsFull: String,
    @ColumnInfo(name = "urlsRaw") val urlsRaw: String,
    @ColumnInfo(name = "likes") val likes: Int,
    @ColumnInfo(name = "likedByUser") val likedByUser: Boolean,
    @ColumnInfo(name = "userProfileImageSmall") val userProfileImageSmall: String,
    @ColumnInfo(name = "idCollection") val idCollection: String,
    @ColumnInfo(name = "tags") val tags: String

)
