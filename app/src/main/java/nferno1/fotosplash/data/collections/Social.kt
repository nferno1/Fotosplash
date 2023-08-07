package nferno1.fotosplash.data.collections

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class Social(

    @SerializedName("instagram_username") var instagramUsername: String? = null,
    @SerializedName("portfolio_url") var portfolioUrl: String? = null,
    @SerializedName("twitter_username") var twitterUsername: String? = null,
    @SerializedName("paypal_email") var paypalEmail: String? = null

) : Parcelable