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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var CreateAccountButton: Button
    private lateinit var CreateAccountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawer: DrawerLayout = findViewById(R.id.drawerLayout)
        val Navigation: NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
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


        CreateAccountButton = findViewById(R.id.imageButton)
        CreateAccountText = findViewById(R.id.textView3)


        //login status
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val menu = navigationView.menu

        val isLoggedIn = intent.getIntExtra("logged", 0)
        val email = intent.getStringExtra("email" )


        //search by speciality
        val searchDoc: Button = findViewById(R.id.searchDoc)
        searchDoc.setOnClickListener {
            val intent1 = Intent(this , SearchDoctorBySpeciality::class.java)
            intent1.putExtra("logged", isLoggedIn)
            intent1.putExtra("email",email )
            startActivity(intent1)
        }

        if (email != null) {

            CreateAccountText.visibility = View.GONE
            CreateAccountButton.visibility = View.GONE

            //menu stuff
            headerView.findViewById<TextView>(R.id.identif).text ="$email"
            headerView.findViewById<Button>(R.id.Login).visibility =View.GONE
            // Update menu items based on login status
            menu.findItem(R.id.Profile).isVisible = true
            menu.findItem(R.id.logout).isVisible = true

        }else{
            CreateAccountText.visibility =View.VISIBLE
            CreateAccountButton.visibility =View.VISIBLE
            headerView.findViewById<TextView>(R.id.identif).text = "Doctor Finder"
            headerView.findViewById<Button>(R.id.Login).visibility =View.VISIBLE
            val loginButton: Button = headerView.findViewById(R.id.Login)
            headerView.setOnClickListener {
            }
            loginButton.setOnClickListener {


                val intent2 = Intent(this , AuthChoice::class.java)
                startActivity(intent2)
            }
            menu.findItem(R.id.Profile).isVisible = false
            menu.findItem(R.id.logout).isVisible = false


            CreateAccountButton.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, CreateAccountDoc::class.java)
                startActivity(intent)
                finish()
            })
        }






    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.Profile -> {
                checkUserTypeAndNavigate()
                return true
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

    private fun checkUserTypeAndNavigate() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { firebaseUser ->
            val userEmail = firebaseUser.email

            if (userEmail != null) {
                checkUserType(userEmail)
            } else {
                //error message or redirect to login
            }
        }
    }

    private fun checkUserType(userEmail: String) {
        val databaseReferenceDoctors = FirebaseDatabase.getInstance().getReference("Doctors")
        databaseReferenceDoctors.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the email exists in the "Doctors" node
                if (dataSnapshot.hasChild(userEmail.replace(".", ","))) {
                    // The user is a doctor
                    navigateToDoctorProfile()
                } else {
                    // The user is a regular user
                    navigateToUserProfile()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Toast.makeText(this@MainActivity, "Database Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToDoctorProfile() {
        // Navigate to the doctor's profile activity
        val intent = Intent(this, DoctorProfileActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToUserProfile() {
        // Navigate to the regular user's profile activity
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }

}