package com.example.apitest.ui.adapter
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView

import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data.model.Section

class toDoAdapter (private var toDoList: List<Section>, private val onItemClickListener: OnItemClickListener ) :
    RecyclerView.Adapter<toDoAdapter.ItemViewHolder>(){
    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_card, parent, false)
        return ItemViewHolder(view)
    }

    interface OnItemClickListener{
        fun onItemClick(section: Section)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.sectionTItle)
        val date = itemView.findViewById<TextView>(R.id.sectionDate)
        val category = itemView.findViewById<TextView>(R.id.sectionCategory)
        val card = itemView.findViewById<CardView>(R.id.card)

    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val section = toDoList[position]
        val context = holder.card.context

        holder.category.text = section.category
        holder.title.text = section.title
        holder.date.text = section.date

        if (!section.image.isNullOrEmpty()) {
            // Use section.image as background
            val imageResId = context.resources.getIdentifier(section.image, "drawable", context.packageName)
            if (imageResId != 0) {
                holder.card.setBackgroundResource(imageResId)
            } else {
                // If image name invalid, fallback to random
                setRandomBackground(holder.card, context, section)
            }
        } else {
            // If image is null, set random background
            setRandomBackground(holder.card, context, section)
        }

        holder.card.setOnClickListener {
            onItemClickListener.onItemClick(section)
        }
    }

    private fun setRandomBackground(cardView: CardView, context: Context, section: Section) {
        val randomNumber = (1..5).random()
        val resourceName = "bg$randomNumber"
        val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resourceId != 0) {
            cardView.setBackgroundResource(resourceId)
            section.image = resourceName

        } else {
            cardView.setBackgroundColor(android.graphics.Color.LTGRAY)
        }
    }




    fun updateSections(newSections: List<Section>) {
        this.toDoList = newSections
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = toDoList.size
}