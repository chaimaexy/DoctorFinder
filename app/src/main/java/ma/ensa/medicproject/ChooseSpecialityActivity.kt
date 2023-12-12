package ma.ensa.medicproject

import android.annotation.SuppressLint
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.Locale

class ChooseSpecialityActivity : AppCompatActivity() {
    private lateinit var spinnerSpeciality: Spinner
    private var selectedSpecialityId: Int = 0
    private lateinit var database: DatabaseReference
    private lateinit var spinnerLocation: Spinner
    private lateinit var selectedCity : String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var locationManager: LocationManager
    //
    private lateinit var fusedLocationProviderClient :FusedLocationProviderClient
    private var REQUEST_CODE = 100
    private var email: String? = null
    private var isLoggedIn: Int? = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_speciality)

        isLoggedIn = intent.getIntExtra("logged", 0)
        email = intent.getStringExtra("email" )
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            backButtonClickListener()
        }
        database = FirebaseDatabase.getInstance().reference.child("Specialities")
       //
        spinnerLocation = findViewById(R.id.spinnerCity)

        val cityArray = resources.getStringArray(R.array.moroccan_cities)
        val adapter0 = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityArray)
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocation.adapter = adapter0

        spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCity = parent?.getItemAtPosition(position).toString()
              //  Toast.makeText(this@ChooseSpecialityActivity, "Selected City: $selectedCity", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@ChooseSpecialityActivity, "No city selected!", Toast.LENGTH_SHORT).show()
            }
        }


        //
        spinnerSpeciality = findViewById(R.id.spinnerSpecialty)
        fetchSpecialtiesData()
        spinnerSpeciality.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSpeciality = parent?.getItemAtPosition(position) as? Specialities
                if (selectedSpeciality != null && position != 0) {
                    selectedSpecialityId = selectedSpeciality.specId
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

                Toast.makeText(this@ChooseSpecialityActivity, "No speciality selected!", Toast.LENGTH_SHORT).show()
            }
        }
        getLastLocation()
        val btnsearch: Button = findViewById(R.id.doctor_sign_in_button1)
        btnsearch.setOnClickListener {

            if (selectedSpecialityId != 0 && selectedCity.isNotEmpty() ) {
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                intent.putExtra("selectedSpecialityId", selectedSpecialityId)
                intent.putExtra("selectedCity", selectedCity)

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@ChooseSpecialityActivity, "Please select a valid speciality", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        backButtonClickListener()
    }

    private fun backButtonClickListener() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("logged", isLoggedIn)
        intent.putExtra("email",email )
        startActivity(intent)
        finish()
    }


    private fun fetchSpecialtiesData() {
        val specialtiesList = mutableListOf<Specialities>()

        // Read data from Firebase
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val specId = dataSnapshot.child("specId").getValue(Int::class.java) ?: 0
                    val specName = dataSnapshot.child("specName").getValue(String::class.java) ?: ""
                    val specImage = dataSnapshot.child("specImage").getValue(String::class.java) ?: ""

                    specialtiesList.add(Specialities(specName,specId, specImage))
                }

                // After fetching data, update the spinner
                updateSpinner(specialtiesList)
            }

            override fun onCancelled(error: DatabaseError) {
                //Error
            }
        })


        // get user location info
        //========================


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun getLastLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener {loc ->
                    if(loc  != null){
                        latitude = loc.latitude.toDouble()
                        longitude = loc.longitude.toDouble()
                      //  Toast.makeText(this, "lat: $latitude", Toast.LENGTH_SHORT).show()
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses: List<Address>? = geocoder.getFromLocation(
                            loc .latitude.toDouble(),
                            loc .longitude.toDouble(),
                            1
                        )



                    }
                }
        }else{
            askPermission()
        }
    }


    private fun updateSpinner(specialtiesList: List<Specialities>) {
        // Add "Select a Speciality" as the first item
        val items = mutableListOf(Specialities("Select a Speciality",0,  ""))
        items.addAll(specialtiesList)

        // Create a custom adapter for the Spinner
        val adapter = CustomSpinnerAdapter(items)

        spinnerSpeciality.adapter = adapter
    }
    private inner class CustomSpinnerAdapter(private val items: MutableList<Specialities>) : BaseAdapter() {

        override fun getCount(): Int = items.size

        override fun getItem(position: Int): Any = items[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun isEnabled(position: Int): Boolean {
            return position != 0
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = layoutInflater.inflate(R.layout.speciality_item, parent, false)

            val item = items[position]
            val imageView: ImageView = view.findViewById(R.id.spinnerItemImage)
            val textView: TextView = view.findViewById(R.id.spinnerItemText)

            if (item.specImage.isNotBlank()) {
                Picasso.get()
                    .load(item.specImage)
                    .placeholder(R.drawable.placeholder_image) // Placeholder image resource
                    .error(R.drawable.error) // Error image resource
                    .into(imageView)
            } else {
                // Handle the case when the image path is empty
                imageView.setImageResource(R.drawable.placeholder_image)
            }
            textView.text = item.specName


            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return getView(position, convertView, parent)
        }
    }

    private fun askPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Request denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}