package ma.ensa.medicproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CreateAccountPatient : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var btnNext: Button
    private lateinit var cancel: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_patient)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        btnNext = findViewById(R.id.patient_sign_in_button1)

        btnNext.setOnClickListener {
            CreateUser()
           
        }

        cancel = findViewById(R.id.btnCancelP)
        cancel.setOnClickListener {
            val intent = Intent(this, AuthUser::class.java)
            startActivity(intent)
        }
    }

  

    private fun CreateUser() {
        // Validate empty fields
        val nameEditText = findViewById<EditText>(R.id.patientName)
        val emailEditText = findViewById<EditText>(R.id.patientEmail)
        val passwordLayout = findViewById<TextInputLayout>(R.id.patientPasswordLayout)
        val passwordConfirmLayout = findViewById<TextInputLayout>(R.id.patientPasswordConfirmLayout)

        name = nameEditText.text.toString().trim()
        email = emailEditText.text.toString().trim()
        password = passwordLayout.findViewById<TextInputEditText>(R.id.patientPassword).text.toString().trim()
        val passwordConfirm = passwordConfirmLayout.findViewById<TextInputEditText>(R.id.patientPasswordConfirm).text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            // Show error message for empty fields
            Toast.makeText(this, "All fields must be filled !", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != passwordConfirm) {
            // Show error message for mismatched passwords
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a User object
        val user = User(name, email, password)

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation successful
                    val firebaseUser: FirebaseUser? = mAuth.currentUser
                    if (firebaseUser != null) {
                        // Save the user object to Firebase Realtime Database
                        saveUserToDatabase(user)

                        // Inform the user about successful account creation
                        Toast.makeText(
                            baseContext, "Account creation successful.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Redirect to your desired activity
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("logged", 1)
                        intent.putExtra("email", email)
                        startActivity(intent)

                    }
                } else {
                    // If account creation fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Account creation failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        // Cancel
        val btnCancel: ImageButton = findViewById(R.id.btnCancelP)
        btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserToDatabase(user: User) {
        // Save the user data under the "Patients" node in Firebase Realtime Database
        mDatabase.child("Users").push().setValue(user)
    }


}
