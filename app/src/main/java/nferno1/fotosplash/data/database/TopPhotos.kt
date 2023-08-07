package nferno1.fotosplash.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "topPhotos")
data class TopPhotos(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "userName") val userName: String,
    @ColumnInfo(name = "bio") val bio: String,
    @ColumnInfo(name = "locationName") val locationName: String,
    @ColumnInfo(name = "locationCity") val locationCity: String,
    @ColumnInfo(name = "locationCountry") val locationCountry: String,
    @ColumnInfo(name = "locationLat") val locationLat: String,
    @ColumnInfo(name = "locationLon") val locationLon: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "altDescription") val altDescription: String,
    @ColumnInfo(name = "urlsRegular") val urlsRegular: String,
    @ColumnInfo(name = "urlsFull") val urlsFull: String,
    @ColumnInfo(name = "urlsRaw") val urlsRaw: String,
    @ColumnInfo(name = "likes") val likes: Int,
    @ColumnInfo(name = "likedByUser") val likedByUser: Boolean,
    @ColumnInfo(name = "userProfileImageSmall") val userProfileImageSmall: String,
    @ColumnInfo(name = "tags") val tags: String,
    @ColumnInfo(name = "downloads") val downloads: Int,
    @ColumnInfo(name = "exifMake") val exifMake: String,
    @ColumnInfo(name = "exifModel") val exifModel: String,
    @ColumnInfo(name = "exifName") val exifName: String,
    @ColumnInfo(name = "exifExposureTime") val exifExposureTime: String,
    @ColumnInfo(name = "exifAperture") val exifAperture: String,
    @ColumnInfo(name = "exifFocalLength") val exifFocalLength: String,
    @ColumnInfo(name = "exifIso") val exifIso: String,
)
