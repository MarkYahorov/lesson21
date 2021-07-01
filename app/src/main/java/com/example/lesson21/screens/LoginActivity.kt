package com.example.lesson21.screens

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import bolts.Task
import com.example.lesson21.Constants.EMAIL
import com.example.lesson21.Constants.KEY_FOR_GET_OR_SET_FILES_FROM_SHARED_PREFERENCES
import com.example.lesson21.Constants.HAS_A_TOKEN
import com.example.lesson21.Constants.TOKEN
import com.example.lesson21.GetTokenThread
import com.example.lesson21.R
import com.example.lesson21.models.LoginRequest
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginBtn: Button
    private lateinit var errorText: TextView
    private lateinit var progress: ProgressDialog

    private val okHttpClient = OkHttpClient()
    private val gson = Gson()
    private val getTokenThread = GetTokenThread(okHttpClient, gson)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!getSharedPreferences(
                KEY_FOR_GET_OR_SET_FILES_FROM_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
            ).getBoolean(
                HAS_A_TOKEN, true
            )
        ) {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        initAll()
    }

    private fun initAll() {
        loginText = findViewById(R.id.login_edit_text)
        passwordText = findViewById(R.id.password_edit_text)
        loginBtn = findViewById(R.id.login_btn)
        errorText = findViewById(R.id.error_text_view)
    }

    override fun onStart() {
        super.onStart()
        setLoginBtnListener()
        addTextListeners()
    }

    private fun setLoginBtnListener() {
        loginBtn.setOnClickListener {
            createProgressDialog()
            getTokenThread
                .getToken(createGson()).onSuccess({
                    if (it.result.status == "error") {
                        with(errorText) {
                            visibility = VISIBLE
                            text = it.result.message
                            progress.dismiss()
                        }
                    } else {
                        setInShared(TOKEN, it.result.token)
                        setInShared(EMAIL, loginText.text.toString())
                        startActivity(Intent(this, ProfileActivity::class.java))
                        progress.dismiss()
                    }
                }, Task.UI_THREAD_EXECUTOR)
        }
    }

    private fun createGson(): LoginRequest {
        return LoginRequest(loginText.text.toString(), passwordText.text.toString())
    }

    private fun setInShared(key: String, putString: String?) {
        getSharedPreferences(KEY_FOR_GET_OR_SET_FILES_FROM_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putString(key, putString)
            .apply()
    }

    private fun addTextListeners() {
        loginText.addTextChangedListener(createTextListener())
        passwordText.addTextChangedListener(createTextListener())
    }

    private fun createTextListener(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginBtn.isEnabled = loginText.text.isNotEmpty() && passwordText.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
    }

    private fun createProgressDialog() {
        progress = ProgressDialog(this@LoginActivity)
        progress.setCanceledOnTouchOutside(false)
        progress.show()
    }

    override fun onStop() {
        super.onStop()
        loginBtn.setOnClickListener(null)
    }
}