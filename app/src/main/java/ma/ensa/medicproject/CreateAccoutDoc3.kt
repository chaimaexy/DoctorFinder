package ma.ensa.medicproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class CreateAccoutDoc3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_accout_doc3)

        //
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, CreateAccountDoc2::class.java)
            startActivity(intent)
        }
        //
        val btnCancel: ImageButton = findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //
        val btnNext: Button = findViewById(R.id.doctor_sign_in_button)
        btnNext.setOnClickListener {
            val intent = Intent(this, CreateAccountDoc4::class.java)
            startActivity(intent)
        }
        //
    }
}