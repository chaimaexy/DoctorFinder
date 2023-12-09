package ma.ensa.medicproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ma.ensa.medicproject.Doctor
import ma.ensa.medicproject.FirebaseImageUploader
import ma.ensa.medicproject.R
import ma.ensa.medicproject.Specialities

class FavoriteDoctorAdapter(
    private val doctorsList: MutableList<Doctor>,
    private val specialitiesList: MutableList<Specialities>,
    private val itemClickListener: (Doctor) -> Unit
) : RecyclerView.Adapter<FavoriteDoctorAdapter.DoctorViewHolder>() {

    // Simplified DoctorViewHolder
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
            holder.specialityTextView.text = associatedSpeciality?.specName ?: ""


        val firebaseImageUploader = FirebaseImageUploader()
        firebaseImageUploader.getImageDownloadUrl(currentDoctor.pmdc,
            { uri ->
                Picasso.get().load(uri).into(holder.doctorImageView)
            },
            {
                holder.doctorImageView.setImageResource(R.drawable.person)
            }
        )

        // Set click listener for the item
        holder.itemView.setOnClickListener {
            // Invoke the provided click listener with the clicked doctor
            itemClickListener.invoke(currentDoctor)
        }
    }





    override fun getItemCount(): Int {
        return doctorsList.size
    }

    fun updateList(newList: List<Doctor>) {
        doctorsList.clear()
        doctorsList.addAll(newList)
        notifyDataSetChanged()
    }
}
