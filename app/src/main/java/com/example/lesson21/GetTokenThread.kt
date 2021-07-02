package com.example.lesson21

import bolts.Task
import com.example.lesson21.Constants.URL
import com.example.lesson21.models.LoginRequest
import com.example.lesson21.models.LoginResponse
import com.example.lesson21.models.ProfileRequest
import com.example.lesson21.models.ProfileResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class GetTokenThread(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    companion object {
        private const val LOGIN = "login"
        private const val PROFILE = "profile"
    }

    fun getToken(loginRequest: LoginRequest): Task<LoginResponse> {
        return Task.callInBackground {
            executeRequest(loginRequest, LOGIN, LoginResponse::class.java)
        }
    }

    fun getUserData(profileRequest: ProfileRequest): Task<ProfileResponse> {
        return Task.callInBackground {
            executeRequest(profileRequest, PROFILE, ProfileResponse::class.java)
        }
    }

    private fun <T,B> executeRequest(request: B, method: String, clazz: Class<T>): T {
        val requestBody = gson.toJson(request).toRequestBody()
        val requestProfile = Request.Builder()
            .url(URL + method)
            .post(requestBody)
            .build()
        okHttpClient.newCall(requestProfile).execute().use {
            it.body.let { body ->
                val jsonObj = body?.string() ?: error("Empty response")
                return gson.fromJson(jsonObj, clazz)
            }
        }
    }
}