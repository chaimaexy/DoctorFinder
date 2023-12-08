package ma.ensa.medicproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ChooseSpecialityActivity : AppCompatActivity() {
    private lateinit var spinnerSpeciality: Spinner
    private var selectedSpecialityId: Int = 0
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_speciality)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        database = FirebaseDatabase.getInstance().reference.child("Specialities")
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
        val btnsearch: Button = findViewById(R.id.doctor_sign_in_button1)
        btnsearch.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
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