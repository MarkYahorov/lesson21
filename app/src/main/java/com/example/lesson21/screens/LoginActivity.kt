package com.example.lesson21.screens

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import bolts.Task
import com.example.lesson21.Constants.EMAIL
import com.example.lesson21.Constants.SAVE_IN_SHARED_PREF
import com.example.lesson21.Constants.HAS_A_TOKEN
import com.example.lesson21.Constants.TOKEN
import com.example.lesson21.GetTokenThread
import com.example.lesson21.R
import com.example.lesson21.Tester
import com.example.lesson21.annotations.TesterAttribute
import com.example.lesson21.annotations.TesterMethod
import com.example.lesson21.models.LoginRequest
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginBtn: Button
    private lateinit var errorText: TextView
    private lateinit var progress: ProgressDialog

    private val getTokenThread = GetTokenThread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!getSharedPreferences(
                SAVE_IN_SHARED_PREF,
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
        testerFromReflection()
        setLoginBtnListener()
        addTextListeners()
    }

    private fun testerFromReflection() {
        val clazz = Tester::class.java.name
        val tester = Class.forName(clazz)
        val testerInstance = tester.constructors.first().newInstance("something")

        invokeMethodsFromTester(tester, testerInstance, "doPublic")
        invokeMethodsFromTester(tester, testerInstance, "doProtected")
        invokeMethodsFromTester(tester, testerInstance, "doPrivate")

        tester.constructors.forEach {
            Log.e("key", "constructor::::::${it.name}")
        }

        tester.declaredMethods.forEach {
            val currentMethod = "MEthod name: ${it.name}"
            if (it.isAnnotationPresent(TesterMethod::class.java)) {
                val annotation = it.getAnnotation(TesterMethod::class.java)
                Log.e(
                    "key", "$currentMethod description: ${annotation?.description} " +
                            "isInner = ${annotation?.isInner}"
                )
            } else {
                Log.e("key", currentMethod)
            }
        }

        tester.declaredFields.forEach {
            val currentField = "${it.name} ${it.type}"
            if (it.isAnnotationPresent(TesterAttribute::class.java)) {
                val annotation = it.getAnnotation(TesterAttribute::class.java)
                Log.d("key", "$currentField, ${annotation.info}")
            } else {
                Log.d("key", currentField)
            }
        }
    }

    private fun invokeMethodsFromTester(tester: Class<*>, testerInstance: Any, method: String) {
        val doMethod = tester.getDeclaredMethod(method)
        doMethod.isAccessible = true
        doMethod.invoke(testerInstance)
    }

    private fun setLoginBtnListener() {
        loginBtn.setOnClickListener {
            createProgressDialog()
            createTheBoltsBackgroundWork()
        }
    }

    private fun createErrorText(errorMessage: String?) {
        with(errorText) {
            visibility = VISIBLE
            text = errorMessage
            progress.dismiss()
        }
    }

    private fun createLoginRequest(): LoginRequest {
        return LoginRequest(loginText.text.toString(), passwordText.text.toString())
    }

    private fun setInShared(key: String, putString: String?) {
        getSharedPreferences(SAVE_IN_SHARED_PREF, Context.MODE_PRIVATE)
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

    private fun createTheBoltsBackgroundWork() {
        getTokenThread
            .getToken(createLoginRequest()).continueWith({
                when {
                    it.error != null -> {
                        createErrorText(it.error.message)
                    }
                    it.result.status == "error" -> {
                        createAlertDialog()
                        createErrorText(it.result.message)
                    }
                    else -> {
                        setInShared(TOKEN, it.result.token)
                        setInShared(EMAIL, loginText.text.toString())
                        startActivity(Intent(this, ProfileActivity::class.java))
                        progress.dismiss()
                    }
                }
            }, Task.UI_THREAD_EXECUTOR)
    }

    private fun createProgressDialog() {
        progress = ProgressDialog(this@LoginActivity)
        progress.setCanceledOnTouchOutside(false)
        progress.show()
    }

    private fun createAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Ok, thanks") { _, _ ->
            loginText.setText("")
            passwordText.setText("")
        }
        builder.setTitle("ERROR")
        builder.setMessage("You data or server is not working")
        builder.setIcon(R.drawable.alert)
        builder.show()
    }

    override fun onStop() {
        super.onStop()
        loginBtn.setOnClickListener(null)
    }
}