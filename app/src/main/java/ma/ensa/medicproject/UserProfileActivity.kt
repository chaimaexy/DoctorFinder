package ma.ensa.medicproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserProfileActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var modifyButton: Button
    private lateinit var deleteButton: Button
    private var isDeleteRequested :Boolean = false
    private lateinit var allDoctorsList: MutableList<Doctor>
    private lateinit var recyclerViewFavoriteDoctors: RecyclerView
    private  var specialitiesList: MutableList<Specialities> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize views

        nameTextView = findViewById(R.id.namePatientProfile)
        modifyButton = findViewById(R.id.ModifieButton)
        deleteButton = findViewById(R.id.deletePatient)
        allDoctorsList = mutableListOf()
        fetchAllDoctorsFromDatabase()
        val isLoggedIn = intent.getIntExtra("logged", 0)
        val email = intent.getStringExtra("email" )

        recyclerViewFavoriteDoctors = findViewById(R.id.recyclerViewFavoriteDoctors)
        val layoutManagerFavoriteDoctors = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewFavoriteDoctors.layoutManager = layoutManagerFavoriteDoctors

        // Set up the toolbar
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this , MainActivity::class.java)
            intent.putExtra("logged", 1)
            intent.putExtra("email",email )

            startActivity(intent)
        }

        // Set up the user's information
        fetchUserDataFromDatabase()

        modifyButton.setOnClickListener {
            val intent = Intent(this , ModifiePatientInfo::class.java)
            intent.putExtra("logged", 1)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmation()
        }
        //===========================
        // Assuming userEmail is the email of the currently logged-in user
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        val encodedEmail = email?.replace(".", ",")
        Toast.makeText(this, encodedEmail, Toast.LENGTH_SHORT).show()
        val databaseReferenceFavoriteDoctors = FirebaseDatabase.getInstance().getReference("Favorites")
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        // ...

        if (userEmail != null && encodedEmail != null) {
            databaseReferenceFavoriteDoctors.child(encodedEmail)
                .child("doctors")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val favoriteDoctorsList: MutableList<Doctor> = mutableListOf()

                        for (snapshot in dataSnapshot.children) {
                            val pmdc = snapshot.value as? String
                            Toast.makeText(this@UserProfileActivity, pmdc, Toast.LENGTH_SHORT).show()
                            // Use the Firebase query to find the doctor with the specified pmdc
                            val doctorsReference = FirebaseDatabase.getInstance().getReference("Doctors")
                            doctorsReference.orderByChild("pmdc").equalTo(pmdc)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(doctorDataSnapshot: DataSnapshot) {
                                        for (doctorSnapshot in doctorDataSnapshot.children) {
                                            val doctor = doctorSnapshot.getValue(Doctor::class.java)
                                            doctor?.let {
                                                favoriteDoctorsList.add(it)
                                            }
                                        }
                                        val specialitiesReference = FirebaseDatabase.getInstance().getReference("Specialities")

                                        specialitiesReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {


                                                for (snapshot in dataSnapshot.children) {
                                                    val speciality = snapshot.getValue(Specialities::class.java)
                                                    speciality?.let { specialitiesList.add(it) }
                                                }


                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                // Handle the error
                                                // You can show an error message or handle it according to your app's logic
                                            }
                                        })
                                        // Assuming you have the necessary data (newFavoriteDoctorsList, specialitiesList)
                                        val newAdapter = FavoriteDoctorAdapter(favoriteDoctorsList, specialitiesList) { clickedDoctor ->
                                            val intent = Intent(this@UserProfileActivity, DoctorInfoActivity::class.java)
                                            intent.putExtra("doctor", clickedDoctor)
                                            intent.putExtra("email", email)
                                            intent.putExtra("logged", isLoggedIn)
                                            intent.putExtra("inProfileUser", 1)
                                            startActivity(intent)
                                        }

// Set the new adapter to the RecyclerView
                                        recyclerViewFavoriteDoctors.adapter = newAdapter
                                        // Notify the adapter that the data set has changed
                                       // recyclerViewFavoriteDoctors.adapter?.notifyDataSetChanged()
                                    }

                                    override fun onCancelled(doctorDatabaseError: DatabaseError) {
                                        // Handle error
                                        Log.e("Firebase", "Error reading doctor data", doctorDatabaseError.toException())
                                    }
                                })
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle the error
                        Toast.makeText(
                            this@UserProfileActivity,
                            "Error reading favorite doctors data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }




    }

    private fun showDeleteConfirmation() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "Account will be deleted in 5 seconds. Undo?",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("Undo") {
            // Handle undo action
            isDeleteRequested = false
        }
        snackbar.show()

        // Set a delay for 5 seconds (5000 milliseconds) before deleting the account
        Handler().postDelayed({
            if (isDeleteRequested) {
                deleteAccount()
            }
        }, 5000)

        // If the user clicks on the delete button again within the 5 seconds, cancel the delete
        deleteButton.setOnClickListener {
            snackbar.dismiss()
            isDeleteRequested = false
        }

        isDeleteRequested = true
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userEmail = user.email

            // Delete from Firebase Authentication
            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userEmail?.let { deleteUserDataFromDatabase(it) }
                        //navigate
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("logged",0)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Failed to delete user from the Authentication",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun deleteUserDataFromDatabase(userEmail: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.orderByChild("email").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        // Assuming each child corresponds to a user node
                        snapshot.ref.removeValue()
                    }

                    // Now, you can show a success message or perform any additional actions
                    Toast.makeText(
                        this@UserProfileActivity,
                        "User deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle deletion failure
                    Toast.makeText(
                        this@UserProfileActivity,
                        "Failed to delete user data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

        private fun fetchUserDataFromDatabase() {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email

            userEmail?.let {
                val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

                databaseReference.orderByChild("email").equalTo(it)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                // Assuming each child corresponds to a user node
                                val user = snapshot.getValue(User::class.java)

                                // Update the UI with user's name
                                user?.let {
                                    nameTextView.text = "Bonjour, ${it.name}!"
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle data fetching failure
                            Toast.makeText(
                                this@UserProfileActivity,
                                "Failed to fetch user data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

    private fun fetchAllDoctorsFromDatabase() {
        val databaseReferenceDoctors = FirebaseDatabase.getInstance().getReference("Doctors")

        databaseReferenceDoctors.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allDoctorsList.clear()

                for (snapshot in dataSnapshot.children) {
                    val doctor = snapshot.getValue(Doctor::class.java)
                    doctor?.let {
                        allDoctorsList.add(it)
                    }
                }


                recyclerViewFavoriteDoctors.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Toast.makeText(this@UserProfileActivity, "Error reading doctors data", Toast.LENGTH_SHORT).show()
            }
        })
    }


}