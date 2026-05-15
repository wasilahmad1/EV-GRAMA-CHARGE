package com.evgrama.charge.domain.model

data class User(
    val uid: String = "",
    val phoneNumber: String = "",
    val name: String = "",
    val role: UserRole = UserRole.NONE,
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    NONE, TRAVELLER, HOST
}
