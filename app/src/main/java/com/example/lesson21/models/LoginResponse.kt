package com.example.lesson21.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("token")
    val token:String?,
    @SerializedName("code")
    val code:String?,
    @SerializedName("message")
    val message: String?
)
