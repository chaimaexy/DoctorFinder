package ma.ensa.medicproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class NavHeader : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_header)

        val button = findViewById<Button>(R.id.loginMenu)

        button.setOnClickListener(View.OnClickListener {
            // Start the "AuthChoice" activity when the button is clicked
            val intent = Intent(this, AuthChoice::class.java)
            startActivity(intent)
        })
    }
}