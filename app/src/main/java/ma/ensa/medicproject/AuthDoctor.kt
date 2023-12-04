package ma.ensa.medicproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AuthDoctor : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var create: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_doctor)

        mAuth = FirebaseAuth.getInstance()



        val loginButton = findViewById<Button>(R.id.doctor_sign_in_button)
        loginButton.setOnClickListener {
            loginDoctor()
        }

        create = findViewById(R.id.CreateAccountDoc)
        create.setOnClickListener{
            val intent = Intent(this, CreateAccountDoc::class.java)
            startActivity(intent)
        }
    }

    private fun loginDoctor() {
        val emailEditText = findViewById<TextInputEditText>(R.id.doctorEmail)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password)

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //
                        val navHeaderLoginButton = findViewById<Button>(R.id.Login)
                        navHeaderLoginButton.visibility = View.GONE

                        //
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        // Example: Redirect to DoctorDashboardActivity
                        val intent = Intent(this, MainActivity::class.java)

                        intent.putExtra("email", email)
                        intent.putExtra("logged", 1)
                        startActivity(intent)
                        finish()
                    } else {
                        // If login fails, display a message to the user.
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
        }
    }


}
