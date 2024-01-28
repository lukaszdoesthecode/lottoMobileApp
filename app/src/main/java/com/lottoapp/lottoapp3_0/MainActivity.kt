package com.lottoapp.lottoapp3_0

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import java.util.Calendar
import com.lottoapp.lottoapp3_0.Notification

class MainActivity : AppCompatActivity() {

    private lateinit var inputLogin: EditText
    private lateinit var inputPassword: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        val btnGO = findViewById<Button>(R.id.btnGO)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        inputLogin = findViewById<EditText>(R.id.loginED)
        inputPassword = findViewById<EditText>(R.id.passwordED)

        btnGO.setOnClickListener {
            logInRegisteredUser()
        }

        btnRegister.setOnClickListener {
            // Intent for RegisterActivity
            val intentRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentRegister)

        }

        fun scheduleNotification(context: Context, notificationTime: Calendar) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, Notification::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            // Set the alarm to trigger at the specified time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime.timeInMillis, pendingIntent)
        }
        val notificationTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        scheduleNotification(this, notificationTime)

    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(inputLogin.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter your email.", true)
                false
            }

            TextUtils.isEmpty(inputPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter your password.", true)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val email = inputLogin.text.toString().trim { it <= ' ' }
            val password = inputPassword.text.toString().trim { it <= ' ' }

            // Authenticate with Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToNumbSelectionActivity()
                    } else {
                        showErrorSnackBar(task.exception?.message.toString(), true)
                    }
                }
        }
    }

    private fun goToNumbSelectionActivity() {
        val user = auth.currentUser
        val uid = user?.email.toString()

        val intent = Intent(this, NumbSelectionActivity::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
    }

    private fun showErrorSnackBar(message: String, isError: Boolean) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (isError) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_success_color)) // Replace with your success color
        }

        snackBar.show()
    }
}
