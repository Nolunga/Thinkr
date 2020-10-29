package edu.android.thinkr.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import edu.android.thinkr.R
import edu.android.thinkr.adapters.SubjectListAdapter.*
import edu.android.thinkr.models.Subject

/**
 * @author robin
 * Created on 10/17/20
 */
class SubjectListAdapter(private val subjects: List<Subject>, val context: Context, val listener: OnSubjectClickedListener) : RecyclerView.Adapter<ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var cardView : CardView = itemView.findViewById(R.id.subjects_card)
        private var imageView : ImageView = itemView.findViewById(R.id.subjects_imageView)
        private var titleText : TextView = itemView.findViewById(R.id.subjects_title)

        fun bind(subject: Subject) {
            titleText.text = subject.chat_room_name
            imageView.setImageResource(subject.image)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cardView.setBackgroundColor(context.getColor(subject.cardBackgroundColor))
            }
            itemView.setOnClickListener {
                listener.onClick(subject)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.subject_list_item, parent, false))
    }

    override fun getItemCount(): Int = subjects.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subjects[position])
    }

    interface OnSubjectClickedListener{
        fun onClick(subject: Subject)
    }
}