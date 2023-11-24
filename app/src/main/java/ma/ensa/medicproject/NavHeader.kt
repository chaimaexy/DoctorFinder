package ma.ensa.medicproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class NavHeader : AppCompatActivity() {

    private lateinit var Identif : EditText
    private lateinit var Login : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_header)

        Login = findViewById(R.id.Login)

        Login.setOnClickListener {
            val intent = Intent(this , AuthChoice::class.java)
            startActivity(intent)
        }

    }





}