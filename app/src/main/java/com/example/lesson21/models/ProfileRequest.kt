package com.example.lesson21.models

import com.google.gson.annotations.SerializedName

data class ProfileRequest(
    @SerializedName("token")
    val token:String?
)
