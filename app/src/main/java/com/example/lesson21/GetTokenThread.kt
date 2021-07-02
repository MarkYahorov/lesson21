package com.example.lesson21

import bolts.Task
import com.example.lesson21.Constants.URL
import com.example.lesson21.models.LoginRequest
import com.example.lesson21.models.LoginResponse
import com.example.lesson21.models.ProfileRequest
import com.example.lesson21.models.ProfileResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GetTokenThread() {

    fun getToken(loginRequest: LoginRequest): Task<LoginResponse> {
        return Task.callInBackground {
            val execute = executeRequest(SenlaService::class.java)
            execute.getToken(loginRequest).execute().body()
        }
    }

    fun getUserData(profileRequest: ProfileRequest): Task<ProfileResponse> {
        return Task.callInBackground {
            executeRequest(SenlaService::class.java).getUserData(profileRequest).execute().body()
        }
    }

    private fun <T> executeRequest(clazz: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(clazz)
    }
}