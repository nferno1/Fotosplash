package nferno1.fotosplash.data.me

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class Tags(


    @SerializedName("custom") var custom: ArrayList<String> = arrayListOf(),
    @SerializedName("aggregated") var aggregated: ArrayList<String> = arrayListOf()

) : Parcelable