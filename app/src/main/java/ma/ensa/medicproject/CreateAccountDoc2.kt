package ma.ensa.medicproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast

class CreateAccountDoc2 : AppCompatActivity() {
    private val SPECIALITIES = arrayOf(
        "Cardiologie", "Dermatologie", "Opticien", "Gynachologie"
    )
    private lateinit var editText: MultiAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_doc2)
        //
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val btnCancel: ImageButton = findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //
        editText = findViewById(R.id.doctor_specialities)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, SPECIALITIES)
        editText.setAdapter(adapter)
        editText.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())


    }
    fun showInput(v: View) {
        val input = editText.text.toString().trim()
        val singleInputs = input.split("\\s*,\\s*").toTypedArray()
        var toastText = ""
        for (i in singleInputs.indices) {
            toastText += "Item $i: ${singleInputs[i]}\n"
        }
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
    }
}