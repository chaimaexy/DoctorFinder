package ma.ensa.medicproject;

import DoctorAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ma.ensa.medicproject.Doctor
import ma.ensa.medicproject.R
import ma.ensa.medicproject.Specialities
import ma.ensa.medicproject.SpecialitiesAdapter
import java.util.Locale

class SearchDoctorBySpeciality : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var originalDoctorsList: MutableList<Doctor> = mutableListOf()
    private var email: String? = null
    private var isLoggedIn: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_doctor_by_speciality)

        //menu--------------------
        val drawer: DrawerLayout = findViewById(R.id.drawerLayout)
        val Navigation: NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Navigation.bringToFront()
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        Navigation.setNavigationItemSelectedListener(this)
        //-------------------------------
        //login status
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val menu = navigationView.menu

        isLoggedIn = intent.getIntExtra("logged", 0)
        email = intent.getStringExtra("email" )
        //-----------------------------

        // RecyclerView for Doctors
        val recyclerViewDoctors: RecyclerView = findViewById(R.id.recyclerViewDoctors)
        val layoutManagerDoctors = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewDoctors.layoutManager = layoutManagerDoctors

        //------------------------------
        if (email != null) {

            //menu stuff
            headerView.findViewById<TextView>(R.id.identif).text ="$email"
            headerView.findViewById<Button>(R.id.Login).visibility =View.GONE
            // Update menu items based on login status
            menu.findItem(R.id.Profile).isVisible = true
            menu.findItem(R.id.logout).isVisible = true

        }else{

            headerView.findViewById<TextView>(R.id.identif).text = "Doctor Finder"
            headerView.findViewById<Button>(R.id.Login).visibility =View.VISIBLE
            val loginButton: Button = headerView.findViewById(R.id.Login)
            headerView.setOnClickListener {
            }
            loginButton.setOnClickListener {

                val intent2 = Intent(this , AuthUser::class.java)
                startActivity(intent2)
            }
            menu.findItem(R.id.Profile).isVisible = false
            menu.findItem(R.id.logout).isVisible = false

        }
        //--------------------------------
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


                val adapter = SpecialitiesAdapter(specialitiesList) { selectedSpeciality ->

                    val filteredDoctorsList = originalDoctorsList.filter { it.specialityId == selectedSpeciality.specId }


                    (recyclerViewDoctors.adapter as DoctorAdapter).updateList(filteredDoctorsList)
                }

                recyclerViewSpecialities.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Log.e("Firebase", "Error reading specialities data", databaseError.toException())
            }
        })

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
                doctorsList.sortByDescending { it.clickCounter }
                val doctorAdapter = DoctorAdapter(doctorsList, specialitiesList) { clickedDoctor ->
                    val intent = Intent(this@SearchDoctorBySpeciality, DoctorInfoActivity::class.java)
                    intent.putExtra("doctor", clickedDoctor)
                    intent.putExtra("email", email)
                    intent.putExtra("logged", isLoggedIn)
                    startActivity(intent)
                }
                recyclerViewDoctors.adapter = doctorAdapter
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
                it.name.toLowerCase(Locale.getDefault()).contains(searchText) || it.gender.toLowerCase(Locale.getDefault()).contains(searchText) || it.city.toLowerCase(Locale.getDefault()).contains(searchText)
            }

            // Update the adapter with the filtered list
            (recyclerViewDoctors.adapter as DoctorAdapter).updateList(filteredDoctorsList)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.Profile -> {
                checkUserTypeAndNavigate()
                return true
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)

                intent.putExtra("logged", 0)
                startActivity(intent)
            }
        }
        val drawer: DrawerLayout = findViewById(R.id.drawerLayout)
        drawer.closeDrawer(GravityCompat.START)

        return false
    }
    private fun checkUserTypeAndNavigate() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { firebaseUser ->
            val userEmail = firebaseUser.email

            if (userEmail != null) {
                checkUserType(userEmail)
            } else {
                //error message or redirect to login
            }
        }
    }

    private fun checkUserType(userEmail: String) {
        val databaseReferenceDoctors = FirebaseDatabase.getInstance().getReference("Doctors")
        databaseReferenceDoctors.orderByChild("email").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // The user is a doctor
                        navigateToDoctorProfile()
                    } else {
                        // The user is a regular user
                        navigateToUserProfile()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                    Toast.makeText(this@SearchDoctorBySpeciality, "Database Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun navigateToDoctorProfile() {
        // Navigate to the doctor's profile activity
        val intent = Intent(this, DoctorProfileActivity::class.java)
        intent.putExtra("logged", 1)
        intent.putExtra("email",email )
        startActivity(intent)
    }

    private fun navigateToUserProfile() {
        // Navigate to the regular user's profile activity
        val intent = Intent(this, UserProfileActivity::class.java)
        intent.putExtra("logged", 1)
        intent.putExtra("email",email )
        startActivity(intent)
    }
}