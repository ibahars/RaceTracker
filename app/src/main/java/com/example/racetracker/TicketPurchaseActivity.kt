package com.example.racetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TicketPurchaseActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Bilet fiyatları
    private val ticketPrices = mapOf(
        "Standart" to 250,
        "VIP" to 750,
        "Pit Lane" to 1500
    )

    private var selectedType = "Standart"
    private var quantity = 1
    private var raceId = ""
    private var raceName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_purchase)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        raceId = intent.getStringExtra("raceId") ?: ""
        raceName = intent.getStringExtra("raceName") ?: ""

        findViewById<TextView>(R.id.tvPurchaseRaceName).text = raceName

        setupTicketTypes()
        setupQuantityControls()
        updateTotal()

        findViewById<TextView>(R.id.tvPurchaseBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnConfirmPurchase).setOnClickListener {
            purchaseTicket()
        }
    }

    private fun setupTicketTypes() {
        val radioGroup = findViewById<RadioGroup>(R.id.rgTicketType)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedType = when (checkedId) {
                R.id.rbStandart -> "Standart"
                R.id.rbVip -> "VIP"
                R.id.rbPitLane -> "Pit Lane"
                else -> "Standart"
            }
            updateTotal()
        }
    }

    private fun setupQuantityControls() {
        val tvQuantity = findViewById<TextView>(R.id.tvQuantity)

        findViewById<Button>(R.id.btnMinus).setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantity.text = quantity.toString()
                updateTotal()
            }
        }

        findViewById<Button>(R.id.btnPlus).setOnClickListener {
            if (quantity < 10) {
                quantity++
                tvQuantity.text = quantity.toString()
                updateTotal()
            }
        }
    }

    private fun updateTotal() {
        val price = ticketPrices[selectedType] ?: 250
        val total = price * quantity
        findViewById<TextView>(R.id.tvTotalPrice).text = "Toplam: ₺$total"
        findViewById<TextView>(R.id.tvUnitPrice).text = "Bilet fiyatı: ₺$price / kişi"
    }

    private fun purchaseTicket() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Oturum bulunamadı", Toast.LENGTH_SHORT).show()
            return
        }

        val price = ticketPrices[selectedType] ?: 250
        val total = price * quantity
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val ticket = Ticket(
            userId = userId,
            raceId = raceId,
            raceName = raceName,
            ticketType = selectedType,
            quantity = quantity,
            totalPrice = total,
            purchaseDate = date
        )


        val btn = findViewById<Button>(R.id.btnConfirmPurchase)
        btn.isEnabled = false
        btn.text = "İşleniyor..."

        db.collection("tickets")
            .add(ticket)
            .addOnSuccessListener {
                showSuccess()
            }
            .addOnFailureListener { e ->
                btn.isEnabled = true
                btn.text = "🎟️  BİLETİ SATIN AL"
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showSuccess() {
        findViewById<View>(R.id.purchaseForm).visibility = View.GONE
        findViewById<View>(R.id.successLayout).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tvSuccessRaceName).text = raceName
        findViewById<TextView>(R.id.tvSuccessDetail).text =
            "$quantity x $selectedType bilet başarıyla alındı!"

        findViewById<Button>(R.id.btnBackToMain).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
