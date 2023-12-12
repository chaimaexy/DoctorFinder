package ma.ensa.medicproject

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import ma.ensa.medicproject.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    //
    private lateinit var selectedCity: String
    private lateinit var selectedSpecialityId: String
    private lateinit var latitude: String
    private lateinit var longitude: String
    // Reference to the doctors in the Realtime Database
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

            val selectedSpecialityId = intent.getIntExtra("selectedSpecialityId", 0)
            selectedCity = intent.getStringExtra("selectedCity").toString()
            latitude = intent.getDoubleExtra("latitude", 0.0).toString()
            longitude = intent.getDoubleExtra("longitude", 0.0).toString()

        // Initialize the Realtime Database reference
        database = FirebaseDatabase.getInstance().reference.child("Doctors")
        // Display a toast with the collected information
       // Toast.makeText(this, "current: ${latitude} , ${longitude}  ", Toast.LENGTH_LONG).show()
        // Fetch doctors based on selectedCity and selectedSpecialityId
       fetchDoctors(selectedCity, selectedSpecialityId)
    }
    private fun fetchDoctors(city: String, specialityId: Int) {
        // Query doctors based on the selected city and speciality
        val query: Query = database.orderByChild("city").equalTo(city)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val doctorsList = mutableListOf<Doctor>()

                for (snapshot in dataSnapshot.children) {
                    val doctor = snapshot.getValue(Doctor::class.java)
                    if (doctor != null && doctor.specialityId == specialityId) {
                        doctorsList.add(doctor)
                    }
                }
                for (doctor in doctorsList) {
                    val doctorLocation = LatLng(doctor.latitude.toDouble(), doctor.longitude.toDouble())
                    mMap.addMarker(
                        MarkerOptions().position(doctorLocation).title(doctor.name)
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Toast.makeText(this@MapActivity, "Error fetching doctors", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable the My Location layer if the permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            // Request the permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }

        // Get the latitude and longitude from the intent
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // Move the camera to the selected location
        val selectedLocation = LatLng(latitude, longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 12f))

        // Register the MapActivity as the OnMarkerClickListener
        //mMap.setOnMarkerClickListener(this)
    }

    private val MY_LOCATION_REQUEST_CODE = 100


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable the My Location layer
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                mMap.isMyLocationEnabled = true
            } else {
                // Permission denied, handle it as needed (show a message, request again, etc.)
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Handle marker click here
        if (marker != null) {
            val doctorName = marker.title
            val doctorLocation = LatLng(marker.position.latitude, marker.position.longitude)

            // Construct a URI for navigation using Google Maps
            val uri = "google.navigation:q=${doctorLocation.latitude},${doctorLocation.longitude}"

            // Create an Intent to launch Google Maps with the navigation URI
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")

            // Check if the user has Google Maps installed
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)

            } else {
                // Handle the case where Google Maps is not installed
                Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
            }

            Toast.makeText(this, "Clicked on marker for $doctorName", Toast.LENGTH_SHORT).show()
        }

        // Return true to indicate that the click event is consumed
        return true
    }


}