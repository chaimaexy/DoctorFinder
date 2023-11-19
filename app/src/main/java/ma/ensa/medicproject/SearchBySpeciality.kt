package ma.ensa.medicproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class SearchBySpeciality : AppCompatActivity() {

    private lateinit var spinnerSpeciality: Spinner

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_speciality)

        //
        val btnBack: ImageButton = findViewById(R.id.btnCancel)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("Specialities")

        fetchSpecialtiesData()


        // Populate the Spinner with city names
        val spinnerLocation: Spinner = findViewById(R.id.spinnerLocation)


        val cityArray = resources.getStringArray(R.array.moroccan_cities)
        val adapter0 = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityArray)
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocation.adapter = adapter0

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
        spinnerSpeciality = findViewById(R.id.spinnerSpecialty)
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



