package com.example.lesson21

import bolts.Task
import com.example.lesson21.models.LoginRequest
import com.example.lesson21.models.LoginResponse
import com.example.lesson21.models.ProfileRequest
import com.example.lesson21.models.ProfileResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.RuntimeException


class GetTokenThread(
    val okHttpClient: OkHttpClient,
    val request: LoginRequest
    ){

    private fun goSomething(): Task<ProfileResponse> {
        return Task.callInBackground {
            val gson = Gson()
            val body = gson.toJson(request, LoginRequest::class.java).toString().toRequestBody()
                val request = Request.Builder()
                    //.url(ProfileActivity.URI + LOGIN)
                    .post(body)
                    .build()
                okHttpClient.newCall(request).execute().use {
                    it.body.let { body ->
                        val jsonObj = body?.string()
                        val gson = Gson()
                        gson.fromJson(jsonObj, LoginResponse::class.java)
                    }
                }
        }.onSuccess {
            if (it.result.status == "error") {
                throw RuntimeException("${it.result.message}")
            } else {
                val gson = Gson()
                val request = ProfileRequest(it.result.token)
                val requestBody = gson.toJson(request).toRequestBody()
                val requestT = Request.Builder()
                    //.url(ProfileActivity.URI + LOGIN)
                    .post(requestBody)
                    .build()
                okHttpClient.newCall(requestT).execute().use {
                    it.body.let { body ->
                        val jsonObj = body?.string()
                        val gson = Gson()
                        gson.fromJson(jsonObj, ProfileResponse::class.java)
                    }
                }
            }
        }


    }
}