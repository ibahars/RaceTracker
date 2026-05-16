package com.example.racetracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RaceAdapter(
    private val races: List<Race>
) : RecyclerView.Adapter<RaceAdapter.RaceViewHolder>() {

    inner class RaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvRaceName)
        val tvDate: TextView = view.findViewById(R.id.tvRaceDate)
        val tvCategory: TextView = view.findViewById(R.id.tvRaceCategory)
        val btnInspect: Button = view.findViewById(R.id.btnInspect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_race_card, parent, false)
        return RaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        val race = races[position]
        holder.tvName.text = race.name
        holder.tvDate.text = "📅 " + race.date
        holder.tvCategory.text = race.category

        val openDetail = {
            val context = holder.itemView.context
            val intent = Intent(context, RaceDetailActivity::class.java).apply {
                putExtra("raceId", race.id)
                putExtra("raceName", race.name)
            }
            context.startActivity(intent)
        }

        holder.itemView.setOnClickListener { openDetail() }
        holder.btnInspect.setOnClickListener { openDetail() }
    }

    override fun getItemCount() = races.size
}