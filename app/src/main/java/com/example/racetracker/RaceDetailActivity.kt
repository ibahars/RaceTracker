package com.example.racetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RaceDetailActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var raceId = ""
    private var raceName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race_detail)

        db = FirebaseFirestore.getInstance()

        raceId = intent.getStringExtra("raceId") ?: ""
        raceName = intent.getStringExtra("raceName") ?: ""

        findViewById<TextView>(R.id.tvDetailTitle).text = raceName

        if (raceId.isNotEmpty()) {
            loadRaceDetail(raceId)
        } else {
            Toast.makeText(this, "Yarış bulunamadı", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tvBack).setOnClickListener {
            finish()
        }

        val btnTicket = findViewById<Button>(R.id.btnGetTicket)
        btnTicket.isEnabled = true
        btnTicket.alpha = 1f
        btnTicket.setOnClickListener {
            val ticketIntent = Intent(this, TicketPurchaseActivity::class.java)
            ticketIntent.putExtra("raceId", raceId)
            ticketIntent.putExtra("raceName", raceName)
            startActivity(ticketIntent)
        }
    }

    private fun loadRaceDetail(raceId: String) {
        db.collection("races").document(raceId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val race = document.toObject(Race::class.java)
                    race?.let { bindRaceData(it) }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Veri yüklenemedi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun bindRaceData(race: Race) {
        findViewById<TextView>(R.id.tvDetailTitle).text = race.name
        findViewById<TextView>(R.id.tvDetailCategory).text = getCategoryEmoji(race.category) + "  " + race.category
        findViewById<TextView>(R.id.tvDetailDate).text = "📅  " + race.date
        findViewById<TextView>(R.id.tvDetailLocation).text = "📍  " + (race.location.ifEmpty { "Belirtilmedi" })
        findViewById<TextView>(R.id.tvDetailDescription).text = race.description.ifEmpty { "Bu yarış için henüz açıklama eklenmemiş." }
    }

    private fun getCategoryEmoji(category: String): String {
        return when (category.lowercase()) {
            "klasik" -> "🚗"
            "ralli" -> "🌲"
            "drift" -> "💨"
            "pist" -> "🏎️"
            "karting" -> "🎯"
            "tırmanma" -> "⛰️"
            "baja" -> "🏜️"
            else -> "🏁"
        }
    }
}