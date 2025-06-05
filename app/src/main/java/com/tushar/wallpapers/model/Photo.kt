package com.tushar.wallpapers.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorites")
data class Photo(
    @PrimaryKey val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerializedName("src")
    @Embedded(prefix = "src") val src: Src
) : Parcelable{
    override fun equals(other: Any?): Boolean {
        return other is Photo && other.id==this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

@Parcelize
data class Src(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
) : Parcelable

