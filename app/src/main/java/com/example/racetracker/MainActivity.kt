package com.example.racetracker

import android.content.Intent
import android.graphics.Typeface
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: RaceAdapter
    private val raceList = mutableListOf<Race>()
    private val allRaces = mutableListOf<Race>()
    private var selectedCategory = "Tümü"

    private val categories = listOf(
        "Tümü" to "🏁",
        "Klasik" to "🚗",
        "Ralli" to "🌲",
        "Drift" to "💨",
        "Pist" to "🏎️",
        "Karting" to "🎯",
        "Tırmanma" to "⛰️",
        "Baja" to "🏜️"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.rvRaces)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RaceAdapter(raceList)
        recyclerView.adapter = adapter

        setupCategoryChips()
        getRacesFromFirestore()
        setupFooter()
    }

    private fun setupFooter() {
        findViewById<LinearLayout>(R.id.navTickets).setOnClickListener {
            val intent = Intent(this, MyTicketsActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            Toast.makeText(this, "Profil sayfası yakında!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupCategoryChips() {
        val chipGroup = findViewById<LinearLayout>(R.id.chipGroup)
        chipGroup.removeAllViews()

        categories.forEach { (label, emoji) ->
            val chip = TextView(this).apply {
                text = "$emoji $label"
                textSize = 13f
                setTypeface(null, Typeface.BOLD)
                setPadding(36, 18, 36, 18)
                gravity = Gravity.CENTER

                val isSelected = label == selectedCategory
                if (isSelected) {
                    setBackgroundResource(R.drawable.chip_selected_bg)
                    setTextColor(Color.WHITE)
                } else {
                    setBackgroundResource(R.drawable.chip_unselected_bg)
                    setTextColor(Color.parseColor("#333333"))
                }

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(6, 0, 6, 0) }
                layoutParams = params

                setOnClickListener {
                    selectedCategory = label
                    setupCategoryChips()
                    filterRaces()
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun filterRaces() {
        raceList.clear()
        if (selectedCategory == "Tümü") {
            raceList.addAll(allRaces)
        } else {
            raceList.addAll(allRaces.filter {
                it.category.equals(selectedCategory, ignoreCase = true)
            })
        }
        adapter.notifyDataSetChanged()
    }

    private fun getRacesFromFirestore() {
        db.collection("races")
            .get()
            .addOnSuccessListener { documents ->
                allRaces.clear()
                for (document in documents) {
                    val race = document.toObject(Race::class.java).copy(id = document.id)
                    allRaces.add(race)
                }
                filterRaces()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Veri çekilemedi: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}