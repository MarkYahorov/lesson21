package com.example.lesson21.models

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("birthDate")
    val birthDate: String?,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("code")
    val code: String?,
    @SerializedName("message")
    val message: String?
)
