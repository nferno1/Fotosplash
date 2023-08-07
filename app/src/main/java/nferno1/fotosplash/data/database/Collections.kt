package nferno1.fotosplash.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class Collections(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "total_photos") val totalPhotos: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "userName") val userName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "urlsRegular") val urlsRegular: String,
    @ColumnInfo(name = "urlsFull") val urlsFull: String,
    @ColumnInfo(name = "coverPhotoLikes") val coverPhotoLikes: Int,
    @ColumnInfo(name = "coverPhotoLikedByUser") val coverPhotoLikedByUser: Boolean,
    @ColumnInfo(name = "profileImageMedium") val profileImageMedium: String,
)
