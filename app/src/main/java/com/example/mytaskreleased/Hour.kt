package com.example.mytaskreleased

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Hour(
    val hour : String,
    var localization : String
) : Parcelable
