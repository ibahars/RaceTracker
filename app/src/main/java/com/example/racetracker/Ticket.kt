package com.example.racetracker

data class Ticket(
    val userId: String = "",
    val raceId: String = "",
    val raceName: String = "",
    val ticketType: String = "",
    val quantity: Int = 1,
    val totalPrice: Int = 0,
    val purchaseDate: String = ""
)