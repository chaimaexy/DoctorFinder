package ma.ensa.medicproject

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_patient)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        btnNext = findViewById(R.id.patient_sign_in_button1)

        btnNext.setOnClickListener {
            CreateUser()
           
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
        savePatientToFirebase(email , password )
        // Save the user object to Firebase Realtime Database
        saveUserToDatabase(user)

        // Cancel
        val btnCancel: ImageButton = findViewById(R.id.btnCancelP)
        btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun savePatientToFirebase(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation successful
                    val firebaseUser: FirebaseUser? = mAuth.currentUser
                    if (firebaseUser != null) {
                        // Do something with the user (optional)
                        Toast.makeText(
                            baseContext, "Authentication success.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } else {
                    // If account creation fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserToDatabase(user: User) {

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Users")

        // Generate a unique key for the  entry
        val PatientKey = reference.push().key
        if (PatientKey != null) {
            reference.child(PatientKey).setValue(user)
            Toast.makeText(this, "Your Account is Created", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)

            intent.putExtra("logged", 2)
            intent.putExtra("PatientName", name)
            intent.putExtra("PatientPassword", password)

            startActivity(intent)
        } else {
            Toast.makeText(this, "Error generating key", Toast.LENGTH_SHORT).show()
        }
    }
}
