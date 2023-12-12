package ma.ensa.medicproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso

class CreateAccountDoc : AppCompatActivity() {
    private lateinit var imageViewProfile: ImageView
    private lateinit var radioGroupGender: RadioGroup

    private lateinit var doctorName: EditText
    private lateinit var doctorEmail: EditText
    private lateinit var doctorPassword: EditText
    private lateinit var doctorPasswordConfirm: EditText
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectedImage: Uri
    private lateinit var selectedGender : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_doc)
        //
        doctorName = findViewById(R.id.doctorName)
        doctorEmail = findViewById(R.id.doctorEmail)
        doctorPassword = findViewById(R.id.doctorPassword)
        doctorPasswordConfirm= findViewById(R.id.doctor_pass_confirm)
        //
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //
        radioGroupGender = findViewById(R.id.radioGroupGender)


        radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
            // checkedId will be the ID of the selected RadioButton
            when (checkedId) {
                R.id.radioButtonMale -> {
                    selectedGender = "Male"
                }
                R.id.radioButtonFemale -> {
                    selectedGender = "Female"
                }

            }
        }
        //
        imageViewProfile = findViewById(R.id.imageViewProfile)
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    // Handle the result, similar to the onActivityResult method
                    if (data != null) {
                        selectedImage = data.data!!
                        imageViewProfile.setImageURI(selectedImage)
                        // Picasso.get().load(selectedImage).into(imageViewProfile)
                    }
                }
            }
        //
        val btnBack: ImageButton = findViewById(R.id.btnCancel)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //
        val btnNext: Button = findViewById(R.id.doctor_sign_in_button1)

        btnNext.setOnClickListener {
            if(doctorName.text.isNotEmpty() &&
                doctorEmail.text.isNotEmpty() &&
                doctorPassword.text.isNotEmpty() &&
                ::selectedImage.isInitialized&& // Check if lateinit property is initialized
                selectedImage != null &&
                radioGroupGender.checkedRadioButtonId != -1
            ) {
                if(doctorPassword.text.toString() == doctorPasswordConfirm.text.toString()){
                    val intent = Intent(this, CreateAccountDoc2::class.java)

                    // Pass data to the next activity
                    intent.putExtra("doctorName", doctorName.text.toString())
                    intent.putExtra("doctorEmail", doctorEmail.text.toString())
                    intent.putExtra("doctorPassword", doctorPassword.text.toString())
                    intent.putExtra("doctorGender", selectedGender)
                    intent.putExtra("selectedImage", selectedImage)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Confirmation of password incorrect", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }


    }
    fun selectProfilePicture(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }




}