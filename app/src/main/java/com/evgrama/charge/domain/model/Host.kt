package com.evgrama.charge.domain.model

data class Host(
    val hostId: String = "",
    val uid: String = "",
    val socketType: SocketType = SocketType.TYPE_5A,
    val pricePerHour: Double = 0.0,
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val avgRating: Float = 0f,
    val name: String = "",
    val phoneNumber: String = "",
    val socketImageUrl: String = "",
    // Runtime computed fields
    val isAvailable: Boolean = true,
    val distanceKm: Double = 0.0
)

enum class SocketType(val displayName: String, val chargingRateKw: Double) {
    TYPE_5A("5A Regular Charge", 1.1),
    TYPE_15A("15A Fast Charge", 3.3)
}
