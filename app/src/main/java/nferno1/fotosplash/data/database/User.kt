package nferno1.fotosplash.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "userName") val userName: String,
    @ColumnInfo(name = "twitterUserName") val twitterUserName: String,
    @ColumnInfo(name = "profileImageLarge") val profileImageLarge: String,
    @ColumnInfo(name = "followersCount") val followersCount: Int,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "downloads") val downloads: Int,
    @ColumnInfo(name = "totalPhotos") val totalPhotos: Int,
    @ColumnInfo(name = "totalLikes") val totalLikes: Int,
    @ColumnInfo(name = "totalCollections") val totalCollections: Int,
)
