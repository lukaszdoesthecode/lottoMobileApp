package com.lottoapp.lottoapp3_0

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var loginED: EditText
    private lateinit var passwordED: EditText
    private lateinit var rePasswordED: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        loginED = findViewById(R.id.loginEDR)
        passwordED = findViewById(R.id.passwordEDR)
        rePasswordED = findViewById(R.id.RePasswordEDR)
        val btnBackToLoginBtn = findViewById<Button>(R.id.backLoginBtn)
        val registerBtn = findViewById<Button>(R.id.registerBtn)

        registerBtn.setOnClickListener{
            registerUser()
        }

        btnBackToLoginBtn.setOnClickListener{
            finish()
        }
    }

    private fun registerUser() {
        val email = loginED.text.toString().trim()
        val password1 = passwordED.text.toString().trim()
        val password2 = rePasswordED.text.toString().trim()

        if (email.isBlank() || password1.isBlank() || password2.isBlank()) {
            showMessage("Please fill in all fields")
            return
        }

        if (password1 != password2) {
            showMessage("Passwords do not match")
            return
        }

        // Firebase registration
        auth.createUserWithEmailAndPassword(email, password1)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success, proceed to next activity
                    showMessage("Registration Successful")
                    val intent = Intent(this, NumbSelectionActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Show error message for registration failure
                    showMessage("Registration Failed: ${task.exception?.message}")
                }
            }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun  userRegistrationSuccess(){

        Toast.makeText(this@RegisterActivity, resources.getString(R.string.register_success),
            Toast.LENGTH_LONG).show()
    }

}
