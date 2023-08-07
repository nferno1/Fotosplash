package nferno1.fotosplash.data.collections

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class CurrentUserCollections(

    @SerializedName("id") var id: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("published_at") var publishedAt: String? = null,
    @SerializedName("last_collected_at") var lastCollectedAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("curated") var curated: Boolean? = null,
    @SerializedName("featured") var featured: Boolean? = null,
    @SerializedName("total_photos") var totalPhotos: Int? = null,
    @SerializedName("private") var private: Boolean? = null,
    @SerializedName("share_key") var shareKey: String? = null,
    @SerializedName("tags") var tags: ArrayList<Tags> = arrayListOf(),
    @SerializedName("links") var links: Links? = Links()

) : Parcelable