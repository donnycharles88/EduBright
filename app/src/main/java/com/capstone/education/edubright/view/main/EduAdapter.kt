package com.capstone.education.edubright.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.education.edubright.R

class EduAdapter(
    private val items: List<EduItem>,
    private val onItemClick: (EduItem) -> Unit
) : RecyclerView.Adapter<EduAdapter.EduViewHolder>() {

    data class EduItem(val titleRes: Int, val authorRes: Int, val descriptionRes: Int, val imageRes: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EduViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edu, parent, false)
        return EduViewHolder(view)
    }

    override fun onBindViewHolder(holder: EduViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = items.size

    class EduViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tvTitle)
        private val author: TextView = view.findViewById(R.id.tvAuthor)
        private val description: TextView = view.findViewById(R.id.tvDescription)
        private val image: ImageView = view.findViewById(R.id.iv_item_photo)

        fun bind(item: EduItem) {
            val context = itemView.context
            title.text = context.getString(item.titleRes)
            author.text = context.getString(item.authorRes)
            description.text = context.getString(item.descriptionRes)
            image.setImageResource(item.imageRes)
        }
    }
}