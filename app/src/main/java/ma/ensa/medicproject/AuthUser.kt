package ma.ensa.medicproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class AuthUser : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var create: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_user)

        mAuth = FirebaseAuth.getInstance()

        val loginButton = findViewById<Button>(R.id.patient_sign_in_button)
        loginButton.setOnClickListener {
            loginUser()
        }

        create = findViewById(R.id.CreateAccount)
        create.setOnClickListener{
            val intent = Intent(this, AuthChoice::class.java)
            startActivity(intent)
        }


    }


    private fun loginUser() {
        val emailEditText = findViewById<TextInputEditText>(R.id.email)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordPatient)

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login successful
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("logged", 1)
                        startActivity(intent)
                        finish()
                    } else {
                        // If login fails
                        val errorMessage = task.exception?.message
                        Toast.makeText(
                            baseContext, "Authentication failed: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()                    }
                }
        } else {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
        }
    }
}