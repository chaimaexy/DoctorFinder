package ma.ensa.medicproject

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_doctor_info)
        doctorName = findViewById(R.id.doctorNameTextView)
        doctorEmail = findViewById(R.id.doctorEmailTextView)
        doctorGender = findViewById(R.id.doctorGenderTextView)

         experience= findViewById(R.id.doctorExperienceTextView)
         specialityy= findViewById(R.id.doctorSpecialityTextView)

         consultPrice= findViewById(R.id.doctorPriceTextView)
         consultPriceInfo= findViewById(R.id.doctorPriceInfoTextView)
        address= findViewById(R.id.doctorLocationTextView)
         city= findViewById(R.id.doctorCityTextView)
         workDays= findViewById(R.id.doctorDaysTextView)
        startTime= findViewById(R.id.doctorStartTextView)
         endTime= findViewById(R.id.doctorFinishTextView)

        doctorImage = findViewById(R.id.DoctorImage)
        firebaseImageUploader = FirebaseImageUploader()
        val isLoggedIn = intent.getIntExtra("logged", 0)
        val email = intent.getStringExtra("email" )

        //
        backButton =  findViewById(R.id.back)
        backButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SearchDoctorBySpeciality::class.java)
            intent.putExtra("email", email)
            intent.putExtra("logged", isLoggedIn)
            startActivity(intent)
        })
        // Get doctor information from intent
        val doctor: Doctor? = intent.getParcelableExtra<Doctor>("doctor")

        // Fetch specialities list directly in the activity
        val databaseReferenceSpecialities = FirebaseDatabase.getInstance().getReference("Specialities")
        databaseReferenceSpecialities.addListenerForSingleValueEvent(object : ValueEventListener {

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
                    val doctorSpeciality = specialitiesList.find { it.specId == doctorSpecId }

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
                        val doctorPriceInfoTextView = findViewById<TextView>(R.id.doctorPriceInfoTextView)
                        doctorPriceInfoTextView.text = it.consultPriceInfo
                    }

                    if (it.experience.isNullOrEmpty()) {

                        experienceLayout.visibility = View.GONE
                    } else {

                        experienceLayout.visibility = View.VISIBLE
                        val doctorExperienceTextView = findViewById<TextView>(R.id.doctorExperienceTextView)
                        doctorExperienceTextView.text = it.experience
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Toast.makeText(this@DoctorInfoActivity, "DataBase Error", Toast.LENGTH_SHORT).show()
            }
        })


    }

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
}