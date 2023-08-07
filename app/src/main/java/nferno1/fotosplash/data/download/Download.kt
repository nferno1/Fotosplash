package nferno1.fotosplash.data.download

import com.google.gson.annotations.SerializedName

data class Download(
    @SerializedName("url") var url: String? = null,
)
