package ma.ensa.medicproject

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.io.Serializable

class DoctorInfoActivity : AppCompatActivity() {
    private lateinit var doctorName: TextView
    private lateinit var doctorEmail: TextView
    private lateinit var experience: TextView
    private lateinit var specialityy: TextView
    private lateinit var doctorGender: TextView
    private lateinit var consultPrice: TextView
    private lateinit var consultPriceInfo: TextView
    private lateinit var address: TextView
    private lateinit var city: TextView
    private lateinit var workDays: TextView
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var backButton : ImageView
    private lateinit var doctorImage: ImageView
    private lateinit var firebaseImageUploader: FirebaseImageUploader

    private lateinit var doctor: Doctor
    private lateinit var  favoriteButton : Button
    private lateinit var callDoctor : Button
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_info)


        doctorName = findViewById(R.id.doctorNameTextView)
        doctorEmail = findViewById(R.id.doctorEmailTextView)
        doctorGender = findViewById(R.id.doctorGenderTextView)

        experience= findViewById(R.id.doctorExperienceTextView)
        specialityy= findViewById(R.id.doctorSpecialityTextView)
        callDoctor =  findViewById(R.id.callDoctor)
        consultPrice= findViewById(R.id.doctorPriceTextView)
        consultPriceInfo= findViewById(R.id.doctorPriceInfoTextView)
        address= findViewById(R.id.doctorLocationTextView)
        city= findViewById(R.id.doctorCityTextView)
        workDays= findViewById(R.id.doctorDaysTextView)
        startTime= findViewById(R.id.doctorStartTextView)
        endTime= findViewById(R.id.doctorFinishTextView)
        val isLoggedIn = intent.getIntExtra("logged", 0)
        val email = intent.getStringExtra("email" )
        val inProfileDoctor = intent.getIntExtra("inProfileDoctor", 0)
        val inProfileUser = intent.getIntExtra("inProfileUser", 0)

        doctor = intent.getParcelableExtra("doctor")!!
        //-------------
        favoriteButton = findViewById(R.id.favoriteButton)
        val favoriteText = findViewById<TextView>(R.id.favoriteText)
        //-------------
        callDoctor.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:"+ doctor.phoneNumber)
            startActivity(intent)
        }
        // Check authentication status
        val user = FirebaseAuth.getInstance().currentUser
        if (isLoggedIn == 1) {
            // User is authenticated, show the "Favorite" button
            favoriteButton.visibility = View.VISIBLE
            favoriteText.visibility = View.VISIBLE
            // Set up the initial UI state
            checkIfDoctorInFavorites(user?.email, doctor.pmdc)
            // Set up the click listener
            favoriteButton.setOnClickListener {
                handleFavoriteButtonClick()
            }
        } else {
            // User is not authenticated, hide the "Favorite" button
            favoriteButton.visibility = View.GONE
            favoriteText.visibility = View.GONE

        }
        //---------------------
        doctorImage = findViewById(R.id.DoctorImage)
        firebaseImageUploader = FirebaseImageUploader()


        //
        backButton =  findViewById(R.id.back)



            backButton.setOnClickListener(View.OnClickListener {
                if (inProfileDoctor == 1) {
                    val intent = Intent(this, DoctorProfileActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("logged", isLoggedIn)
                    startActivity(intent)
                } else if(inProfileUser == 1) {
                    backButton.setOnClickListener(View.OnClickListener {
                        val intent = Intent(this, UserProfileActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("logged", isLoggedIn)
                        startActivity(intent)
                    })
                } else {
                    val intent = Intent(this, SearchDoctorBySpeciality::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("logged", isLoggedIn)
                    startActivity(intent)
                }


                // Get doctor information from intent
                val doctor: Doctor? = intent.getParcelableExtra<Doctor>("doctor")

                // Fetch specialities list directly in the activity
                val databaseReferenceSpecialities =
                    FirebaseDatabase.getInstance().getReference("Specialities")
                databaseReferenceSpecialities.addListenerForSingleValueEvent(object :
                    ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val specialitiesList: MutableList<Specialities> = mutableListOf()

                        for (snapshot in dataSnapshot.children) {
                            val specName = snapshot.child("specName").getValue(String::class.java)
                            val specId = snapshot.child("specId").getValue(Int::class.java)
                            val specImage = snapshot.child("specImage").getValue(String::class.java)

                            if (specName != null && specId != null && specImage != null) {
                                val speciality = Specialities(specName, specId, specImage)
                                specialitiesList.add(speciality)
                            }
                        }

                        doctor?.let {
                            val doctorSpecId = it.specialityId
                            val doctorSpeciality =
                                specialitiesList.find { it.specId == doctorSpecId }

                            // Display doctor information in your UI elements
                            doctorName.text = " ${it.name}"
                            doctorEmail.text = " ${it.email}"
                            doctorGender.text = " ${it.gender}"
                            //experience.text = "Experience: ${it.experience}"

                            // Display speciality information if found
                            doctorSpeciality?.let {
                                specialityy.text = " ${it.specName}"
                            }

                            consultPrice.text = " ${it.consultPrice}"

                            //consultPriceInfo.text = " ${it.consultPriceInfo}"
                            address.text = " ${it.address}"
                            city.text = " ${it.city}"
                            workDays.text = " ${it.selectedDays}"
                            startTime.text = " ${it.startTime}"
                            endTime.text = " ${it.endTime}"

                            loadDoctorImage(it.pmdc)
                            //
                            val experienceLayout = findViewById<LinearLayout>(R.id.ExperienceLayout)
                            val priceInfoLayout = findViewById<LinearLayout>(R.id.PriceInfoLayout)

                            if (it.consultPriceInfo.isNullOrEmpty()) {

                                priceInfoLayout.visibility = View.GONE
                            } else {

                                priceInfoLayout.visibility = View.VISIBLE
                                val doctorPriceInfoTextView =
                                    findViewById<TextView>(R.id.doctorPriceInfoTextView)
                                doctorPriceInfoTextView.text = it.consultPriceInfo
                            }

                            if (it.experience.isNullOrEmpty()) {

                                experienceLayout.visibility = View.GONE
                            } else {

                                experienceLayout.visibility = View.VISIBLE
                                val doctorExperienceTextView =
                                    findViewById<TextView>(R.id.doctorExperienceTextView)
                                doctorExperienceTextView.text = it.experience
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle the error
                        Toast.makeText(
                            this@DoctorInfoActivity,
                            "DataBase Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

            })

    }
//-0------------------------------------
private fun checkIfDoctorInFavorites(userEmail: String?, doctorPMDC: String?) {
    val favoritesReference = FirebaseDatabase.getInstance().getReference("Favorites")
    val userFavoritesReference = userEmail?.replace(".", ",")?.let { favoritesReference.child(it) }

    userFavoritesReference?.child("doctors")?.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val isDoctorInFavorites = dataSnapshot.children.any { it.getValue(String::class.java) == doctorPMDC }
            updateFavoriteButtonUI(isDoctorInFavorites)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle the error
            Toast.makeText(this@DoctorInfoActivity, "Failed to check favorites", Toast.LENGTH_SHORT).show()
        }
    })
}
    private fun updateFavoriteButtonUI(isDoctorInFavorites: Boolean) {
        // Update the drawable of the favorite button based on the doctor's status in favorites
        val drawableResId = if (isDoctorInFavorites) R.drawable.heart_icon else R.drawable.gray_heart
        favoriteButton.setBackgroundResource(drawableResId)
    }


    //----------------------------------------------
    private fun loadDoctorImage(imagePMDC: String) {
        firebaseImageUploader.getImageDownloadUrl(imagePMDC,
            { uri ->
                // Loading the image into the ImageView using Picasso
                Picasso.get().load(uri).placeholder(R.drawable.homeimg).into(doctorImage)
            },
            {
                // Handle failure to get image URL
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun handleFavoriteButtonClick() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        // Check if the user is logged in
        if (userEmail != null) {
            val isDoctorInFavorites = favoriteButton.isSelected

            if (isDoctorInFavorites) {
                // Remove the doctor from favorites
                removeDoctorFromFavorites(userEmail, doctor.pmdc)
               // Toast.makeText(this, "Doctor already in your favorite list. check your profile page to see this list!", Toast.LENGTH_SHORT).show()
            } else {
                // Add the doctor to favorites
                addDoctorToFavorites(userEmail, doctor.pmdc)
            }

            // Toggle the selected state of the button
            favoriteButton.isSelected = !isDoctorInFavorites
        }
    }


    private fun addDoctorToFavorites(userEmail: String, doctorPMDC: String) {
        // Update the Firebase database to add the doctor to the user's favorites list
        val favoritesReference = FirebaseDatabase.getInstance().getReference("Favorites")
        val userFavoritesReference = favoritesReference.child(userEmail.replace(".", ","))

        // Check if the doctor is already in the favorites
        userFavoritesReference.child("doctors").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isDoctorAlreadyAdded = dataSnapshot.children.any { it.getValue(String::class.java) == doctorPMDC }

                if (!isDoctorAlreadyAdded) {
                    // Doctor is not in favorites, add it
                    userFavoritesReference.child("doctors").push().setValue(doctorPMDC)
                    Toast.makeText(this@DoctorInfoActivity, "Doctor added to favorites", Toast.LENGTH_SHORT).show()
                    updateFavoriteButtonUI(true)
                } else {
                    // Doctor is already in favorites, show a message or handle as needed
                  //  Toast.makeText(this@DoctorInfoActivity, "Doctor already in your favorites list", Toast.LENGTH_SHORT).show()
                    removeDoctorFromFavorites(userEmail, doctor.pmdc)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Toast.makeText(this@DoctorInfoActivity, "Failed to check favorites", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeDoctorFromFavorites(userEmail: String?, doctorPMDC: String?) {
        favoriteButton.setBackgroundResource(R.drawable.gray_heart)

        // Update the Firebase database to remove the doctor from the user's favorites list
        val favoritesReference = FirebaseDatabase.getInstance().getReference("Favorites")
        val userFavoritesReference = userEmail?.replace(".", ",")
            ?.let { favoritesReference.child(it) }
        userFavoritesReference?.child("doctors")?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Find the key associated with the doctor in the user's favorites list
                val doctorKey = dataSnapshot.children.find { it.getValue(String::class.java) == doctorPMDC }?.key

                // Remove the doctor from the favorites list
                doctorKey?.let { userFavoritesReference.child("doctors").child(it).removeValue() }

                // Update the UI
                updateFavoriteButtonUI(false)

                Toast.makeText(this@DoctorInfoActivity, "Doctor removed from favorites", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Toast.makeText(this@DoctorInfoActivity, "Failed to remove from favorites", Toast.LENGTH_SHORT).show()
            }
        })
    }

}