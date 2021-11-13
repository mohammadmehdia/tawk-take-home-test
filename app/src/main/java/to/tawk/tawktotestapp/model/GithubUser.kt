package to.tawk.tawktotestapp.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class GithubUser(
    @SerializedName("id")           @PrimaryKey                         val id: Long,
    @SerializedName("login")        @ColumnInfo(name = "login")         val login: String?,
    @SerializedName("node_id")      @ColumnInfo(name = "node_id")       val nodeId: String?,
    @SerializedName("avatar_url")   @ColumnInfo(name = "avatar_url")    val avatarUrl: String?,
    @SerializedName("url")          @ColumnInfo(name = "url")           val url: String?,
    @SerializedName("html_url")     @ColumnInfo(name = "html_url")      val htmlUrl: String?,
    @SerializedName("type")         @ColumnInfo(name = "type")          val type: String?,
    @SerializedName("blog")         @ColumnInfo(name = "blog")          val blog: String?,
    @SerializedName("company")      @ColumnInfo(name = "company")       val company: String?,
    @SerializedName("twitter_username") @ColumnInfo(name = "twitter_username")  val twitterUsername: String?,
    @SerializedName("name")         @ColumnInfo(name = "name")          val name: String?,
    @SerializedName("followers")    @ColumnInfo(name = "followers")     val followers: Int,
    @SerializedName("following")    @ColumnInfo(name = "following")     val following: Int,
    @SerializedName("location")     @ColumnInfo(name = "location")      val location: String?,
    @SerializedName("note")         @ColumnInfo(name = "note")          var note: String?,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    fun displayName(): String? {
        return if(!TextUtils.isEmpty(name))
            name
        else
            login
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(login)
        parcel.writeString(nodeId)
        parcel.writeString(avatarUrl)
        parcel.writeString(url)
        parcel.writeString(htmlUrl)
        parcel.writeString(type)
        parcel.writeString(blog)
        parcel.writeString(company)
        parcel.writeString(twitterUsername)
        parcel.writeString(name)
        parcel.writeInt(followers)
        parcel.writeInt(following)
        parcel.writeString(location)
        parcel.writeString(note)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GithubUser> {
        override fun createFromParcel(parcel: Parcel): GithubUser {
            return GithubUser(parcel)
        }

        override fun newArray(size: Int): Array<GithubUser?> {
            return arrayOfNulls(size)
        }
    }

}