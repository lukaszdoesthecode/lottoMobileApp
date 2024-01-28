package com.lottoapp.lottoapp3_0

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val gamesList : ArrayList<Game>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent,
            false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = gamesList[position]
        holder.chosenNumbers.text = currentItem.chosenNumbs?.joinToString(", ") ?: ""
        holder.drawnNumbers.text = currentItem.drawnNumbers?.joinToString(", ") ?: ""
        holder.score.text = currentItem.score?.toString() ?: ""
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val chosenNumbers : TextView = itemView.findViewById(R.id.tvChosenNumbers)
        val drawnNumbers : TextView = itemView.findViewById(R.id.tvDrawnNumbers)
        val score : TextView = itemView.findViewById(R.id.tvScore)


    }
}