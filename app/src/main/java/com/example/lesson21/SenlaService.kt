package com.example.lesson21

import com.example.lesson21.models.LoginRequest
import com.example.lesson21.models.LoginResponse
import com.example.lesson21.models.ProfileRequest
import com.example.lesson21.models.ProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SenlaService {

    @POST("lesson-21.php?method=login")
    fun getToken(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("lesson-21.php?method=profile")
    fun getUserData(@Body profileRequest: ProfileRequest): Call<ProfileResponse>
}