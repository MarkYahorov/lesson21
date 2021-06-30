package com.example.lesson21.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.lesson21.R
import com.example.lesson21.models.LoginRequest
import com.example.lesson21.models.ProfileRequest
import com.google.gson.Gson
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody

class LoginActivity : AppCompatActivity() {

    private lateinit var loginText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginBtn: Button
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAll()
    }

    private fun initAll(){
        loginText = findViewById(R.id.login_edit_text)
        passwordText = findViewById(R.id.password_edit_text)
        loginBtn = findViewById(R.id.login_btn)
        errorText = findViewById(R.id.error_text_view)
    }

    override fun onStart() {
        super.onStart()
        setLoginBtnListener()
    }

    private fun setLoginBtnListener(){
        loginBtn.setOnClickListener {

        }
    }

    private fun createGson(): LoginRequest{
        return LoginRequest(loginText.text.toString(), passwordText.text.toString())
    }
}