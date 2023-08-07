package nferno1.fotosplash.data

import com.google.gson.annotations.SerializedName

data class Token(

    @SerializedName("access_token") var accessToken: String? = null,
    @SerializedName("token_type") var tokenType: String? = null,
    @SerializedName("scope") var scope: String? = null,
    @SerializedName("created_at") var createdAt: Int? = null

)