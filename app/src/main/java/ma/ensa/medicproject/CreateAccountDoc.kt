package ma.ensa.medicproject

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class CreateAccountDoc : AppCompatActivity() {
    private lateinit var imageViewProfile: ImageView
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioButtonMale: RadioButton
    private lateinit var radioButtonFemale: RadioButton
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_doc)
        //
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //
        val btnBack: ImageButton = findViewById(R.id.btnCancel)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //
        val btnNext: Button = findViewById(R.id.doctor_sign_in_button1)
        btnNext.setOnClickListener {
            val intent = Intent(this, CreateAccountDoc2::class.java)
            startActivity(intent)
        //
            imageViewProfile = findViewById(R.id.imageViewProfile)
            radioGroupGender = findViewById(R.id.radioGroupGender)
            radioButtonMale = findViewById(R.id.radioButtonMale)
            radioButtonFemale = findViewById(R.id.radioButtonFemale)

            imagePickerLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val data: Intent? = result.data
                        // Handle the result, similar to the onActivityResult method
                        if (data != null) {
                            val selectedImage = data.data
                            imageViewProfile.setImageURI(selectedImage)
                        }
                    }
                }
        }

        fun selectProfilePicture(view: View) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        fun getSelectedGender(): String {
            return when (radioGroupGender.checkedRadioButtonId) {
                R.id.radioButtonMale -> "Male"
                R.id.radioButtonFemale -> "Female"
                else -> ""
            }
        }

    }
}