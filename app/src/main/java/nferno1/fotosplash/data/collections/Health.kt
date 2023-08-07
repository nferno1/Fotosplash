package nferno1.fotosplash.data.collections

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class Health(

    @SerializedName("status") var status: String? = null,
    @SerializedName("approved_on") var approvedOn: String? = null

) : Parcelable