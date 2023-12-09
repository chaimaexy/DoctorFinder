package ma.ensa.medicproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ModifieDoctorInfo : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var experienceEditText: EditText
    private lateinit var consultPriceEditText: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var mondayCheckBox: CheckBox
    private lateinit var passwordConfirmEditText : EditText
    private lateinit var tuesdayCheckBox: CheckBox
    private lateinit var wednesdayCheckBox: CheckBox
    private lateinit var thursdayCheckBox: CheckBox
    private lateinit var fridayCheckBox: CheckBox
    private lateinit var saturdayCheckBox: CheckBox
    private lateinit var validateButton: Button
    private lateinit var cancelButton: Button
    private lateinit var hourStart: NumberPicker
    private lateinit var minuteStart: NumberPicker
    private lateinit var hourEnd: NumberPicker
    private lateinit var minuteEnd: NumberPicker
    private lateinit var currentDoctorEmail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifie_doctor_info)

// Initialize views
        nameEditText = findViewById(R.id.editTextName)
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        passwordConfirmEditText = findViewById(R.id.editTextPasswordConfirm)
        experienceEditText = findViewById(R.id.editTextExperience)
        editTextPhone = findViewById(R.id.editTextPhone)
        consultPriceEditText = findViewById(R.id.editTextConsultPrice)
        mondayCheckBox = findViewById(R.id.checkBoxMonday)
        tuesdayCheckBox = findViewById(R.id.checkBoxTuesday)
        wednesdayCheckBox = findViewById(R.id.checkBoxWednesday)
        thursdayCheckBox = findViewById(R.id.checkBoxThursday)
        fridayCheckBox = findViewById(R.id.checkBoxFriday)
        saturdayCheckBox = findViewById(R.id.checkBoxSaturday)
        validateButton = findViewById(R.id.buttonValidate)
        cancelButton = findViewById(R.id.buttonCancel)

        currentDoctorEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
// Set up NumberPicker for hours

        hourStart = findViewById(R.id.hourStart)
        minuteStart = findViewById(R.id.minuteStart)
        hourEnd = findViewById(R.id.hourEnd)
        minuteEnd = findViewById(R.id.minuteEnd)
        hourStart.minValue = 0
        hourStart.maxValue = 23

// Set up NumberPicker for minutes
        minuteStart.minValue = 0
        minuteStart.maxValue = 59
        // Set up NumberPicker for minutes
        hourEnd.minValue = 0
        hourEnd.maxValue = 23

        minuteEnd.minValue = 0
        minuteEnd.maxValue = 59

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Fetch doctor data and populate the fields
        fetchDoctorDataFromDatabase()

        validateButton.setOnClickListener {
            validateChanges()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchDoctorDataFromDatabase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Doctors")

        databaseReference.orderByChild("email").equalTo(currentDoctorEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val doctor = snapshot.getValue(Doctor::class.java)

                        // Update the UI with doctor's data
                        doctor?.let {
                            nameEditText.setText(it.name)
                            emailEditText.setText(it.email)
                            // You might not want to show the password in EditText for security reasons
                            passwordEditText.setText(it.password)
                            experienceEditText.setText(it.experience)
                            editTextPhone.setText(it.phoneNumber)
                            consultPriceEditText.setText(it.consultPrice)



                            // Update CheckBox and NumberPicker based on the selectedDays, startTime, and endTime in Doctor
                            // Example: Set Monday CheckBox checked if "Monday" is in the selectedDays list
                            mondayCheckBox.isChecked = it.selectedDays.contains("Monday")
                            tuesdayCheckBox.isChecked = it.selectedDays.contains("Tuesday")
                            wednesdayCheckBox.isChecked = it.selectedDays.contains("Wednesday")
                            thursdayCheckBox.isChecked = it.selectedDays.contains("Thursday")
                            fridayCheckBox.isChecked = it.selectedDays.contains("Friday")
                            saturdayCheckBox.isChecked = it.selectedDays.contains("Saturday")


                            // Initialize NumberPickers for startTime and endTime
                            val startTimeParts = it.startTime.split(":")
                            val endTimeParts = it.endTime.split(":")

                            hourStart.value = startTimeParts[0].toInt()
                            minuteStart.value = startTimeParts[1].toInt()

                            hourEnd.value = endTimeParts[0].toInt()
                            minuteEnd.value = endTimeParts[1].toInt()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle data fetching failure
                    Toast.makeText(
                        this@ModifieDoctorInfo,
                        "Failed to fetch doctor data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun validateChanges() {
        val newName = nameEditText.text.toString().trim()
        val newEmail = emailEditText.text.toString().trim()
        val newPassword = passwordEditText.text.toString().trim()
        val newPasswordConfirm = passwordConfirmEditText.text.toString().trim()
        val newExperience = experienceEditText.text.toString().trim()
        val newPhone = editTextPhone.text.toString().trim()
        val newConsultPrice = consultPriceEditText.text.toString().trim()
        val newSelectedDays = getSelectedDays()
        val newStartTime = getStartTime()
        val newEndTime = getEndTime()

        if (newName.isEmpty() || newEmail.isEmpty()|| newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
            Toast.makeText(
                this,
                "Name, Email, Password, and Confirm Password cannot be empty",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (newPassword != newPasswordConfirm) {
            Toast.makeText(
                this,
                "Password and Confirm Password do not match",
                Toast.LENGTH_SHORT
            ).show()
            return
        }else {
            // Update the doctor's data in the database
            updateDoctorProfile(
                newName,
                newEmail,
                newPassword,
                newExperience,
                newConsultPrice,
                newSelectedDays,
                newStartTime,
                newEndTime,
                newPhone
            )
        }
    }

    private fun getSelectedDays(): List<String> {
        val selectedDays = mutableListOf<String>()

        if (mondayCheckBox.isChecked) selectedDays.add("Monday")
        if (tuesdayCheckBox.isChecked) selectedDays.add("Tuesday")
        if (wednesdayCheckBox.isChecked) selectedDays.add("Wednesday")
        if (thursdayCheckBox.isChecked) selectedDays.add("Thursday")
        if (fridayCheckBox.isChecked) selectedDays.add("Friday")
        if (saturdayCheckBox.isChecked) selectedDays.add("Saturday")

        return selectedDays
    }


    private fun getStartTime(): String {
        val hourStart = hourStart.value.toString().padStart(2, '0')
        val minuteStart = minuteStart.value.toString().padStart(2, '0')

        return "$hourStart:$minuteStart"
    }

    private fun getEndTime(): String {
        val hourEnd = hourEnd.value.toString().padStart(2, '0')
        val minuteEnd = minuteEnd.value.toString().padStart(2, '0')

        return "$hourEnd:$minuteEnd"
    }

    private fun updateDoctorProfile(
        newName: String,
        newEmail: String,
        newPassword: String,
        newExperience: String,
        newConsultPrice: String,
        newSelectedDays: List<String>,
        newStartTime: String,
        newEndTime: String,
        newPhone: String
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Doctors")

        databaseReference.orderByChild("email").equalTo(currentDoctorEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        // Assuming each child corresponds to a doctor node
                        val doctor = snapshot.getValue(Doctor::class.java)

                        // Update doctor's data
                        doctor?.let {
                            it.name = newName
                            it.email = newEmail
                            // You might want to add logic to update the password securely
                            it.experience = newExperience
                            it.consultPrice = newConsultPrice
                            it.selectedDays = newSelectedDays
                            it.startTime = newStartTime
                            it.endTime = newEndTime
                            it.phoneNumber = newPhone

                            // Save the updated doctor
                            snapshot.ref.setValue(it)

                            Toast.makeText(
                                this@ModifieDoctorInfo,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Finish the activity after successful update
                            finish()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle update failure
                    Toast.makeText(
                        this@ModifieDoctorInfo,
                        "Failed to update doctor profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}