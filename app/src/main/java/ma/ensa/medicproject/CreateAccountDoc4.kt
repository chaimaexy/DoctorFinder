package ma.ensa.medicproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class  CreateAccountDoc4 : AppCompatActivity() {
    // member variables
    private lateinit var doctorName: String
    private lateinit var doctorEmail: String
    private lateinit var doctorPassword: String
    private lateinit var selectedGender: String
    private var selectedImageUri: Uri? = null
    private lateinit var doctorPMDC: String
    private lateinit var doctorExperience: String
    private var selectedSpecialityId: Int = 0
    private lateinit var consultPrice: String
    private lateinit var consultPriceInfo: String
    private lateinit var adresse: String
    private lateinit var location: String
    private lateinit var selectedCity: String
    //
    private lateinit var checkBoxMonday: CheckBox
    private lateinit var checkBoxTuesday: CheckBox
    private lateinit var checkBoxWednesday: CheckBox
    private lateinit var checkBoxThursday: CheckBox
    private lateinit var checkBoxFriday: CheckBox
    private lateinit var checkBoxSaturday: CheckBox
    private lateinit var checkBoxSunday: CheckBox

    private lateinit var hourStartPicker: NumberPicker
    private lateinit var minuteStartPicker: NumberPicker
    private lateinit var hourEndPicker: NumberPicker
    private lateinit var minuteEndPicker: NumberPicker
    private lateinit var btnNext: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_doc4)

        // Initialize views
        checkBoxMonday = findViewById(R.id.checkBoxMonday)
        checkBoxTuesday = findViewById(R.id.checkBoxTuesday)
        checkBoxWednesday = findViewById(R.id.checkBoxWednesday)
        checkBoxThursday = findViewById(R.id.checkBoxThursday)
        checkBoxFriday = findViewById(R.id.checkBoxFriday)
        checkBoxSaturday = findViewById(R.id.checkBoxSaturday)
        checkBoxSunday = findViewById(R.id.checkBoxSunday)

        hourStartPicker = findViewById(R.id.hourStart)
        minuteStartPicker = findViewById(R.id.minuteStart)
        hourEndPicker = findViewById(R.id.hourEnd)
        minuteEndPicker = findViewById(R.id.minuteEnd)

        btnNext = findViewById(R.id.doctor_sign_in_button)

        //get old inputs
        doctorName = intent.getStringExtra("doctorName") ?: ""
        doctorEmail = intent.getStringExtra("doctorEmail") ?: ""
        doctorPassword = intent.getStringExtra("doctorPassword") ?: ""
        selectedGender = intent.getStringExtra("doctorGender") ?: ""
        selectedImageUri = intent.getParcelableExtra("selectedImage")
        doctorPMDC = intent.getStringExtra("doctorPMDC") ?: ""
        doctorExperience = intent.getStringExtra("doctorExperience") ?: ""
        selectedSpecialityId = intent.getIntExtra("selectedSpecialityId", 0)
        consultPrice = intent.getStringExtra("consultPrice") ?: ""
        consultPriceInfo = intent.getStringExtra("consultPriceInfo") ?: ""
        adresse = intent.getStringExtra("adresse") ?: ""
        location = intent.getStringExtra("location") ?: ""
        selectedCity = intent.getStringExtra("selectedCity") ?: ""


        // Set up NumberPickers
        setUpNumberPickers()

        //  Next button
        btnNext.setOnClickListener {
            val imageUploader = FirebaseImageUploader()

            // Check if selectedImageUri is not null before uploading
            selectedImageUri?.let {
                imageUploader.uploadImage(it, doctorPMDC)
                Toast.makeText(this@CreateAccountDoc4, "image upload succesful", Toast.LENGTH_SHORT).show()

            } ?: run {
                Toast.makeText(this@CreateAccountDoc4, "No image found", Toast.LENGTH_SHORT).show()
            }
            onNextButtonClick()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("doctorPMDC", doctorPMDC)
            intent.putExtra("doctorName", doctorName)
            intent.putExtra("logged", 1)


            startActivity(intent)

        }





//use old data
        findViewById<TextView>(R.id.textViewDoctorName).text = "Doctor Name: $doctorName"

        //use old data
        findViewById<TextView>(R.id.textViewDoctorName).text = "Doctor Name: $doctorName"
        findViewById<TextView>(R.id.textViewDoctorEmail).text = "Doctor Email: $doctorEmail"
        findViewById<TextView>(R.id.textViewDoctorPassword).text = "Doctor Password: $doctorPassword"
        this.findViewById<TextView>(R.id.textViewDoctorGender).text = "Doctor gender: $selectedGender"
        val imageViewSelectedImage = findViewById<ImageView>(R.id.imageViewSelectedImage)
        if (selectedImageUri != null) {
            imageViewSelectedImage.setImageURI(selectedImageUri)
        } else {
            Log.e("CreateAccountDoc2", "selectedImageUri is null")

        }
        findViewById<TextView>(R.id.textViewPMDC).text = "Doctor PMDC: $doctorPMDC"
        findViewById<TextView>(R.id.textViewDoctorExperience).text = "Doctor Experience: $doctorExperience"
        findViewById<TextView>(R.id.textViewDoctorSpeciality).text = "Doctor spec: $selectedSpecialityId"

        findViewById<TextView>(R.id.textViewDoctorPrice).text = "Doctor spec: $consultPrice"
        findViewById<TextView>(R.id.textViewDoctorAdresse).text = "Doctor spec: $consultPriceInfo"
        findViewById<TextView>(R.id.textViewDoctorPriceInfo).text = "Doctor spec: $adresse"
        findViewById<TextView>(R.id.textViewDoctorLocation).text = "Doctor location: $location"
        findViewById<TextView>(R.id.textViewDoctorCity).text = "Doctor Name: $selectedCity"

        //back
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, CreateAccoutDoc3::class.java)
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
            startActivity(intent)
        }
        //cancel
        val btnCancel: ImageButton = findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
    private fun setUpNumberPickers() {
        // Set up the range for hour and minute pickers
        hourStartPicker.minValue = 0
        hourStartPicker.maxValue = 23
        minuteStartPicker.minValue = 0
        minuteStartPicker.maxValue = 59

        hourEndPicker.minValue = 0
        hourEndPicker.maxValue = 23
        minuteEndPicker.minValue = 0
        minuteEndPicker.maxValue = 59
    }

    private fun getSelectedDays(): List<String> {
        val selectedDays = mutableListOf<String>()
        if (checkBoxMonday.isChecked) selectedDays.add("Monday")
        if (checkBoxTuesday.isChecked) selectedDays.add("Tuesday")
        if (checkBoxWednesday.isChecked) selectedDays.add("Wednesday")
        if (checkBoxThursday.isChecked) selectedDays.add("Thursday")
        if (checkBoxFriday.isChecked) selectedDays.add("Friday")
        if (checkBoxSaturday.isChecked) selectedDays.add("Saturday")
        if (checkBoxSunday.isChecked) selectedDays.add("Sunday")
        return selectedDays
    }

    private fun onNextButtonClick() {
        // Retrieve selected days
        val selectedDays = getSelectedDays()

        if (selectedDays.isNotEmpty()) {
            // Retrieve selected start time
            val startHour = hourStartPicker.value
            val startMinute = minuteStartPicker.value

            // Retrieve selected end time
            val endHour = hourEndPicker.value
            val endMinute = minuteEndPicker.value

            // Create a Doctor object with all the collected information
            val doctor = Doctor(
                doctorName,
                doctorEmail,
                doctorPassword,
                selectedGender,
                doctorPMDC,
                doctorExperience,
                selectedSpecialityId,
                consultPrice,
                consultPriceInfo,
                adresse,
                location,
                selectedCity,
                selectedDays,
                "$startHour:$startMinute",
                "$endHour:$endMinute"
            )
            saveDoctorToFirebase(doctor)
        }else{
            Toast.makeText(this, "Please select at least one day..", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDoctorToFirebase(doctor: Doctor) {
        // Replace the following placeholders with your actual Firebase project information
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Doctors") // "Doctors" is the parent node

        // Generate a unique key for the Doctor entry
        val doctorKey = reference.push().key

        // Save the Doctor object under the generated key
        if (doctorKey != null) {
            reference.child(doctorKey).setValue(doctor)
            Toast.makeText(this, "Doctor information saved ", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error generating Doctor key", Toast.LENGTH_SHORT).show()
        }
    }
}



