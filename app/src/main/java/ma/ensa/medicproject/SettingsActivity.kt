package ma.ensa.medicproject

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    private var email: String? = null
    private var isLoggedIn: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
        isLoggedIn = intent.getIntExtra("logged", 0)
        email = intent.getStringExtra("email" )

        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        val langArray = resources.getStringArray(R.array.language_options)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, langArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {

                if (position > 0) {
                    val selectedLanguage: String =
                        parentView?.getItemAtPosition(position).toString()
                    setAppLanguage(selectedLanguage)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        }
    }

    private fun setAppLanguage(language: String) {
        val newLanguageCode = getLanguageCode(language)

        // Check if the language is already set
        if (getCurrentLanguageCode() != newLanguageCode) {
            val locale = Locale(newLanguageCode)
            Locale.setDefault(locale)

            val resources = resources
            val configuration = Configuration(resources.configuration)
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)

            // Start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("logged", isLoggedIn)
            intent.putExtra("email",email )
            startActivity(intent)

            // Finish the current SettingsActivity
            finish()
        }
    }
    private fun getCurrentLanguageCode(): String {
        return resources.configuration.locale.language
    }
    private fun getLanguageCode(language: String): String {
        return when (language) {
            "Select language" -> "en"
            "English" -> "en"
            "Francais" -> "fr"
            else -> "en" // Default to English
        }
    }
}