package com.example.lesson21.screens

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import bolts.Task
import com.example.lesson21.Constants.BIRTH_DATE
import com.example.lesson21.Constants.EMAIL
import com.example.lesson21.Constants.FIRST_NAME
import com.example.lesson21.Constants.SAVE_IN_SHARED_PREF
import com.example.lesson21.Constants.HAS_A_TOKEN
import com.example.lesson21.Constants.LAST_NAME
import com.example.lesson21.Constants.NOTE
import com.example.lesson21.Constants.TOKEN
import com.example.lesson21.Constants.isNotInProfileActivity
import com.example.lesson21.GetTokenThread
import com.example.lesson21.R
import com.example.lesson21.models.ProfileRequest
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var currentEmail: TextView
    private lateinit var currentFirstName: TextView
    private lateinit var currentSecondName: TextView
    private lateinit var currentBirthday: TextView
    private lateinit var currentNote: TextView
    private lateinit var logoutBtn: Button
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var progress: ProgressDialog

    private val getTokenThread = GetTokenThread()
    private val formatter = SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initAll()
    }

    private fun initAll() {
        currentEmail = findViewById(R.id.current_email)
        currentFirstName = findViewById(R.id.current_first_name)
        currentSecondName = findViewById(R.id.current_last_name)
        currentBirthday = findViewById(R.id.current_birthday)
        currentNote = findViewById(R.id.current_note)
        logoutBtn = findViewById(R.id.logout_btn)
        swipeLayout = findViewById(R.id.swipe_layout)
    }

    override fun onStart() {
        super.onStart()
        if (isNotInProfileActivity) {
            createProgressDialog()
            getUserData()
        }
        setInShared(false)
        setLogoutBtnListener()
        currentNote.movementMethod = ScrollingMovementMethod()
        setSwipeLayout()
    }

    private fun setLogoutBtnListener() {
        logoutBtn.setOnClickListener {
            setInShared(true)
            getSharedPreferences(
                SAVE_IN_SHARED_PREF,
                Context.MODE_PRIVATE
            )
                .edit()
                .putString(TOKEN, "")
                .apply()
            finish()
        }
    }

    private fun createGson(): ProfileRequest {
        return ProfileRequest(
            getSharedPreferences(SAVE_IN_SHARED_PREF, Context.MODE_PRIVATE)
                .getString(TOKEN, "")
        )
    }

    private fun setSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            getUserData()
        }
    }

    private fun getUserData() {
        currentEmail.text = getSharedPreferences(SAVE_IN_SHARED_PREF, Context.MODE_PRIVATE)
            .getString(EMAIL, "")
        getTokenThread.getUserData(createGson())
            .continueWith({
                when {
                    it.error != null -> {
                        createAlertDialog()
                    }
                    it.result.status == "error" -> {
                        createAlertDialog()
                    }
                    else -> {
                        currentFirstName.text = it.result.firstName
                        currentSecondName.text = it.result.lastName
                        currentBirthday.text = formatDate(formatter, it.result.birthDate!!.toLong())
                        currentNote.text = it.result.notes
                        swipeLayout.isRefreshing = false
                        progress.dismiss()
                    }
                }
            }, Task.UI_THREAD_EXECUTOR)
    }


    private fun setInShared(outOrIn: Boolean) {
        isNotInProfileActivity = outOrIn
        getSharedPreferences(SAVE_IN_SHARED_PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(HAS_A_TOKEN, isNotInProfileActivity)
            .apply()
    }

    private fun formatDate(formatter: SimpleDateFormat, millisForFormat: Long): String {
        return formatter.format(Date(millisForFormat))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EMAIL, currentEmail.text.toString())
        outState.putString(FIRST_NAME, currentFirstName.text.toString())
        outState.putString(LAST_NAME, currentSecondName.text.toString())
        outState.putString(BIRTH_DATE, currentBirthday.text.toString())
        outState.putString(NOTE, currentNote.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentEmail.text = savedInstanceState.getString(EMAIL)
        currentFirstName.text = savedInstanceState.getString(FIRST_NAME)
        currentSecondName.text = savedInstanceState.getString(LAST_NAME)
        currentBirthday.text = savedInstanceState.getString(BIRTH_DATE)
        currentNote.text = savedInstanceState.getString(NOTE)
    }

    private fun createProgressDialog() {
        progress = ProgressDialog(this@ProfileActivity)
        progress.setCanceledOnTouchOutside(false)
        progress.show()
    }

    private fun createAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Ok, thanks") { _, _ ->
            setInShared(true)
            finish()
        }
        builder.setTitle("ERROR")
        builder.setIcon(R.drawable.alert)
        builder.setMessage("You data or server is not working")
        builder.show()
    }

    override fun onStop() {
        super.onStop()
        logoutBtn.setOnClickListener(null)
    }
}