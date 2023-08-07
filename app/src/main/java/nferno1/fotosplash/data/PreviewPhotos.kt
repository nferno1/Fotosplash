package nferno1.fotosplash.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class PreviewPhotos (

  @SerializedName("id"         ) var id        : String? = null,
  @SerializedName("created_at" ) var createdAt : String? = null,
  @SerializedName("updated_at" ) var updatedAt : String? = null,
  @SerializedName("blur_hash"  ) var blurHash  : String? = null,
  @SerializedName("urls"       ) var urls      : Urls?   = Urls()

): Parcelable