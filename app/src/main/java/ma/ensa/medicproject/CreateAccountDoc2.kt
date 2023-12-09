package ma.ensa.medicproject

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.MultiAutoCompleteTextView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CreateAccountDoc2 : AppCompatActivity() {
    private var selectedSpecialityId: Int = 0
    private lateinit var doctorPMDC: EditText
    private lateinit var doctorExperience: EditText
    private lateinit var spinnerSpeciality: Spinner
    private lateinit var doctorName :String
    private lateinit var doctorPassword :String
    private lateinit var selectedGender :String
    private lateinit var doctorEmail :String


    private lateinit var database: DatabaseReference

    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_doc2)
        //back
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, CreateAccountDoc::class.java)
            startActivity(intent)
            finish()
        }
        //cancel
        val btnCancel: ImageButton = findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        //spinner show
        database = FirebaseDatabase.getInstance().reference.child("Specialities")
        spinnerSpeciality = findViewById(R.id.spinnerSpecialty)
        fetchSpecialtiesData()
        //spinner choice
        spinnerSpeciality.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSpeciality = parent?.getItemAtPosition(position) as? Specialities
                if (selectedSpeciality != null && position != 0) {
                    selectedSpecialityId = selectedSpeciality.specId
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

                Toast.makeText(this@CreateAccountDoc2, "No speciality selected!", Toast.LENGTH_SHORT).show()
            }
        }

        //get old inputs
        doctorName = intent.getStringExtra("doctorName") ?: ""
        doctorEmail = intent.getStringExtra("doctorEmail") ?: ""
        doctorPassword = intent.getStringExtra("doctorPassword") ?: ""
        selectedGender = intent.getStringExtra("doctorGender") ?: ""
        selectedImageUri = intent.getParcelableExtra("selectedImage")




        //next
        val btnNext: Button = findViewById(R.id.doctor_sign_in_button1)

        btnNext.setOnClickListener {
            doctorPMDC = findViewById(R.id.doctorPMDC)
            doctorExperience = findViewById(R.id.doctorExperience)

            val doctorPMDCValue = doctorPMDC.text.toString()

            if (doctorPMDC.text.isNotEmpty() &&
                selectedSpecialityId != 0
            ) {
                checkDoctorPMDCUnique(doctorPMDCValue)
            }else{
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }




    }

    private fun checkDoctorPMDCUnique(doctorPMDCValue: String) {
        val doctorsRef = FirebaseDatabase.getInstance().reference.child("Doctors")

        doctorsRef.orderByChild("doctorPMDC").equalTo(doctorPMDCValue)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Doctor with the same PMDC already exists
                        Toast.makeText(this@CreateAccountDoc2, "Doctor with the same PMDC already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        // DoctorPMDC is unique, proceed to the next step
                        val intent = Intent(this@CreateAccountDoc2, CreateAccoutDoc3::class.java)
                        // Put now inputs
                        intent.putExtra("doctorPMDC", doctorPMDCValue)
                        intent.putExtra("doctorExperience", doctorExperience.text.toString())
                        intent.putExtra("selectedSpecialityId", selectedSpecialityId)
                        // Put old input
                        intent.putExtra("doctorName", doctorName)
                        intent.putExtra("doctorEmail", doctorEmail)
                        intent.putExtra("doctorPassword", doctorPassword)
                        intent.putExtra("doctorGender", selectedGender)
                        intent.putExtra("selectedImage", selectedImageUri)

                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled event
                }
            })
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
}