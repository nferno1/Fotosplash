package nferno1.fotosplash.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class Sponsorship(

    @SerializedName("impression_urls") var impressionUrls: ArrayList<String> = arrayListOf(),
    @SerializedName("tagline") var tagline: String? = null,
    @SerializedName("tagline_url") var taglineUrl: String? = null,
    @SerializedName("sponsor") var sponsor: Sponsor? = Sponsor()

) : Parcelable