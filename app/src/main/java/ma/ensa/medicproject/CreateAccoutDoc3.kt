package ma.ensa.medicproject

import android.content.Intent
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class CreateAccoutDoc3 : AppCompatActivity() , LocationListener {
    //remove test
    private var selectedImageUri: Uri? = null
    //
    private lateinit var consultPrice: EditText
    private lateinit var doctorPhone : EditText
    private lateinit var consultPriceInfo: EditText
    private lateinit var Doclocation: EditText
    private lateinit var selectedCity : String

    //map
    private lateinit var locationManager: LocationManager
    //spinner location
    private lateinit var spinnerLocation: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_accout_doc3)

        //maps runtime permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )

        }

        //get old inputs
        val doctorName = intent.getStringExtra("doctorName")
        val doctorEmail = intent.getStringExtra("doctorEmail")
        val doctorPassword = intent.getStringExtra("doctorPassword")
        val selectedGender = intent.getStringExtra("doctorGender")
        selectedImageUri = intent.getParcelableExtra("selectedImage")
        val doctorPMDC = intent.getStringExtra("doctorPMDC")
        val doctorExperience = intent.getStringExtra("doctorExperience")
        val selectedSpecialityId = intent.getIntExtra("selectedSpecialityId", 0)


        // city spinner
        // Populate the Spinner with city names
        spinnerLocation = findViewById(R.id.spinnerCity)

        val cityArray = resources.getStringArray(R.array.moroccan_cities)
        val adapter0 = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityArray)
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocation.adapter = adapter0

        spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCity = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@CreateAccoutDoc3, "Selected City: $selectedCity", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@CreateAccoutDoc3, "No city selected!", Toast.LENGTH_SHORT).show()
            }
        }
        //back
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, CreateAccountDoc2::class.java)
            intent.putExtra("doctorName", doctorName)
            intent.putExtra("doctorEmail", doctorEmail)
            intent.putExtra("doctorPassword", doctorPassword)
            intent.putExtra("doctorGender", selectedGender)
            intent.putExtra("selectedImage", selectedImageUri)
            startActivity(intent)
        }
        //cancel
        val btnCancel: ImageButton = findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            backButtonClickListener()
        }
        //new input
        consultPrice = findViewById(R.id.doctorprice)
        consultPriceInfo= findViewById(R.id.consoltPriceInfos)
        Doclocation= findViewById(R.id.Location)
        doctorPhone =  findViewById(R.id.doctorPhone)
        //next
        val btnNext: Button = findViewById(R.id.doctor_sign_in_button)
        btnNext.setOnClickListener {
            val consultPriceValue = consultPrice.text.toString()
            val locationValue = Doclocation.text.toString()
            val doctorPhone = doctorPhone.text.toString()
            val lastKnownLocation = getLastKnownLocation()
            if (consultPriceValue.isNotEmpty() &&
                locationValue.isNotEmpty() &&
                doctorPhone.isNotEmpty() &&
                selectedCity.isNotEmpty()) {
                val intent = Intent(this, CreateAccountDoc4::class.java)
                // put new data
                intent.putExtra("consultPrice", consultPriceValue)
                intent.putExtra("consultPriceInfo", consultPriceInfo.text.toString())
                intent.putExtra("location", locationValue)
                intent.putExtra("selectedCity", selectedCity)
                intent.putExtra("doctorPhone", doctorPhone)
                // put old input2
                intent.putExtra("doctorPMDC", doctorPMDC)
                intent.putExtra("doctorExperience", doctorExperience)
                intent.putExtra("selectedSpecialityId", selectedSpecialityId)
                // put old input1
                intent.putExtra("doctorName", doctorName)
                intent.putExtra("doctorEmail", doctorEmail)
                intent.putExtra("doctorPassword", doctorPassword)
                intent.putExtra("doctorGender", selectedGender)
                intent.putExtra("selectedImage", selectedImageUri)

                // Retrieve the last known location
                if (lastKnownLocation != null) {
//                    val latitude = lastKnownLocation.latitude
//                    val longitude = lastKnownLocation.longitude
                    // Pass the latitude and longitude to the intent
                    intent.putExtra("latitude", lastKnownLocation.latitude)
                    intent.putExtra("longitude", lastKnownLocation.longitude)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Call the same logic as your custom back button when the back button is pressed
        backButtonClickListener()
    }

    private fun backButtonClickListener() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    //map
    fun selectCurrentLocation(view: View) {
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }catch (e: Exception){
            e.printStackTrace();
        }
    }



    override fun onLocationChanged(location: Location) {
       Toast.makeText(this, ""+location.latitude +","+location.longitude , Toast.LENGTH_SHORT).show()
        val geocoder = Geocoder(this@CreateAccoutDoc3, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(
                location.latitude.toDouble(),
                location.longitude.toDouble(),
                1
            )
            if (!addresses.isNullOrEmpty()) {
                val cityName: String = addresses[0].locality ?: "Unknown City"
                Toast.makeText(this, "Current City: $cityName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "City information not available", Toast.LENGTH_SHORT).show()
            }
            if (!addresses.isNullOrEmpty()) {
                val address: String = addresses[0].getAddressLine(0)
                Doclocation.setText(address)
            } else {
                //the case when no address is available
            }        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}