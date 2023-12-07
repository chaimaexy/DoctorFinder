package ma.ensa.medicproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ModifiePatientInfo : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var validateButton: Button
    private lateinit var cancelButton: Button
    private lateinit var currentUserEmail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifie_patient_info)

        // Initialize views
        nameEditText = findViewById(R.id.namePatientField)
        emailEditText = findViewById(R.id.emailPatientField)
        passwordEditText = findViewById(R.id.passwordPatientField)
        validateButton = findViewById(R.id.buttonValidate)
        cancelButton = findViewById(R.id.buttonCancel)

        // Get current user's email
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val isLoggedIn = intent.getIntExtra("logged", 0)
        val email = intent.getStringExtra("email")
        // Set up the toolbar
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {

           // finish()
        }

        // Fetch user data and populate the fields
        fetchUserDataFromDatabase()

        validateButton.setOnClickListener {
            validateChanges()
        }

        cancelButton.setOnClickListener {
            //finish()
        }
    }


    private fun fetchUserDataFromDatabase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.orderByChild("email").equalTo(currentUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        // Assuming each child corresponds to a user node
                        val user = snapshot.getValue(User::class.java)

                        // Update the UI with user's data
                        user?.let {
                            nameEditText.setText(it.name)
                            emailEditText.setText(it.email)
                            // You might not want to show the password in EditText for security reasons
                            passwordEditText.setText(it.password)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle data fetching failure
                    Toast.makeText(
                        this@ModifiePatientInfo,
                        "Failed to fetch user data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun validateChanges() {
        val newName = nameEditText.text.toString().trim()
        val newEmail = emailEditText.text.toString().trim()
        val newPassword = passwordEditText.text.toString().trim()

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(
                this,
                "Name and Email cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Update the user's data in the database
        updateUserProfile(newName, newEmail, newPassword)
    }

    private fun updateUserProfile(newName: String, newEmail: String, newPassword: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.orderByChild("email").equalTo(currentUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        // Assuming each child corresponds to a user node
                        val user = snapshot.getValue(User::class.java)

                        // Update user's data
                        user?.let {
                            it.name = newName
                            it.email = newEmail
                            // You might want to add logic to update the password securely

                            // Save the updated user
                            snapshot.ref.setValue(it)

                            Toast.makeText(
                                this@ModifiePatientInfo,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Finish the activity after successful update
                            finish()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle update failure
                    Toast.makeText(
                        this@ModifiePatientInfo,
                        "Failed to update user profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}