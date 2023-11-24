package ma.ensa.medicproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SpecialitiesAdapter(private val specialitiesList: List<Specialities>) : RecyclerView.Adapter<SpecialitiesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_speciality_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val speciality = specialitiesList[position]

        // Set the text for specName TextView
        holder.specName.text = speciality.specName

        // Load and display the image using Picasso
        Picasso.get().load(speciality.specImage).into(holder.specImage)
    }

    override fun getItemCount(): Int {
        return specialitiesList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val specImage: ImageView = itemView.findViewById(R.id.specImage)
        val specName: TextView = itemView.findViewById(R.id.specName)
    }
}
