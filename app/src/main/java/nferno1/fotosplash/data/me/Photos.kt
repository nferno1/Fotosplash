package nferno1.fotosplash.data.me

import com.google.gson.annotations.SerializedName
import nferno1.fotosplash.data.Urls


data class Photos(

    @SerializedName("id") var id: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("blur_hash") var blurHash: String? = null,
    @SerializedName("urls") var urls: Urls? = Urls()


)
