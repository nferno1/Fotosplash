package nferno1.fotosplash.data.me

import com.google.gson.annotations.SerializedName


data class Me(

    @SerializedName("id") var id: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("twitter_username") var twitterUsername: String? = null,
    @SerializedName("portfolio_url") var portfolioUrl: String? = null,
    @SerializedName("bio") var bio: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("links") var links: Links? = Links(),
    @SerializedName("profile_image") var profileImage: ProfileImage? = ProfileImage(),
    @SerializedName("instagram_username") var instagramUsername: String? = null,
    @SerializedName("total_collections") var totalCollections: Int? = null,
    @SerializedName("total_likes") var totalLikes: Int? = null,
    @SerializedName("total_photos") var totalPhotos: Int? = null,
    @SerializedName("accepted_tos") var acceptedTos: Boolean? = null,
    @SerializedName("for_hire") var forHire: Boolean? = null,
    @SerializedName("social") var social: Social? = Social(),
    @SerializedName("followed_by_user") var followedByUser: Boolean? = null,
    @SerializedName("photos") var photos: ArrayList<Photos> = arrayListOf(),
    @SerializedName("badge") var badge: String? = null,
    @SerializedName("followers_count") var followersCount: Int? = null,
    @SerializedName("following_count") var followingCount: Int? = null,
    @SerializedName("allow_messages") var allowMessages: Boolean? = null,
    @SerializedName("numeric_id") var numericId: Int? = null,
    @SerializedName("downloads") var downloads: Int? = null,
    @SerializedName("meta") var meta: Meta? = Meta(),
    @SerializedName("uid") var uid: String? = null,
    @SerializedName("confirmed") var confirmed: Boolean? = null,
    @SerializedName("uploads_remaining") var uploadsRemaining: Int? = null,
    @SerializedName("unlimited_uploads") var unlimitedUploads: Boolean? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("dmca_verification") var dmcaVerification: String? = null,
    @SerializedName("unread_in_app_notifications") var unreadInAppNotifications: Boolean? = null,
    @SerializedName("unread_highlight_notifications") var unreadHighlightNotifications: Boolean? = null

)