import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import ma.ensa.medicproject.Doctor
import ma.ensa.medicproject.FirebaseImageUploader
import ma.ensa.medicproject.R
import ma.ensa.medicproject.Specialities

class DoctorAdapter(
    private val doctorsList: MutableList<Doctor>,
    private val specialitiesList: MutableList<Specialities>,
    private val itemClickListener: (Doctor) -> Unit

): RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorImageView: ImageView = itemView.findViewById(R.id.doctorImage)
        val nameTextView: TextView = itemView.findViewById(R.id.doctorName)
        val specialityTextView: TextView = itemView.findViewById(R.id.speciality)
        val addressTextView: TextView = itemView.findViewById(R.id.location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)

        return DoctorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val currentDoctor = doctorsList[position]

        // Display doctor information
        holder.nameTextView.text = currentDoctor.name

        // Find the associated speciality for the doctor
        val specialityId = currentDoctor.specialityId
        val associatedSpeciality = specialitiesList.find { it.specId == specialityId }

        // Display speciality information if found
        if (associatedSpeciality != null) {
            holder.specialityTextView.text = associatedSpeciality.specName
        }


        val firebaseImageUploader = FirebaseImageUploader()
        firebaseImageUploader.getImageDownloadUrl(currentDoctor.pmdc,
            { uri ->
                Picasso.get().load(uri).into(holder.doctorImageView)
            },
            {
                holder.doctorImageView.setImageResource(R.drawable.person)            }
        )
        // Set click listener for the item
        holder.itemView.setOnClickListener {
            currentDoctor.clickCounter++
            updateClickCounter(currentDoctor)
            itemClickListener.invoke(currentDoctor)
        }

    }

    override fun getItemCount(): Int {
        return doctorsList.size

    }
    fun updateList(newList: List<Doctor>) {
        doctorsList.clear()
        doctorsList.addAll(newList)
        doctorsList.sortByDescending { it.clickCounter }
        notifyDataSetChanged()
    }

    fun updateSpecialitiesList(newList: List<Specialities>) {
        specialitiesList.clear()
        specialitiesList.addAll(newList)
        notifyDataSetChanged()
    }
    private fun updateClickCounter(doctor: Doctor) {
        // Query the database to get the random key based on the doctor's email
        val databaseReferenceDoctors = FirebaseDatabase.getInstance().getReference("Doctors")
        databaseReferenceDoctors.orderByChild("email").equalTo(doctor.email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            val randomKey = snapshot.key
                            // Update the click counter in the Firebase Realtime Database
                            FirebaseDatabase.getInstance().getReference("Doctors")
                                .child(randomKey!!)
                                .child("clickCounter")
                                .setValue(doctor.clickCounter)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                    //Toast.makeText(this@DoctorAdapter, "Database Error", Toast.LENGTH_SHORT).show()
                }
            })
    }


}


