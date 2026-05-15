package com.evgrama.charge.domain.model

data class Booking(
    val bookingId: String = "",
    val travellerId: String = "",
    val hostId: String = "",
    val status: BookingStatus = BookingStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val travellerName: String = "",
    val travellerPhone: String = "",
    val hostName: String = "",
    val hostPhone: String = ""
)

enum class BookingStatus {
    PENDING, ACCEPTED, REJECTED, COMPLETED
}
