package ma.ensa.medicproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch

class AuthChoice : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_choice)

        val loginButton = findViewById<Button>(R.id.choiceBtn)
        val userDoctorSwitch = findViewById<Switch>(R.id.switch1)

        loginButton.setOnClickListener {
            if (userDoctorSwitch.isChecked) {
                // Start the "activity_auth_doctor" if the switch is ON
                val intent = Intent(this, AuthDoctor::class.java)
                startActivity(intent)
            } else {
                // Start the "activity_auth_user" if the switch is OFF
                val intent = Intent(this, AuthUser::class.java)
                startActivity(intent)
            }
        }
    }
}