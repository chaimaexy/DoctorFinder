package ma.ensa.medicproject

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

class SearchBySpeciality : AppCompatActivity() {

    private lateinit var spinnerSpeciality: Spinner


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
        val spinnerLocation: Spinner = findViewById(R.id.spinnerLocation)

        // Populate the Spinner with city names
        val cityArray = resources.getStringArray(R.array.moroccan_cities)
        val adapter0 = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityArray)
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocation.adapter = adapter0


        //
        spinnerSpeciality = findViewById(R.id.spinnerSpecialty)
        val spinnerItems = listOf(
            SpinnerItem("Select a Speciality :", 0),
            SpinnerItem("Cardiologist", R.drawable.back),
            SpinnerItem("Dermatologist", R.drawable.home),


        )
        // Create a custom adapter for the Spinner
        val adapter = CustomSpinnerAdapter(spinnerItems)
        spinnerSpeciality.adapter = adapter
    }

    private inner class CustomSpinnerAdapter(private val items: List<SpinnerItem>) : BaseAdapter() {

        override fun getCount(): Int = items.size

        override fun getItem(position: Int): Any = items[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun isEnabled(position: Int): Boolean {
            return position != 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = layoutInflater.inflate(R.layout.speciality_item, parent, false)

            val item = items[position]
            val imageView: ImageView = view.findViewById(R.id.spinnerItemImage)
            val textView: TextView = view.findViewById(R.id.spinnerItemText)

            imageView.setImageResource(item.imageResource)
            textView.text = item.text

            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return getView(position, convertView, parent)
        }
    }


}



