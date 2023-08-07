package nferno1.fotosplash.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class Exif(

    @SerializedName("make") var make: String? = null,
    @SerializedName("model") var model: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("exposure_time") var exposureTime: String? = null,
    @SerializedName("aperture") var aperture: String? = null,
    @SerializedName("focal_length") var focalLength: String? = null,
    @SerializedName("iso") var iso: String? = null

) : Parcelable