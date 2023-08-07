package nferno1.fotosplash.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class Subcategory(

    @SerializedName("slug") var slug: String? = null,
    @SerializedName("pretty_slug") var prettySlug: String? = null

) : Parcelable