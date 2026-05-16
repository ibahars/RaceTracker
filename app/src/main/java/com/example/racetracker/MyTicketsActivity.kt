package com.example.racetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyTicketsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tickets)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        findViewById<TextView>(R.id.tvTicketsBack).setOnClickListener { finish() }

        loadTickets()
    }

    private fun loadTickets() {
        val userId = auth.currentUser?.uid ?: return
        val recyclerView = findViewById<RecyclerView>(R.id.rvTickets)
        val tvEmpty = findViewById<TextView>(R.id.tvEmptyTickets)

        recyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("tickets")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val tickets = documents.map { it.toObject(Ticket::class.java) }

                if (tickets.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                    recyclerView.adapter = TicketAdapter(tickets)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Biletler yüklenemedi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

class TicketAdapter(private val tickets: List<Ticket>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRaceName: TextView = view.findViewById(R.id.tvTicketRaceName)
        val tvType: TextView = view.findViewById(R.id.tvTicketType)
        val tvQuantity: TextView = view.findViewById(R.id.tvTicketQuantity)
        val tvDate: TextView = view.findViewById(R.id.tvTicketDate)
        val tvPrice: TextView = view.findViewById(R.id.tvTicketPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.tvRaceName.text = ticket.raceName
        holder.tvType.text = getTypeEmoji(ticket.ticketType) + "  " + ticket.ticketType
        holder.tvQuantity.text = "${ticket.quantity} Bilet"
        holder.tvDate.text = "📅 " + ticket.purchaseDate
        holder.tvPrice.text = "₺${ticket.totalPrice}"
    }

    override fun getItemCount() = tickets.size

    private fun getTypeEmoji(type: String) = when (type) {
        "VIP" -> "⭐"
        "Pit Lane" -> "🏁"
        else -> "🎫"
    }
}