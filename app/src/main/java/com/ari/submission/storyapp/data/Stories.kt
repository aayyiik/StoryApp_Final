package com.ari.submission.storyapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "list_stories")
data class Stories(
    @PrimaryKey
    var id: String,

    var name: String? = null,
    var description: String? = null,
    var photoUrl: String? = null,
    var createdAt: String? = null,
    var lat: Float,
    var lon: Float
): Parcelable

