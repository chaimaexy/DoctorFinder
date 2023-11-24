package ma.ensa.medicproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var imageButton: Button
    private lateinit var textView3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawer: DrawerLayout = findViewById(R.id.drawerLayout)
        val Navigation: NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // Set up navigation header






        setSupportActionBar(toolbar)

        Navigation.bringToFront()
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        Navigation.setNavigationItemSelectedListener(this)
        imageButton = findViewById(R.id.imageButton)
        textView3 = findViewById(R.id.textView3)
        //login status
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val profileImage = headerView.findViewById<ImageView>(R.id.profileImage)
        val menu = navigationView.menu

        val isLoggedIn = intent.getIntExtra("logged", 0) == 1
        //
        val isLoggedInPatient = intent.getIntExtra("logged", 0) == 2
        //
        val doctorName = intent.getStringExtra("doctorName")
        val doctorPMDC = intent.getStringExtra("doctorPMDC")

        val PatientName = intent.getStringExtra("PatientName")
        val PatientPassword = intent.getStringExtra("PatientPassword")

        //login button in Header
        val loginButton: Button = headerView.findViewById(R.id.Login)

        loginButton.setOnClickListener {
            val intent = Intent(this , AuthChoice::class.java)
            startActivity(intent)
        }




        //Update page elements

        if (isLoggedIn) {
            //stuff in the home page
            textView3.visibility = View.GONE
            imageButton.visibility = View.GONE
            //menu stuff
            headerView.findViewById<TextView>(R.id.identif).text ="Welcome, Dr. $doctorName"
            headerView.findViewById<Button>(R.id.Login).visibility =View.GONE
            // Update menu items based on login status
            menu.findItem(R.id.ProfileDoctor).isVisible = true
            menu.findItem(R.id.logout).isVisible = true

        }else if(isLoggedInPatient){

            textView3.visibility =View.GONE
            imageButton.visibility =View.GONE
            headerView.findViewById<TextView>(R.id.identif).text ="Welcome,$PatientName"
            headerView.findViewById<Button>(R.id.Login).visibility =View.GONE
            menu.findItem(R.id.ProfileDoctor).isVisible = true
            menu.findItem(R.id.logout).isVisible = true

        }else{
            textView3.visibility =View.VISIBLE
            imageButton.visibility =View.VISIBLE
            headerView.findViewById<TextView>(R.id.identif).text = "Doctor Finder"
            headerView.findViewById<Button>(R.id.Login).visibility =View.VISIBLE
            menu.findItem(R.id.ProfileDoctor).isVisible = false
            menu.findItem(R.id.logout).isVisible = false

        }




        imageButton.setOnClickListener(View.OnClickListener {
            // Start the "AuthChoice" activity when the button is clicked
            val intent = Intent(this, CreateAccountDoc::class.java)
            startActivity(intent)
        })


    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.home -> {

            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("logged", 0)
                startActivity(intent)

            }


        }
        val drawer: DrawerLayout = findViewById(R.id.drawerLayout)
        drawer.closeDrawer(GravityCompat.START)

        return false
    }



}