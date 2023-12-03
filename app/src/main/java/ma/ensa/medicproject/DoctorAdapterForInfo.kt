package ma.ensa.medicproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.medicproject.R

class DoctorAdapterForInfo(private val items: List<String>) :
    RecyclerView.Adapter<DoctorAdapterForInfo.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val infoTextView: TextView = itemView.findViewById(R.id.infoText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_info, parent, false)

        return InfoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val currentInfo = items[position]
        holder.infoTextView.text = currentInfo
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
