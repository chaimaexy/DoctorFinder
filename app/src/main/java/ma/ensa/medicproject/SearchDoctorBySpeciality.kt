package ma.ensa.medicproject;

import DoctorAdapter
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ma.ensa.medicproject.Doctor
import ma.ensa.medicproject.R
import ma.ensa.medicproject.Specialities
import ma.ensa.medicproject.SpecialitiesAdapter
import java.util.Locale

class SearchDoctorBySpeciality : AppCompatActivity() {
    private var originalDoctorsList: MutableList<Doctor> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_doctor_by_speciality)

        // RecyclerView for Specialities
        val recyclerViewSpecialities: RecyclerView = findViewById(R.id.recyclerViewSpecialities)
        val layoutManagerSpecialities = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewSpecialities.layoutManager = layoutManagerSpecialities

        val specialitiesList: MutableList<Specialities> = mutableListOf()

        val databaseReferenceSpecialities = FirebaseDatabase.getInstance().getReference("Specialities")

        databaseReferenceSpecialities.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val specName = snapshot.child("specName").getValue(String::class.java)
                    val specId = snapshot.child("specId").getValue(Int::class.java)
                    val specImage = snapshot.child("specImage").getValue(String::class.java)

                    if (specName != null && specId != null && specImage != null) {
                        val speciality = Specialities(specName, specId, specImage)
                        specialitiesList.add(speciality)
                    }
                }

                // Now, specialitiesList contains your Speciality objects
                // You can use this list to populate your RecyclerView adapter
                val adapter = SpecialitiesAdapter(specialitiesList)
                recyclerViewSpecialities.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Log.e("Firebase", "Error reading specialities data", databaseError.toException())
            }
        })




        // RecyclerView for Doctors
        val recyclerViewDoctors: RecyclerView = findViewById(R.id.recyclerViewDoctors)
        val layoutManagerDoctors = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewDoctors.layoutManager = layoutManagerDoctors

        // Fetch Doctors from Database
        val databaseReferenceDoctors = FirebaseDatabase.getInstance().getReference("Doctors")
        databaseReferenceDoctors.addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                originalDoctorsList.clear()
                val doctorsList: MutableList<Doctor> = mutableListOf()

                for (snapshot in dataSnapshot.children) {
                    val doctor = snapshot.getValue(Doctor::class.java)
                    if (doctor != null) {
                        originalDoctorsList.add(doctor)
                        doctorsList.add(doctor)
                    }
                }

                val adapter = DoctorAdapter(doctorsList, specialitiesList)
                recyclerViewDoctors.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Log.e("Firebase", "Error reading doctors data", databaseError.toException())
            }
        })

        // filter for doctors (by search)

        val searchDoctorEditText: EditText = findViewById(R.id.searchDoctorEditText)
        val searchDoctorButton: ImageView = findViewById(R.id.searchDoctorButton)

        // Set up a click listener for the search button
        searchDoctorButton.setOnClickListener {
            val searchText = searchDoctorEditText.text.toString().toLowerCase(Locale.getDefault())
            val filteredDoctorsList: List<Doctor> = originalDoctorsList.filter {
                it.name.toLowerCase(Locale.getDefault()).contains(searchText) || it.gender.toLowerCase(Locale.getDefault()).contains(searchText)
            }

            // Update the adapter with the filtered list
            (recyclerViewDoctors.adapter as DoctorAdapter).updateList(filteredDoctorsList)
        }
    }
}