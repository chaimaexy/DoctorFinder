package ma.ensa.medicproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast

class CreateAccountDoc4 : AppCompatActivity() {

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

        // Set up NumberPickers
        setUpNumberPickers()

        // Set up click listener for the Next button
        btnNext.setOnClickListener {
            onNextButtonClick()
        }
        //
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, CreateAccoutDoc3::class.java)
            startActivity(intent)
        }
        //
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

        // Retrieve selected start time
        val startHour = hourStartPicker.value
        val startMinute = minuteStartPicker.value

        // Retrieve selected end time
        val endHour = hourEndPicker.value
        val endMinute = minuteEndPicker.value

        // Add your logic here to handle the selected days and times
        // For now, display a toast with the selected information
        val message = "Selected Days: $selectedDays\nStart Time: $startHour:$startMinute\nEnd Time: $endHour:$endMinute"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}



