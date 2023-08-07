package nferno1.fotosplash.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class Ancestry(

    @SerializedName("type") var type: Type? = Type(),
    @SerializedName("category") var category: Category? = Category(),
    @SerializedName("subcategory") var subcategory: Subcategory? = Subcategory()

) : Parcelable