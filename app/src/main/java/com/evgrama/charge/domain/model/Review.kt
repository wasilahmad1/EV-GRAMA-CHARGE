package com.evgrama.charge.domain.model

data class Review(
    val reviewId: String = "",
    val hostId: String = "",
    val travellerId: String = "",
    val rating: Int = 0,
    val reviewText: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val travellerName: String = ""
)
