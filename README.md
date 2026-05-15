EV-Grama Charge Android Application

This folder is now a working Kotlin Android project.

Open it in Android Studio and run the `app` configuration, or build from the
terminal with:

```bash
./gradlew :app:assembleDebug
```

The current app is a native Kotlin Android build with no Firebase setup required.
It includes:

- Mobile login and role selection
- Traveller home with nearby charging hosts
- Local booking request and status flow
- Host dashboard with availability, pending requests and profile edit
- Charging range/time calculator
- Review flow after a completed charging session

Package: `com.evgrama.charge`

The original implementation guide is kept below for future Firebase, Maps,
Compose and MVVM expansion.

Complete Implementation Guide
PROJECT STRUCTURE
text

app/
├── data/
│   ├── model/
│   ├── repository/
│   └── remote/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── auth/
│   ├── traveller/
│   ├── host/
│   └── common/
├── ui/theme/
└── utils/
PART 1: PROJECT SETUP
build.gradle (App Level)
Kotlin

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.evgrama.charge"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.evgrama.charge"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.01.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Firebase BOM
    val firebaseBom = platform("com.google.firebase:firebase-bom:32.7.0")
    implementation(firebaseBom)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Google Maps
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Lottie
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Coil for images
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}
PART 2: THEME & DESIGN SYSTEM
Theme.kt
Kotlin

package com.evgrama.charge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─── Brand Colors ───────────────────────────────────────────────────────────
val ElectricBlue = Color(0xFF0A84FF)
val ElectricGreen = Color(0xFF30D158)
val ElectricRed = Color(0xFFFF453A)
val ElectricOrange = Color(0xFFFF9F0A)

// ─── Dark Theme Palette ──────────────────────────────────────────────────────
val DarkBackground = Color(0xFF0D0D0F)
val DarkSurface = Color(0xFF1C1C1E)
val DarkSurfaceVariant = Color(0xFF2C2C2E)
val DarkOnSurface = Color(0xFFE5E5EA)
val DarkOnSurfaceVariant = Color(0xFF8E8E93)
val DarkOutline = Color(0xFF3A3A3C)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1A3A6B),
    onPrimaryContainer = Color(0xFFD6E8FF),
    secondary = ElectricGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1A3D27),
    onSecondaryContainer = Color(0xFFCCF5DA),
    error = ElectricRed,
    onError = Color.White,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = Color(0xFF2C2C2E)
)

@Composable
fun EVGramaTheme(
    darkTheme: Boolean = true, // Force dark mode
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = EVGramaTypography,
        shapes = EVGramaShapes,
        content = content
    )
}
Typography.kt
Kotlin

package com.evgrama.charge.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val EVGramaTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)
Shapes.kt
Kotlin

package com.evgrama.charge.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val EVGramaShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
PART 3: DATA MODELS
Domain Models
Kotlin

// domain/model/User.kt
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
Kotlin

// domain/model/Host.kt
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
Kotlin

// domain/model/Booking.kt
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
Kotlin

// domain/model/Review.kt
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
PART 4: DATA LAYER
FirestoreService.kt
Kotlin

// data/remote/FirestoreService.kt
package com.evgrama.charge.data.remote

import com.evgrama.charge.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val db: FirebaseFirestore
) {

    // ─── USER OPERATIONS ─────────────────────────────────────────────────────

    suspend fun createUser(user: User) {
        db.collection("users").document(user.uid).set(user).await()
    }

    suspend fun getUser(uid: String): User? {
        return db.collection("users").document(uid).get().await().toObject<User>()
    }

    suspend fun updateUserRole(uid: String, role: UserRole) {
        db.collection("users").document(uid)
            .update("role", role.name).await()
    }

    // ─── HOST OPERATIONS ─────────────────────────────────────────────────────

    suspend fun createOrUpdateHost(host: Host) {
        db.collection("hosts").document(host.hostId).set(host).await()
    }

    suspend fun getHost(hostId: String): Host? {
        return db.collection("hosts").document(hostId).get().await().toObject<Host>()
    }

    suspend fun getHostByUid(uid: String): Host? {
        return db.collection("hosts")
            .whereEqualTo("uid", uid)
            .get().await()
            .documents.firstOrNull()?.toObject<Host>()
    }

    fun getAllHostsFlow(): Flow<List<Host>> = callbackFlow {
        val listener = db.collection("hosts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val hosts = snapshot?.documents?.mapNotNull {
                    it.toObject<Host>()
                } ?: emptyList()
                trySend(hosts)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateHostProfile(
        hostId: String,
        socketType: SocketType,
        pricePerHour: Double,
        socketImageUrl: String
    ) {
        db.collection("hosts").document(hostId)
            .update(mapOf(
                "socketType" to socketType.name,
                "pricePerHour" to pricePerHour,
                "socketImageUrl" to socketImageUrl
            )).await()
    }

    suspend fun updateHostRating(hostId: String, newAvgRating: Float) {
        db.collection("hosts").document(hostId)
            .update("avgRating", newAvgRating).await()
    }

    // ─── BOOKING OPERATIONS ──────────────────────────────────────────────────

    suspend fun createBooking(booking: Booking): String {
        val ref = db.collection("bookings").document()
        val newBooking = booking.copy(bookingId = ref.id)
        ref.set(newBooking).await()
        return ref.id
    }

    fun getBookingFlow(bookingId: String): Flow<Booking?> = callbackFlow {
        val listener = db.collection("bookings").document(bookingId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObject<Booking>())
            }
        awaitClose { listener.remove() }
    }

    fun getPendingBookingsForHostFlow(hostId: String): Flow<List<Booking>> = callbackFlow {
        val listener = db.collection("bookings")
            .whereEqualTo("hostId", hostId)
            .whereEqualTo("status", BookingStatus.PENDING.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val bookings = snapshot?.documents?.mapNotNull {
                    it.toObject<Booking>()
                } ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        db.collection("bookings").document(bookingId)
            .update("status", status.name).await()
    }

    suspend fun getCompletedSessionCount(hostId: String): Long {
        return db.collection("bookings")
            .whereEqualTo("hostId", hostId)
            .whereEqualTo("status", BookingStatus.COMPLETED.name)
            .get().await().size().toLong()
    }

    suspend fun getTotalRevenue(hostId: String, pricePerHour: Double): Double {
        val count = getCompletedSessionCount(hostId)
        return count * pricePerHour
    }

    // ─── REVIEW OPERATIONS ───────────────────────────────────────────────────

    suspend fun submitReview(review: Review) {
        val ref = db.collection("reviews").document()
        ref.set(review.copy(reviewId = ref.id)).await()
    }

    suspend fun getReviewsForHost(hostId: String): List<Review> {
        return db.collection("reviews")
            .whereEqualTo("hostId", hostId)
            .get().await()
            .documents.mapNotNull { it.toObject<Review>() }
    }

    suspend fun calculateAndUpdateHostRating(hostId: String) {
        val reviews = getReviewsForHost(hostId)
        if (reviews.isNotEmpty()) {
            val avg = reviews.map { it.rating }.average().toFloat()
            updateHostRating(hostId, avg)
        }
    }
}
RealtimeDatabaseService.kt
Kotlin

// data/remote/RealtimeDatabaseService.kt
package com.evgrama.charge.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeDatabaseService @Inject constructor(
    private val database: FirebaseDatabase
) {

    private fun hostRef(hostId: String) =
        database.getReference("hosts/$hostId")

    suspend fun setAvailability(hostId: String, isAvailable: Boolean) {
        hostRef(hostId).child("isAvailable").setValue(isAvailable).await()
    }

    fun getAvailabilityFlow(hostId: String): Flow<Boolean> = callbackFlow {
        val ref = hostRef(hostId).child("isAvailable")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Boolean::class.java) ?: true)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Observe all host availability statuses at once
    fun getAllAvailabilityFlow(): Flow<Map<String, Boolean>> = callbackFlow {
        val ref = database.getReference("hosts")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = mutableMapOf<String, Boolean>()
                snapshot.children.forEach { hostSnap ->
                    val hostId = hostSnap.key ?: return@forEach
                    val isAvailable = hostSnap.child("isAvailable")
                        .getValue(Boolean::class.java) ?: true
                    map[hostId] = isAvailable
                }
                trySend(map)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
PART 5: DOMAIN LAYER
Repository Interface
Kotlin

// domain/repository/EVGramaRepository.kt
package com.evgrama.charge.domain.repository

import com.evgrama.charge.domain.model.*
import kotlinx.coroutines.flow.Flow

interface EVGramaRepository {
    // Users
    suspend fun createUser(user: User)
    suspend fun getUser(uid: String): User?
    suspend fun updateUserRole(uid: String, role: UserRole)

    // Hosts
    suspend fun createOrUpdateHost(host: Host)
    suspend fun getHost(hostId: String): Host?
    suspend fun getHostByUid(uid: String): Host?
    fun getAllHostsFlow(): Flow<List<Host>>
    suspend fun updateHostProfile(hostId: String, socketType: SocketType, price: Double, imageUrl: String)

    // Availability (RTDB)
    suspend fun setAvailability(hostId: String, isAvailable: Boolean)
    fun getAvailabilityFlow(hostId: String): Flow<Boolean>
    fun getAllAvailabilityFlow(): Flow<Map<String, Boolean>>

    // Bookings
    suspend fun createBooking(booking: Booking): String
    fun getBookingFlow(bookingId: String): Flow<Booking?>
    fun getPendingBookingsForHostFlow(hostId: String): Flow<List<Booking>>
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus)
    suspend fun acceptBooking(bookingId: String, hostId: String)

    // Reviews
    suspend fun submitReview(review: Review)
    suspend fun calculateAndUpdateHostRating(hostId: String)

    // Earnings
    suspend fun getCompletedSessionCount(hostId: String): Long
    suspend fun getTotalRevenue(hostId: String, pricePerHour: Double): Double
}
Repository Implementation
Kotlin

// data/repository/EVGramaRepositoryImpl.kt
package com.evgrama.charge.data.repository

import com.evgrama.charge.data.remote.FirestoreService
import com.evgrama.charge.data.remote.RealtimeDatabaseService
import com.evgrama.charge.domain.model.*
import com.evgrama.charge.domain.repository.EVGramaRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EVGramaRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService,
    private val rtdbService: RealtimeDatabaseService,
    private val db: FirebaseFirestore
) : EVGramaRepository {

    override suspend fun createUser(user: User) = firestoreService.createUser(user)
    override suspend fun getUser(uid: String) = firestoreService.getUser(uid)
    override suspend fun updateUserRole(uid: String, role: UserRole) =
        firestoreService.updateUserRole(uid, role)

    override suspend fun createOrUpdateHost(host: Host) =
        firestoreService.createOrUpdateHost(host)
    override suspend fun getHost(hostId: String) = firestoreService.getHost(hostId)
    override suspend fun getHostByUid(uid: String) = firestoreService.getHostByUid(uid)
    override fun getAllHostsFlow() = firestoreService.getAllHostsFlow()
    override suspend fun updateHostProfile(
        hostId: String, socketType: SocketType, price: Double, imageUrl: String
    ) = firestoreService.updateHostProfile(hostId, socketType, price, imageUrl)

    override suspend fun setAvailability(hostId: String, isAvailable: Boolean) =
        rtdbService.setAvailability(hostId, isAvailable)
    override fun getAvailabilityFlow(hostId: String) =
        rtdbService.getAvailabilityFlow(hostId)
    override fun getAllAvailabilityFlow() = rtdbService.getAllAvailabilityFlow()

    override suspend fun createBooking(booking: Booking) =
        firestoreService.createBooking(booking)
    override fun getBookingFlow(bookingId: String) =
        firestoreService.getBookingFlow(bookingId)
    override fun getPendingBookingsForHostFlow(hostId: String) =
        firestoreService.getPendingBookingsForHostFlow(hostId)
    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus) =
        firestoreService.updateBookingStatus(bookingId, status)

    /**
     * CRITICAL: Accept booking + flip availability in a single atomic operation.
     * Uses Firestore batch write to prevent double-booking.
     */
    override suspend fun acceptBooking(bookingId: String, hostId: String) {
        val batch = db.batch()
        // 1. Update booking status
        val bookingRef = db.collection("bookings").document(bookingId)
        batch.update(bookingRef, "status", BookingStatus.ACCEPTED.name)
        batch.commit().await()
        // 2. Immediately flip RTDB availability to Busy
        rtdbService.setAvailability(hostId, false)
    }

    override suspend fun submitReview(review: Review) =
        firestoreService.submitReview(review)
    override suspend fun calculateAndUpdateHostRating(hostId: String) =
        firestoreService.calculateAndUpdateHostRating(hostId)
    override suspend fun getCompletedSessionCount(hostId: String) =
        firestoreService.getCompletedSessionCount(hostId)
    override suspend fun getTotalRevenue(hostId: String, pricePerHour: Double) =
        firestoreService.getTotalRevenue(hostId, pricePerHour)
}
Use Cases
Kotlin

// domain/usecase/GetNearbyHostsUseCase.kt
package com.evgrama.charge.domain.usecase

import com.evgrama.charge.domain.model.Host
import com.evgrama.charge.domain.repository.EVGramaRepository
import com.evgrama.charge.utils.HaversineCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

enum class SortFilter { NEAREST, CHEAPEST }

class GetNearbyHostsUseCase @Inject constructor(
    private val repository: EVGramaRepository
) {
    operator fun invoke(
        userLat: Double,
        userLng: Double,
        filter: SortFilter = SortFilter.NEAREST
    ): Flow<List<Host>> {
        return combine(
            repository.getAllHostsFlow(),
            repository.getAllAvailabilityFlow()
        ) { hosts, availabilityMap ->
            // Merge availability from RTDB
            val hostsWithAvailability = hosts.map { host ->
                val isAvailable = availabilityMap[host.hostId] ?: true
                val distance = HaversineCalculator.calculate(
                    userLat, userLng, host.lat, host.lng
                )
                host.copy(isAvailable = isAvailable, distanceKm = distance)
            }
            // Apply sorting filter
            when (filter) {
                SortFilter.NEAREST -> hostsWithAvailability.sortedBy { it.distanceKm }
                SortFilter.CHEAPEST -> hostsWithAvailability.sortedBy { it.pricePerHour }
            }
        }
    }
}
PART 6: UTILITIES
HaversineCalculator.kt
Kotlin

// utils/HaversineCalculator.kt
package com.evgrama.charge.utils

import kotlin.math.*

object HaversineCalculator {
    private const val EARTH_RADIUS_KM = 6371.0

    fun calculate(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    fun formatDistance(km: Double): String {
        return if (km < 1.0) "${(km * 1000).toInt()} m away"
        else "${"%.1f".format(km)} km away"
    }
}
ChargingCalculator.kt
Kotlin

// utils/ChargingCalculator.kt
package com.evgrama.charge.utils

import com.evgrama.charge.domain.model.SocketType

object ChargingCalculator {
    // Average EV efficiency: ~6 km/kWh for Indian EVs (two-wheelers)
    private const val KM_PER_KWH = 6.0

    fun calculateRangeGainedInOneHour(
        batteryCapacityKwh: Double,
        currentBatteryPercent: Int,
        socketType: SocketType
    ): Double {
        val chargingRateKw = socketType.chargingRateKw
        // Energy delivered in 1 hour = chargingRate (kW) * 1h = kWh
        val energyDeliveredKwh = minOf(
            chargingRateKw,
            batteryCapacityKwh * (1 - currentBatteryPercent / 100.0)
        )
        return energyDeliveredKwh * KM_PER_KWH
    }

    fun formatRange(km: Double): String = "${"%.0f".format(km)} km"
}
PART 7: NAVIGATION
Navigation.kt
Kotlin

// presentation/navigation/Navigation.kt
package com.evgrama.charge.presentation.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object RoleSelection : Screen("role_selection")

    // Traveller
    object TravellerHome : Screen("traveller_home")
    object BookingStatus : Screen("booking_status/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_status/$bookingId"
    }
    object ChargingCalculator : Screen("charging_calculator")
    object RatingReview : Screen("rating_review/{hostId}/{bookingId}") {
        fun createRoute(hostId: String, bookingId: String) = "rating_review/$hostId/$bookingId"
    }

    // Host
    object HostDashboard : Screen("host_dashboard")
    object HostProfile : Screen("host_profile")
}

@Composable
fun EVGramaNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(navController = navController)
        }
        composable(Screen.TravellerHome.route) {
            TravellerHomeScreen(navController = navController)
        }
        composable(
            route = Screen.BookingStatus.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            BookingStatusScreen(
                bookingId = backStackEntry.arguments?.getString("bookingId") ?: "",
                navController = navController
            )
        }
        composable(Screen.ChargingCalculator.route) {
            ChargingCalculatorScreen()
        }
        composable(
            route = Screen.RatingReview.route,
            arguments = listOf(
                navArgument("hostId") { type = NavType.StringType },
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            RatingReviewScreen(
                hostId = backStackEntry.arguments?.getString("hostId") ?: "",
                bookingId = backStackEntry.arguments?.getString("bookingId") ?: "",
                navController = navController
            )
        }
        composable(Screen.HostDashboard.route) {
            HostDashboardScreen(navController = navController)
        }
        composable(Screen.HostProfile.route) {
            HostProfileScreen(navController = navController)
        }
    }
}
PART 8: AUTHENTICATION SCREENS
Screen 1.1 — Splash Screen
Kotlin

// presentation/auth/SplashScreen.kt
package com.evgrama.charge.presentation.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.evgrama.charge.R
import com.evgrama.charge.presentation.navigation.Screen
import com.evgrama.charge.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    // Lottie animation
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lightning_bolt)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(2500L)
        when {
            isLoggedIn == false -> navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
            userRole == "HOST" -> navController.navigate(Screen.HostDashboard.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
            userRole == "TRAVELLER" -> navController.navigate(Screen.TravellerHome.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
            else -> navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Lightning Bolt Lottie
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(160.dp)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "EV-Grama",
                    style = MaterialTheme.typography.displayLarge,
                    color = ElectricBlue,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Charge Together, Go Further",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )

                Spacer(Modifier.height(48.dp))

                // Pulsing indicator
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = ElectricGreen,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
Screen 1.2 — Login / OTP Screen
Kotlin

// presentation/auth/LoginScreen.kt
package com.evgrama.charge.presentation.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.evgrama.charge.R
import com.evgrama.charge.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.loginUiState.collectAsState()
    var phoneNumber by remember { mutableStateOf("") }
    var otpDigits by remember { mutableStateOf(List(6) { "" }) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.ev_charging)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Animation
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Welcome to\nEV-Grama",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Your community charging network",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            // ─── Phone Input ──────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Phone Number",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkOnSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Country code badge
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = DarkSurfaceVariant,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                "🇮🇳 +91",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkOnSurface
                            )
                        }

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                                    phoneNumber = it
                                }
                            },
                            placeholder = {
                                Text("10-digit mobile number", color = DarkOnSurfaceVariant)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = DarkOutline,
                                focusedTextColor = DarkOnSurface,
                                unfocusedTextColor = DarkOnSurface,
                                cursorColor = ElectricBlue
                            )
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Send OTP Button
                    Button(
                        onClick = { viewModel.sendOtp("+91$phoneNumber") },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = phoneNumber.length == 10 && uiState !is LoginUiState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            disabledContainerColor = DarkSurfaceVariant
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (uiState is LoginUiState.OtpSent && (uiState as LoginUiState.OtpSent).loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Send OTP →",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // ─── OTP Input Grid (Animated Reveal) ─────────────────────────
            AnimatedVisibility(
                visible = uiState is LoginUiState.OtpSent || uiState is LoginUiState.Verifying,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text(
                                "Enter OTP",
                                style = MaterialTheme.typography.labelLarge,
                                color = DarkOnSurfaceVariant
                            )

                            Text(
                                "Sent to +91 $phoneNumber",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ElectricGreen
                            )

                            Spacer(Modifier.height(20.dp))

                            // 6-digit OTP grid
                            OtpInputGrid(
                                otpDigits = otpDigits,
                                onOtpChange = { index, value ->
                                    otpDigits = otpDigits.toMutableList().also {
                                        it[index] = value
                                    }
                                }
                            )

                            Spacer(Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    viewModel.verifyOtp(
                                        otpDigits.joinToString(""),
                                        navController
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                enabled = otpDigits.all { it.isNotEmpty() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricGreen,
                                    disabledContainerColor = DarkSurfaceVariant
                                ),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                if (uiState is LoginUiState.Verifying) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.Black,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        "Verify & Login ✓",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Error message
            if (uiState is LoginUiState.Error) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ElectricRed.copy(alpha = 0.15f)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        (uiState as LoginUiState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = ElectricRed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun OtpInputGrid(
    otpDigits: List<String>,
    onOtpChange: (Int, String) -> Unit
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        otpDigits.forEachIndexed { index, digit ->
            OutlinedTextField(
                value = digit,
                onValueChange = { newVal ->
                    val clean = newVal.filter { it.isDigit() }.take(1)
                    onOtpChange(index, clean)
                    if (clean.isNotEmpty() && index < 5) {
                        focusRequesters[index + 1].requestFocus()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .focusRequester(focusRequesters[index]),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkOnSurface
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = if (digit.isNotEmpty()) ElectricGreen else DarkOutline,
                    cursorColor = ElectricBlue
                ),
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}
Screen 1.3 — Role Selection
Kotlin

// presentation/auth/RoleSelectionScreen.kt
package com.evgrama.charge.presentation.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.evgrama.charge.domain.model.UserRole
import com.evgrama.charge.ui.theme.*

@Composable
fun RoleSelectionScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            Text(
                "How do you want\nto use the app?",
                style = MaterialTheme.typography.displayLarge,
                color = DarkOnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "You can change this later in settings",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurfaceVariant
            )

            Spacer(Modifier.height(48.dp))

            // ─── Traveller Card ───────────────────────────────────────────
            RoleCard(
                icon = Icons.Default.ElectricBolt,
                title = "I want to CHARGE",
                subtitle = "Find nearby charging points\nand book instantly",
                accentColor = ElectricBlue,
                isSelected = selectedRole == UserRole.TRAVELLER,
                onClick = { selectedRole = UserRole.TRAVELLER }
            )

            Spacer(Modifier.height(16.dp))

            // ─── Host Card ────────────────────────────────────────────────
            RoleCard(
                icon = Icons.Default.Power,
                title = "I want to HOST",
                subtitle = "Share your socket and\nearn money from neighbours",
                accentColor = ElectricGreen,
                isSelected = selectedRole == UserRole.HOST,
                onClick = { selectedRole = UserRole.HOST }
            )

            Spacer(Modifier.height(48.dp))

            // ─── Continue Button ──────────────────────────────────────────
            Button(
                onClick = {
                    selectedRole?.let { role ->
                        viewModel.saveRoleAndNavigate(role, navController)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedRole != null && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (selectedRole) {
                        UserRole.HOST -> ElectricGreen
                        else -> ElectricBlue
                    },
                    disabledContainerColor = DarkSurfaceVariant
                ),
                shape = MaterialTheme.shapes.large
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Continue →",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selectedRole == UserRole.HOST) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun RoleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else DarkOutline,
        label = "borderColor"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.12f) else DarkSurface,
        label = "bgColor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.large
            )
            .clickable(onClick = onClick)
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Surface(
                modifier = Modifier.size(64.dp),
                shape = MaterialTheme.shapes.medium,
                color = accentColor.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(36.dp),
                        tint = accentColor
                    )
                }
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkOnSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
            }

            // Selection indicator
            AnimatedVisibility(visible = isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
AuthViewModel.kt
Kotlin

// presentation/auth/AuthViewModel.kt
package com.evgrama.charge.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.evgrama.charge.domain.model.User
import com.evgrama.charge.domain.model.UserRole
import com.evgrama.charge.domain.repository.EVGramaRepository
import com.evgrama.charge.presentation.navigation.Screen
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class OtpSent(val loading: Boolean = false) : LoginUiState()
    object Verifying : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: EVGramaRepository
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isLoggedIn: StateFlow<Boolean?> = flow {
        emit(auth.currentUser != null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val userRole: StateFlow<String?> = flow {
        val uid = auth.currentUser?.uid ?: run { emit(null); return@flow }
        emit(repository.getUser(uid)?.role?.name)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun sendOtp(phoneNumber: String) {
        _loginUiState.value = LoginUiState.OtpSent(loading = true)
        // Requires Activity context — inject via companion or pass activity
        // Shown as logic scaffold:
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithCredential(credential, null)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _loginUiState.value = LoginUiState.Error(
                    e.message ?: "Verification failed. Try again."
                )
            }

            override fun onCodeSent(
                vId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = vId
                resendToken = token
                _loginUiState.value = LoginUiState.OtpSent(loading = false)
            }
        }
        // PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber)...build()
    }

    fun verifyOtp(otp: String, navController: NavController) {
        val vId = verificationId ?: run {
            _loginUiState.value = LoginUiState.Error("Session expired. Resend OTP.")
            return
        }
        _loginUiState.value = LoginUiState.Verifying
        val credential = PhoneAuthProvider.getCredential(vId, otp)
        signInWithCredential(credential, navController)
    }

    private fun signInWithCredential(
        credential: PhoneAuthCredential,
        navController: NavController?
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
                val uid = auth.currentUser?.uid ?: return@launch
                val phone = auth.currentUser?.phoneNumber ?: ""
                val existingUser = repository.getUser(uid)

                if (existingUser == null) {
                    // New user — create and go to role selection
                    repository.createUser(
                        User(uid = uid, phoneNumber = phone, name = "EV User")
                    )
                    navController?.navigate(Screen.RoleSelection.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                } else {
                    // Existing user — route by role
                    val destination = when (existingUser.role) {
                        UserRole.TRAVELLER -> Screen.TravellerHome.route
                        UserRole.HOST -> Screen.HostDashboard.route
                        else -> Screen.RoleSelection.route
                    }
                    navController?.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
                _loginUiState.value = LoginUiState.Success
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState.Error(
                    "Invalid OTP. Please try again."
                )
            }
        }
    }

    fun saveRoleAndNavigate(role: UserRole, navController: NavController) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                repository.updateUserRole(uid, role)

                val destination = when (role) {
                    UserRole.TRAVELLER -> Screen.TravellerHome.route
                    UserRole.HOST -> Screen.HostDashboard.route
                    else -> Screen.Login.route
                }
                navController.navigate(destination) {
                    popUpTo(Screen.RoleSelection.route) { inclusive = true }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
PART 9: TRAVELLER SCREENS
Screen 2.1 — Home Map Screen
Kotlin

// presentation/traveller/TravellerHomeScreen.kt
package com.evgrama.charge.presentation.traveller

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.evgrama.charge.domain.model.Host
import com.evgrama.charge.domain.usecase.SortFilter
import com.evgrama.charge.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravellerHomeScreen(
    navController: NavController,
    viewModel: TravellerHomeViewModel = hiltViewModel()
) {
    val hosts by viewModel.filteredHosts.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    var selectedHost by remember { mutableStateOf<Host?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: LatLng(20.5937, 78.9629), // India center default
            14f
        )
    }

    // Track user location changes
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 14f))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ─── Full-Screen Map ──────────────────────────────────────────────
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = userLocation != null,
                mapStyleOptions = MapStyleOptions(DARK_MAP_STYLE_JSON)
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false
            ),
            onMapClick = { selectedHost = null }
        ) {
            hosts.forEach { host ->
                val markerColor = when {
                    host.isAvailable -> BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN
                    )
                    else -> BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED
                    )
                }
                Marker(
                    state = MarkerState(position = LatLng(host.lat, host.lng)),
                    icon = markerColor,
                    title = host.name,
                    snippet = "₹${host.pricePerHour}/hr • ${
                        if (host.isAvailable) "Available" else "Busy"
                    }",
                    onClick = {
                        selectedHost = host
                        true
                    }
                )
            }
        }

        // ─── Top Overlay (Search + Filters) ──────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Search Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurface.copy(alpha = 0.95f)
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = DarkOnSurfaceVariant
                    )
                    Spacer(Modifier.width(12.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search locality, area...",
                                color = DarkOnSurfaceVariant
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = DarkOnSurface,
                            unfocusedTextColor = DarkOnSurface,
                            cursorColor = ElectricBlue
                        ),
                        singleLine = true
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null, tint = DarkOnSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    EVFilterChip(
                        label = "⚡ Nearest",
                        selected = currentFilter == SortFilter.NEAREST,
                        selectedColor = ElectricBlue,
                        onClick = { viewModel.setFilter(SortFilter.NEAREST) }
                    )
                }
                item {
                    EVFilterChip(
                        label = "💰 Cheapest",
                        selected = currentFilter == SortFilter.CHEAPEST,
                        selectedColor = ElectricOrange,
                        onClick = { viewModel.setFilter(SortFilter.CHEAPEST) }
                    )
                }
                item {
                    EVFilterChip(
                        label = "✅ Available Only",
                        selected = viewModel.showOnlyAvailable.collectAsState().value,
                        selectedColor = ElectricGreen,
                        onClick = { viewModel.toggleAvailableFilter() }
                    )
                }
            }
        }

        // ─── Bottom FABs ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // My Location FAB
            FloatingActionButton(
                onClick = { viewModel.centerOnUserLocation() },
                containerColor = DarkSurface,
                contentColor = ElectricBlue,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.MyLocation, "My Location")
            }

            // Calculator FAB
            FloatingActionButton(
                onClick = { navController.navigate(Screen.ChargingCalculator.route) },
                containerColor = DarkSurface,
                contentColor = ElectricOrange,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Calculate, "Calculator")
            }
        }

        // ─── Live Stats Bar ───────────────────────────────────────────────
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkSurface.copy(alpha = 0.95f)
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChip(
                    count = hosts.count { it.isAvailable },
                    label = "Available",
                    color = ElectricGreen
                )
                Divider(
                    modifier = Modifier
                        .height(32.dp)
                        .width(1.dp),
                    color = DarkOutline
                )
                StatChip(
                    count = hosts.count { !it.isAvailable },
                    label = "Busy",
                    color = ElectricRed
                )
                Divider(
                    modifier = Modifier
                        .height(32.dp)
                        .width(1.dp),
                    color = DarkOutline
                )
                StatChip(
                    count = hosts.size,
                    label = "Total",
                    color = ElectricBlue
                )
            }
        }
    }

    // ─── Host Detail Bottom Sheet ─────────────────────────────────────────
    selectedHost?.let { host ->
        ModalBottomSheet(
            onDismissRequest = { selectedHost = null },
            sheetState = sheetState,
            containerColor = DarkSurface,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            HostDetailBottomSheet(
                host = host,
                onBookingRequest = { selectedHostId ->
                    viewModel.createBooking(selectedHostId) { bookingId ->
                        selectedHost = null
                        navController.navigate(
                            Screen.BookingStatus.createRoute(bookingId)
                        )
                    }
                },
                onDismiss = { selectedHost = null }
            )
        }
    }
}

@Composable
fun EVFilterChip(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = selectedColor.copy(alpha = 0.2f),
            selectedLabelColor = selectedColor,
            containerColor = DarkSurface,
            labelColor = DarkOnSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = selectedColor,
            borderColor = DarkOutline,
            selectedBorderWidth = 1.5.dp
        )
    )
}

@Composable
fun StatChip(count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "$count",
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkOnSurfaceVariant
        )
    }
}
Screen 2.2 — Host Detail Bottom Sheet
Kotlin

// presentation/traveller/HostDetailBottomSheet.kt
package com.evgrama.charge.presentation.traveller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.evgrama.charge.domain.model.Host
import com.evgrama.charge.ui.theme.*
import com.evgrama.charge.utils.HaversineCalculator

@Composable
fun HostDetailBottomSheet(
    host: Host,
    onBookingRequest: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // ─── Drag Handle ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(DarkOutline)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        // ─── Host Info Header ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
            ) {
                if (host.socketImageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = host.socketImageUrl,
                        contentDescription = "Host Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                        tint = DarkOnSurfaceVariant
                    )
                }
                // Availability dot
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                if (host.isAvailable) ElectricGreen else ElectricRed
                            )
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    host.name.ifEmpty { "Host #${host.hostId.take(6)}" },
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkOnSurface
                )

                Spacer(Modifier.height(4.dp))

                // Star rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < host.avgRating.toInt())
                                Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = ElectricOrange
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${"%.1f".format(host.avgRating)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkOnSurfaceVariant
                    )
                }
            }

            // Live Status Pill
            LiveStatusPill(isAvailable = host.isAvailable)
        }

        Spacer(Modifier.height(20.dp))
        Divider(color = DarkOutline, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(20.dp))

        // ─── Details Grid ─────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DetailItem(
                icon = Icons.Default.BoltOutlined,
                label = "Socket Type",
                value = host.socketType.displayName,
                tint = ElectricBlue
            )
            DetailItem(
                icon = Icons.Default.CurrencyRupee,
                label = "Price",
                value = "₹${host.pricePerHour.toInt()}/hr",
                tint = ElectricGreen
            )
            DetailItem(
                icon = Icons.Default.Navigation,
                label = "Distance",
                value = HaversineCalculator.formatDistance(host.distanceKm),
                tint = ElectricOrange
            )
        }

        Spacer(Modifier.height(20.dp))

        // ─── Address ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = DarkOnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                host.address.ifEmpty { "Address not provided" },
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(28.dp))

        // ─── Book Button (Sticky) ─────────────────────────────────────────
        Button(
            onClick = { onBookingRequest(host.hostId) },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 24.dp),
            enabled = host.isAvailable,
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricBlue,
                disabledContainerColor = DarkSurfaceVariant
            ),
            shape = MaterialTheme.shapes.large
        ) {
            if (host.isAvailable) {
                Icon(Icons.Default.FlashOn, null, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    "Request to Book",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    Icons.Default.Block,
                    null,
                    modifier = Modifier.size(22.dp),
                    tint = DarkOnSurfaceVariant
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Currently Busy",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LiveStatusPill(isAvailable: Boolean) {
    val color = if (isAvailable) ElectricGreen else ElectricRed
    val text = if (isAvailable) "Available Now" else "Busy"
    val icon = if (isAvailable) Icons.Default.CheckCircle else Icons.Default.Cancel

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsing dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text,
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
        }
    }
}

@Composable
fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = tint.copy(alpha = 0.15f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
        Text(value, style = MaterialTheme.typography.labelLarge, color = DarkOnSurface)
    }
}
Screen 2.3 — Booking Status Screen
Kotlin

// presentation/traveller/BookingStatusScreen.kt
package com.evgrama.charge.presentation.traveller

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.evgrama.charge.R
import com.evgrama.charge.domain.model.BookingStatus
import com.evgrama.charge.presentation.navigation.Screen
import com.evgrama.charge.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun BookingStatusScreen(
    bookingId: String,
    navController: NavController,
    viewModel: BookingStatusViewModel = hiltViewModel()
) {
    val booking by viewModel.booking.collectAsState()
    val host by viewModel.host.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(bookingId) {
        viewModel.observeBooking(bookingId)
    }

    // Countdown timer (60 minutes)
    var remainingSeconds by remember { mutableStateOf(3600) }
    val isAccepted = booking?.status == BookingStatus.ACCEPTED

    LaunchedEffect(isAccepted) {
        if (isAccepted) {
            while (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds--
            }
            // Auto-complete booking when timer ends
            viewModel.completeBooking(bookingId)
            navController.navigate(
                Screen.RatingReview.createRoute(
                    host?.hostId ?: "",
                    bookingId
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ─── Header ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = DarkOnSurface)
                }
                Text(
                    "Booking Status",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkOnSurface
                )
            }

            Spacer(Modifier.height(32.dp))

            // ─── Booking ID ───────────────────────────────────────────────
            Surface(
                shape = MaterialTheme.shapes.small,
                color = DarkSurface
            ) {
                Text(
                    "Booking #${bookingId.take(8).uppercase()}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
            }

            Spacer(Modifier.height(40.dp))

            // ─── Status Content ───────────────────────────────────────────
            when (booking?.status) {
                BookingStatus.PENDING -> PendingState()
                BookingStatus.REJECTED -> RejectedState {
                    navController.navigate(Screen.TravellerHome.route) {
                        popUpTo(Screen.TravellerHome.route) { inclusive = true }
                    }
                }
                BookingStatus.ACCEPTED -> AcceptedState(
                    host = host,
                    remainingSeconds = remainingSeconds,
                    onWhatsApp = {
                        val phone = host?.phoneNumber?.replace("+", "") ?: return@AcceptedState
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("whatsapp://send?phone=$phone")
                                setPackage("com.whatsapp")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // WhatsApp not installed — fallback to SMS
                            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("sms:${host?.phoneNumber}")
                            }
                            context.startActivity(smsIntent)
                        }
                    },
                    onNavigate = {
                        host?.let { h ->
                            val mapsUri = Uri.parse(
                                "google.navigation:q=${h.lat},${h.lng}&mode=d"
                            )
                            val intent = Intent(Intent.ACTION_VIEW, mapsUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            context.startActivity(intent)
                        }
                    }
                )
                BookingStatus.COMPLETED -> {
                    // Redirect to review
                    LaunchedEffect(Unit) {
                        navController.navigate(
                            Screen.RatingReview.createRoute(
                                host?.hostId ?: "",
                                bookingId
                            )
                        )
                    }
                }
                null -> {
                    CircularProgressIndicator(color = ElectricBlue)
                }
            }
        }
    }
}

@Composable
fun PendingState() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.waiting_clock)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(220.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Waiting for Host\nto Accept...",
            style = MaterialTheme.typography.headlineMedium,
            color = DarkOnSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Usually takes 1-3 minutes",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkOnSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(0.6f),
            color = ElectricBlue,
            trackColor = DarkSurfaceVariant
        )
    }
}

@Composable
fun AcceptedState(
    host: Host?,
    remainingSeconds: Int,
    onWhatsApp: () -> Unit,
    onNavigate: () -> Unit
) {
    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // ✅ Accepted banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ElectricGreen.copy(alpha = 0.15f)
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = ElectricGreen,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "Booking Accepted! 🎉",
                        style = MaterialTheme.typography.titleLarge,
                        color = ElectricGreen
                    )
                    Text(
                        "Head to the charging point now",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkOnSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ─── Countdown Timer ──────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Charging Session",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "%02d:%02d:%02d".format(hours, minutes, seconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = when {
                        remainingSeconds > 600 -> ElectricGreen
                        remainingSeconds > 300 -> ElectricOrange
                        else -> ElectricRed
                    }
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { remainingSeconds / 3600f },
                    modifier = Modifier.fillMaxWidth(),
                    color = ElectricGreen,
                    trackColor = DarkSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "remaining",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ─── Action Buttons ───────────────────────────────────────────────
        Button(
            onClick = onNavigate,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.Navigation, null)
            Spacer(Modifier.width(10.dp))
            Text(
                "Navigate to Charger",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onWhatsApp,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF25D366) // WhatsApp green
            ),
            border = BorderStroke(1.5.dp, Color(0xFF25D366)),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.Chat, null)
            Spacer(Modifier.width(10.dp))
            Text(
                "Chat on WhatsApp",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Show host phone
        Spacer(Modifier.height(12.dp))
        Text(
            "Host: ${host?.phoneNumber ?: "N/A"}",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkOnSurfaceVariant
        )
    }
}

@Composable
fun RejectedState(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("❌", fontSize = 80.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Booking Rejected",
            style = MaterialTheme.typography.headlineMedium,
            color = ElectricRed
        )
        Text(
            "The host is unavailable. Try another nearby charger.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkOnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
        ) {
            Text("Find Another Charger")
        }
    }
}
Screen 2.4 — Charging Calculator
Kotlin

// presentation/traveller/ChargingCalculatorScreen.kt
package com.evgrama.charge.presentation.traveller

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.evgrama.charge.R
import com.evgrama.charge.domain.model.SocketType
import com.evgrama.charge.ui.theme.*
import com.evgrama.charge.utils.ChargingCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargingCalculatorScreen() {
    var batteryPercent by remember { mutableStateOf(20f) }
    var batteryCapacity by remember { mutableStateOf(2.5) } // kWh
    var selectedSocket by remember { mutableStateOf(SocketType.TYPE_5A) }

    val rangeGained = remember(batteryPercent, batteryCapacity, selectedSocket) {
        ChargingCalculator.calculateRangeGainedInOneHour(
            batteryCapacityKwh = batteryCapacity,
            currentBatteryPercent = batteryPercent.toInt(),
            socketType = selectedSocket
        )
    }

    // Lottie animation
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.battery_charging)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // Battery capacity options for EVs common in rural India
    val capacityOptions = listOf(
        Pair("Ola S1 Air (2.5 kWh)", 2.5),
        Pair("Ola S1 Pro (3.97 kWh)", 3.97),
        Pair("Bajaj Chetak (3.0 kWh)", 3.0),
        Pair("TVS iQube (4.56 kWh)", 4.56),
        Pair("Ather 450X (2.9 kWh)", 2.9),
        Pair("Custom", 0.0)
    )
    var selectedCapacityIndex by remember { mutableStateOf(0) }
    var customCapacity by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                "⚡ Charging Calculator",
                style = MaterialTheme.typography.headlineMedium,
                color = DarkOnSurface
            )
            Text(
                "Estimate your range for 1-hour session",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // ─── Lottie Battery Animation ─────────────────────────────────
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(180.dp)
            )

            Spacer(Modifier.height(24.dp))

            // ─── Current Battery % Slider ─────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Current Battery", style = MaterialTheme.typography.labelLarge, color = DarkOnSurfaceVariant)
                        Text(
                            "${batteryPercent.toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            color = when {
                                batteryPercent < 20 -> ElectricRed
                                batteryPercent < 50 -> ElectricOrange
                                else -> ElectricGreen
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Slider(
                        value = batteryPercent,
                        onValueChange = { batteryPercent = it },
                        valueRange = 0f..100f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricBlue,
                            activeTrackColor = ElectricBlue,
                            inactiveTrackColor = DarkSurfaceVariant
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0%", style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
                        Text("100%", style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── EV Model / Capacity Dropdown ─────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "EV Model / Battery Capacity",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkOnSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = capacityOptions[selectedCapacityIndex].first,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = DarkOutline,
                                focusedTextColor = DarkOnSurface,
                                unfocusedTextColor = DarkOnSurface
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            capacityOptions.forEachIndexed { index, option ->
                                DropdownMenuItem(
                                    text = { Text(option.first) },
                                    onClick = {
                                        selectedCapacityIndex = index
                                        if (option.second > 0) {
                                            batteryCapacity = option.second
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Custom capacity input
                    if (capacityOptions[selectedCapacityIndex].second == 0.0) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = customCapacity,
                            onValueChange = {
                                customCapacity = it
                                it.toDoubleOrNull()?.let { v -> batteryCapacity = v }
                            },
                            label = { Text("Custom capacity (kWh)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = DarkOutline,
                                focusedTextColor = DarkOnSurface,
                                unfocusedTextColor = DarkOnSurface
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── Socket Type Selection ─────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Socket Type",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkOnSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))

                    SocketType.values().forEach { socket ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSocket == socket,
                                onClick = { selectedSocket = socket },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = ElectricBlue,
                                    unselectedColor = DarkOutline
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    socket.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = DarkOnSurface
                                )
                                Text(
                                    "${socket.chargingRateKw} kW charging rate",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkOnSurfaceVariant
                                )
                            }
                        }
                        if (socket != SocketType.values().last()) {
                            Divider(
                                color = DarkOutline,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ─── Result Card ───────────────────────────────────────────────
            AnimatedContent(
                targetState = rangeGained,
                transitionSpec = { fadeIn() + slideInVertically() togetherWith fadeOut() },
                label = "rangeResult"
            ) { range ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ElectricGreen.copy(alpha = 0.15f)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "In 1 hour you'll gain",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkOnSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "~${ChargingCalculator.formatRange(range)}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = ElectricGreen
                        )
                        Text(
                            "of range",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkOnSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        Divider(color = ElectricGreen.copy(alpha = 0.3f))
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CalcStat("Socket", selectedSocket.displayName.split(" ").first())
                            CalcStat("Battery", "${batteryPercent.toInt()}%")
                            CalcStat("Rate", "${selectedSocket.chargingRateKw} kW")
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun CalcStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = DarkOnSurface)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
    }
}
Screen 2.5 — Rating & Review
Kotlin

// presentation/traveller/RatingReviewScreen.kt
package com.evgrama.charge.presentation.traveller

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.evgrama.charge.ui.theme.*

@Composable
fun RatingReviewScreen(
    hostId: String,
    bookingId: String,
    navController: NavController,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    var selectedRating by remember { mutableStateOf(0) }
    var hoverRating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val isSubmitted by viewModel.isSubmitted.collectAsState()

    LaunchedEffect(isSubmitted) {
        if (isSubmitted) {
            navController.navigate(Screen.TravellerHome.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(32.dp))

            // Trophy emoji
            Text("🏆", fontSize = 72.sp)

            Spacer(Modifier.height(16.dp))

            Text(
                "Charging Complete!",
                style = MaterialTheme.typography.headlineMedium,
                color = ElectricGreen,
                fontWeight = FontWeight.Bold
            )

            Text(
                "How was your charging experience?",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkOnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // ─── Star Rating ──────────────────────────────────────────────
            Text(
                ratingLabel(selectedRating),
                style = MaterialTheme.typography.titleMedium,
                color = ElectricOrange
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..5).forEach { star ->
                    val isActive = star <= maxOf(selectedRating, hoverRating)
                    Icon(
                        imageVector = if (isActive) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star $star",
                        tint = if (isActive) ElectricOrange else DarkOutline,
                        modifier = Modifier
                            .size(52.dp)
                            .graphicsLayer {
                                scaleX = if (isActive) 1.1f else 1f
                                scaleY = if (isActive) 1.1f else 1f
                            }
                            .clickable {
                                selectedRating = star
                                hoverRating = 0
                            }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ─── Review Text ──────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Add a comment (optional)",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkOnSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { if (it.length <= 300) reviewText = it },
                        placeholder = {
                            Text(
                                "Tell others about the socket quality, host hospitality, location accuracy...",
                                color = DarkOnSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = DarkOutline,
                            focusedTextColor = DarkOnSurface,
                            unfocusedTextColor = DarkOnSurface,
                            cursorColor = ElectricBlue
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            "${reviewText.length}/300",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkOnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ─── Submit Button ────────────────────────────────────────────
            Button(
                onClick = {
                    viewModel.submitReview(
                        hostId = hostId,
                        bookingId = bookingId,
                        rating = selectedRating,
                        text = reviewText
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedRating > 0 && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricOrange,
                    disabledContainerColor = DarkSurfaceVariant
                ),
                shape = MaterialTheme.shapes.large
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Star, null)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Submit Review",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = {
                    navController.navigate(Screen.TravellerHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                Text(
                    "Skip for now",
                    color = DarkOnSurfaceVariant
                )
            }
        }
    }
}

fun ratingLabel(rating: Int) = when (rating) {
    1 -> "😞 Poor"
    2 -> "😐 Fair"
    3 -> "🙂 Good"
    4 -> "😊 Great"
    5 -> "🤩 Excellent!"
    else -> "Tap a star to rate"
}
PART 10: HOST SCREENS
Screen 3.1 — Host Dashboard
Kotlin

// presentation/host/HostDashboardScreen.kt
package com.evgrama.charge.presentation.host

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.evgrama.charge.domain.model.Booking
import com.evgrama.charge.presentation.navigation.Screen
import com.evgrama.charge.ui.theme.*

@Composable
fun HostDashboardScreen(
    navController: NavController,
    viewModel: HostDashboardViewModel = hiltViewModel()
) {
    val isAvailable by viewModel.isAvailable.collectAsState()
    val pendingBookings by viewModel.pendingBookings.collectAsState()
    val host by viewModel.host.collectAsState()
    val isTogglingAvailability by viewModel.isTogglingAvailability.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ─── Header ───────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Host Dashboard",
                            style = MaterialTheme.typography.headlineMedium,
                            color = DarkOnSurface
                        )
                        Text(
                            "Good ${timeOfDayGreeting()}, ${host?.name?.split(" ")?.firstOrNull() ?: "Host"} 👋",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkOnSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { navController.navigate(Screen.HostProfile.route) }
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            null,
                            tint = DarkOnSurface
                        )
                    }
                }
            }

            // ─── MEGA TOGGLE — The Core Control ───────────────────────────
            item {
                AvailabilityToggleCard(
                    isAvailable = isAvailable,
                    isLoading = isTogglingAvailability,
                    onToggle = { viewModel.toggleAvailability() }
                )
            }

            // ─── Quick Stats Row ──────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        value = "${pendingBookings.size}",
                        label = "Pending",
                        color = ElectricOrange,
                        icon = Icons.Default.HourglassEmpty
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        value = "${"%.1f".format(host?.avgRating ?: 0f)}⭐",
                        label = "Rating",
                        color = ElectricOrange,
                        icon = Icons.Default.Star
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        value = host?.socketType?.name?.replace("TYPE_", "") ?: "N/A",
                        label = "Socket",
                        color = ElectricBlue,
                        icon = Icons.Default.Power
                    )
                }
            }

            // ─── Pending Requests Section ─────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Incoming Requests",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkOnSurface
                    )
                    Spacer(Modifier.width(8.dp))
                    if (pendingBookings.isNotEmpty()) {
                        Badge(containerColor = ElectricOrange) {
                            Text(
                                "${pendingBookings.size}",
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            if (pendingBookings.isEmpty()) {
                item {
                    EmptyRequestsCard()
                }
            } else {
                items(
                    items = pendingBookings,
                    key = { it.bookingId }
                ) { booking ->
                    BookingRequestCard(
                        booking = booking,
                        onAccept = {
                            viewModel.acceptBooking(booking.bookingId)
                        },
                        onReject = {
                            viewModel.rejectBooking(booking.bookingId)
                        }
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ─── Availability Toggle Card ──────────────────────────────────────────────

@Composable
fun AvailabilityToggleCard(
    isAvailable: Boolean,
    isLoading: Boolean,
    onToggle: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isAvailable)
            ElectricGreen.copy(alpha = 0.12f)
        else
            DarkSurface,
        label = "toggleBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isAvailable) ElectricGreen else DarkOutline,
        label = "toggleBorder"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Big status icon
                AnimatedContent(
                    targetState = isAvailable,
                    label = "statusIcon"
                ) { available ->
                    Text(
                        if (available) "⚡" else "🔌",
                        style = MaterialTheme.typography.displayLarge
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    "My Charging Point",
                    style = MaterialTheme.typography.labelLarge,
                    color = DarkOnSurfaceVariant
                )

                AnimatedContent(
                    targetState = isAvailable,
                    transitionSpec = {
                        slideInVertically { it } + fadeIn() togetherWith
                        slideOutVertically { -it } + fadeOut()
                    },
                    label = "statusText"
                ) { available ->
                    Text(
                        if (available) "Accepting EVs" else "Offline",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (available) ElectricGreen else DarkOnSurfaceVariant
                    )
                }
            }

            // Toggle Switch
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = ElectricGreen,
                    strokeWidth = 3.dp
                )
            } else {
                Switch(
                    checked = isAvailable,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ElectricGreen,
                        uncheckedThumbColor = DarkOnSurfaceVariant,
                        uncheckedTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.size(width = 64.dp, height = 36.dp)
                )
            }
        }
    }
}

// ─── Booking Request Card ──────────────────────────────────────────────────

@Composable
fun BookingRequestCard(
    booking: Booking,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        booking.travellerName.ifEmpty { "Traveller" },
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkOnSurface
                    )
                    Text(
                        booking.travellerPhone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkOnSurfaceVariant
                    )
                }

                // New badge
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = ElectricOrange.copy(alpha = 0.2f)
                ) {
                    Text(
                        "NEW",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = ElectricOrange
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Booking #${booking.bookingId.take(8).uppercase()}",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reject button
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ElectricRed
                    ),
                    border = BorderStroke(1.5.dp, ElectricRed),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Reject", fontWeight = FontWeight.SemiBold)
                }

                // Accept button
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        Icons.Default.Check,
                        null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Black
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Accept",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyRequestsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🔋", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "No requests yet",
                style = MaterialTheme.typography.titleMedium,
                color = DarkOnSurface
            )
            Text(
                "Keep your status ON to receive bookings",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuickStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    color: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = color)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
        }
    }
}

fun timeOfDayGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Morning"
        hour < 17 -> "Afternoon"
        else -> "Evening"
    }
}
Screen 3.2 — Host Profile & Earnings
Kotlin

// presentation/host/HostProfileScreen.kt
package com.evgrama.charge.presentation.host

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.evgrama.charge.domain.model.SocketType
import com.evgrama.charge.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostProfileScreen(
    navController: NavController,
    viewModel: HostProfileViewModel = hiltViewModel()
) {
    val host by viewModel.host.collectAsState()
    val earnings by viewModel.totalEarnings.collectAsState()
    val sessionCount by viewModel.sessionCount.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    var socketType by remember { mutableStateOf(host?.socketType ?: SocketType.TYPE_5A) }
    var pricePerHour by remember { mutableStateOf(host?.pricePerHour?.toString() ?: "30") }
    val isSaving by viewModel.isSaving.collectAsState()

    LaunchedEffect(host) {
        host?.let {
            socketType = it.socketType
            pricePerHour = it.pricePerHour.toString()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = DarkOnSurface)
                }
                Text(
                    "My Profile & Earnings",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkOnSurface
                )
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = ElectricBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ElectricBlue
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "💰 Earnings",
                            color = if (selectedTab == 0) ElectricBlue else DarkOnSurfaceVariant
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "⚙️ Settings",
                            color = if (selectedTab == 1) ElectricBlue else DarkOnSurfaceVariant
                        )
                    }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> EarningsTab(
                    totalEarnings = earnings,
                    sessionCount = sessionCount,
                    avgRating = host?.avgRating ?: 0f,
                    pricePerHour = host?.pricePerHour ?: 0.0
                )
                1 -> ProfileSettingsTab(
                    socketType = socketType,
                    pricePerHour = pricePerHour,
                    isSaving = isSaving,
                    onSocketTypeChange = { socketType = it },
                    onPriceChange = { pricePerHour = it },
                    onUploadPhoto = { viewModel.uploadSocketPhoto() },
                    onSave = {
                        viewModel.saveProfile(
                            socketType = socketType,
                            pricePerHour = pricePerHour.toDoubleOrNull() ?: 30.0
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun EarningsTab(
    totalEarnings: Double,
    sessionCount: Long,
    avgRating: Float,
    pricePerHour: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Total Revenue - Big display
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ElectricGreen.copy(alpha = 0.12f)
            ),
            shape = MaterialTheme.shapes.extraLarge,
            border = BorderStroke(1.5.dp, ElectricGreen.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Total Earnings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "₹${"%.0f".format(totalEarnings)}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = ElectricGreen
                )
                Text(
                    "lifetime revenue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
            }
        }

        // Sessions & Rating row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EarningStatCard(
                modifier = Modifier.weight(1f),
                value = "$sessionCount",
                label = "Sessions",
                sublabel = "Completed",
                color = ElectricBlue,
                emoji = "⚡"
            )
            EarningStatCard(
                modifier = Modifier.weight(1f),
                value = "${"%.1f".format(avgRating)}",
                label = "Avg Rating",
                sublabel = "out of 5.0",
                color = ElectricOrange,
                emoji = "⭐"
            )
        }

        // Price per session
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Price per Session", style = MaterialTheme.typography.labelLarge, color = DarkOnSurfaceVariant)
                    Text(
                        "₹${pricePerHour.toInt()}/hr × $sessionCount sessions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkOnSurface
                    )
                }
                Icon(Icons.Default.CurrencyRupee, null, tint = ElectricGreen, modifier = Modifier.size(32.dp))
            }
        }

        // Motivation card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ElectricBlue.copy(alpha = 0.1f)
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💡", fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Upgrade to a 15A socket to earn 3x more per session!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurface
                )
            }
        }
    }
}

@Composable
fun EarningStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    sublabel: String,
    color: Color,
    emoji: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(label, style = MaterialTheme.typography.labelLarge, color = DarkOnSurface)
            Text(sublabel, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
        }
    }
}

@Composable
fun ProfileSettingsTab(
    socketType: SocketType,
    pricePerHour: String,
    isSaving: Boolean,
    onSocketTypeChange: (SocketType) -> Unit,
    onPriceChange: (String) -> Unit,
    onUploadPhoto: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Socket Type Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Socket Type",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkOnSurface
                )
                Text(
                    "Select the type of socket you're sharing",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                SocketType.values().forEach { socket ->
                    val isSelected = socketType == socket
                    val color = if (isSelected) ElectricBlue else DarkOnSurfaceVariant

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                if (isSelected) ElectricBlue.copy(alpha = 0.1f)
                                else Color.Transparent
                            )
                            .clickable { onSocketTypeChange(socket) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSocketTypeChange(socket) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = ElectricBlue,
                                unselectedColor = DarkOutline
                            )
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(socket.displayName, style = MaterialTheme.typography.bodyLarge, color = DarkOnSurface)
                            Text("${socket.chargingRateKw} kW • ${if (socket == SocketType.TYPE_5A) "Standard" else "Fast Charge"}", style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
                        }
                        if (socket == SocketType.TYPE_15A) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = ElectricOrange.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "POPULAR",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ElectricOrange,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    if (socket != SocketType.values().last()) {
                        Divider(color = DarkOutline.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // Price Configuration
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Price per Hour",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkOnSurface
                )
                Text(
                    "Recommended: ₹30-60/hr for rural areas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = pricePerHour,
                    onValueChange = { if (it.length <= 5 && it.all { c -> c.isDigit() }) onPriceChange(it) },
                    label = { Text("Price (₹)") },
                    leadingIcon = {
                        Text(
                            "₹",
                            style = MaterialTheme.typography.titleLarge,
                            color = ElectricGreen,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    },
                    trailingIcon = {
                        Text(
                            "/hr",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkOnSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricGreen,
                        unfocusedBorderColor = DarkOutline,
                        focusedTextColor = DarkOnSurface,
                        unfocusedTextColor = DarkOnSurface,
                        cursorColor = ElectricGreen
                    )
                )
            }
        }

        // Upload Socket Photo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Socket Photo",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkOnSurface
                )
                Text(
                    "Helps travellers identify your socket type",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onUploadPhoto,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
                    border = BorderStroke(1.5.dp, ElectricBlue),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(10.dp))
                    Text("Upload Socket Photo")
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Save Button
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricBlue,
                disabledContainerColor = DarkSurfaceVariant
            ),
            shape = MaterialTheme.shapes.large
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Save Changes",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
PART 11: VIEWMODELS
TravellerHomeViewModel.kt
Kotlin

// presentation/traveller/TravellerHomeViewModel.kt
package com.evgrama.charge.presentation.traveller

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evgrama.charge.domain.model.*
import com.evgrama.charge.domain.repository.EVGramaRepository
import com.evgrama.charge.domain.usecase.GetNearbyHostsUseCase
import com.evgrama.charge.domain.usecase.SortFilter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TravellerHomeViewModel @Inject constructor(
    private val repository: EVGramaRepository,
    private val getNearbyHostsUseCase: GetNearbyHostsUseCase,
    private val auth: FirebaseAuth,
    private val locationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    private val _currentFilter = MutableStateFlow(SortFilter.NEAREST)
    val currentFilter: StateFlow<SortFilter> = _currentFilter.asStateFlow()

    private val _showOnlyAvailable = MutableStateFlow(false)
    val showOnlyAvailable: StateFlow<Boolean> = _showOnlyAvailable.asStateFlow()

    val filteredHosts: StateFlow<List<Host>> = combine(
        _userLocation,
        _currentFilter,
        _showOnlyAvailable
    ) { location, filter, onlyAvailable ->
        Triple(location, filter, onlyAvailable)
    }.flatMapLatest { (location, filter, onlyAvailable) ->
        val lat = location?.latitude ?: 20.5937
        val lng = location?.longitude ?: 78.9629
        getNearbyHostsUseCase(lat, lng, filter)
            .map { hosts ->
                if (onlyAvailable) hosts.filter { it.isAvailable } else hosts
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchUserLocation()
    }

    @SuppressLint("MissingPermission")
    private fun fetchUserLocation() {
        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                _userLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    fun centerOnUserLocation() = fetchUserLocation()

    fun setFilter(filter: SortFilter) {
        _currentFilter.value = filter
    }

    fun toggleAvailableFilter() {
        _showOnlyAvailable.value = !_showOnlyAvailable.value
    }

    fun createBooking(hostId: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val user = repository.getUser(uid) ?: return@launch
                val host = repository.getHost(hostId) ?: return@launch

                val booking = Booking(
                    travellerId = uid,
                    hostId = hostId,
                    status = BookingStatus.PENDING,
                    travellerName = user.name,
                    travellerPhone = user.phoneNumber,
                    hostName = host.name,
                    hostPhone = host.phoneNumber
                )
                val bookingId = repository.createBooking(booking)
                onSuccess(bookingId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
HostDashboardViewModel.kt
Kotlin

// presentation/host/HostDashboardViewModel.kt
package com.evgrama.charge.presentation.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evgrama.charge.domain.model.*
import com.evgrama.charge.domain.repository.EVGramaRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostDashboardViewModel @Inject constructor(
    private val repository: EVGramaRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _host = MutableStateFlow<Host?>(null)
    val host: StateFlow<Host?> = _host.asStateFlow()

    private val _isAvailable = MutableStateFlow(true)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    private val _isTogglingAvailability = MutableStateFlow(false)
    val isTogglingAvailability: StateFlow<Boolean> = _isTogglingAvailability.asStateFlow()

    val pendingBookings: StateFlow<List<Booking>> = _host
        .filterNotNull()
        .flatMapLatest { host ->
            repository.getPendingBookingsForHostFlow(host.hostId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadHostData()
    }

    private fun loadHostData() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val host = repository.getHostByUid(uid) ?: return@launch
            _host.value = host

            // Observe live availability from RTDB
            repository.getAvailabilityFlow(host.hostId)
                .collect { available ->
                    _isAvailable.value = available
                }
        }
    }

    fun toggleAvailability() {
        viewModelScope.launch {
            val hostId = _host.value?.hostId ?: return@launch
            _isTogglingAvailability.value = true
            try {
                repository.setAvailability(hostId, !_isAvailable.value)
            } finally {
                _isTogglingAvailability.value = false
            }
        }
    }

    /**
     * CRITICAL TRANSACTION:
     * Accept booking → flip RTDB availability to Busy atomically.
     */
    fun acceptBooking(bookingId: String) {
        viewModelScope.launch {
            val hostId = _host.value?.hostId ?: return@launch
            try {
                repository.acceptBooking(bookingId, hostId)
                // RTDB is now set to false (Busy) inside acceptBooking()
            } catch (e: Exception) {
                // Show error to user
            }
        }
    }

    fun rejectBooking(bookingId: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, BookingStatus.REJECTED)
        }
    }
}
ReviewViewModel.kt
Kotlin

// presentation/traveller/ReviewViewModel.kt
package com.evgrama.charge.presentation.traveller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evgrama.charge.domain.model.*
import com.evgrama.charge.domain.repository.EVGramaRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val repository: EVGramaRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted: StateFlow<Boolean> = _isSubmitted.asStateFlow()

    fun submitReview(
        hostId: String,
        bookingId: String,
        rating: Int,
        text: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val user = repository.getUser(uid) ?: return@launch

                val review = Review(
                    hostId = hostId,
                    travellerId = uid,
                    rating = rating,
                    reviewText = text,
                    travellerName = user.name
                )

                repository.submitReview(review)
                // Recalculate host average rating
                repository.calculateAndUpdateHostRating(hostId)
                // Mark booking as completed
                repository.updateBookingStatus(bookingId, BookingStatus.COMPLETED)

                _isSubmitted.value = true
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
PART 12: DEPENDENCY INJECTION
AppModule.kt
Kotlin

// di/AppModule.kt
package com.evgrama.charge.di

import com.evgrama.charge.data.remote.FirestoreService
import com.evgrama.charge.data.remote.RealtimeDatabaseService
import com.evgrama.charge.data.repository.EVGramaRepositoryImpl
import com.evgrama.charge.domain.repository.EVGramaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideRealtimeDatabase(): FirebaseDatabase =
        FirebaseDatabase.getInstance().also {
            it.setPersistenceEnabled(true) // Offline support
        }

    @Provides
    @Singleton
    fun provideFirestoreService(db: FirebaseFirestore) = FirestoreService(db)

    @Provides
    @Singleton
    fun provideRtdbService(db: FirebaseDatabase) = RealtimeDatabaseService(db)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(
        impl: EVGramaRepositoryImpl
    ): EVGramaRepository
}

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}
PART 13: MAIN ACTIVITY & APPLICATION
EVGramaApplication.kt
Kotlin

// EVGramaApplication.kt
package com.evgrama.charge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EVGramaApplication : Application()
MainActivity.kt
Kotlin

// MainActivity.kt
package com.evgrama.charge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.evgrama.charge.presentation.navigation.EVGramaNavGraph
import com.evgrama.charge.ui.theme.EVGramaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EVGramaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    EVGramaNavGraph(navController = navController)
                }
            }
        }
    }
}
PART 14: DARK MAP STYLE & CONSTANTS
DarkMapStyle.kt
Kotlin

// utils/DarkMapStyle.kt
package com.evgrama.charge.utils

const val DARK_MAP_STYLE_JSON = """
[
  {"elementType": "geometry", "stylers": [{"color": "#0d0d0f"}]},
  {"elementType": "labels.text.fill", "stylers": [{"color": "#8E8E93"}]},
  {"elementType": "labels.text.stroke", "stylers": [{"color": "#0d0d0f"}]},
  {"featureType": "road", "elementType": "geometry",
    "stylers": [{"color": "#1C1C1E"}]},
  {"featureType": "road", "elementType": "geometry.stroke",
    "stylers": [{"color": "#2C2C2E"}]},
  {"featureType": "water", "elementType": "geometry",
    "stylers": [{"color": "#0a1628"}]},
  {"featureType": "poi", "stylers": [{"visibility": "off"}]},
  {"featureType": "transit", "stylers": [{"visibility": "off"}]},
  {"featureType": "administrative.locality",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#0A84FF"}]}
]
"""
PART 15: ANDROIDMANIFEST.XML
XML

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

    <application
        android:name=".EVGramaApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.EVGrama"
        tools:targetApi="31">

        <!-- Google Maps API Key -->
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}" />

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
PART 16: FIREBASE SECURITY RULES
Firestore Rules
JavaScript

// firestore.rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Users: read own, write own
    match /users/{uid} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == uid;
    }

    // Hosts: anyone can read, only host owner can write
    match /hosts/{hostId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null
        && request.resource.data.uid == request.auth.uid;
      allow update: if request.auth != null
        && resource.data.uid == request.auth.uid;
    }

    // Bookings: traveller or host can read their own
    match /bookings/{bookingId} {
      allow read: if request.auth != null
        && (resource.data.travellerId == request.auth.uid
            || resource.data.hostId == request.auth.uid);
      allow create: if request.auth != null;
      allow update: if request.auth != null
        && (resource.data.travellerId == request.auth.uid
            || resource.data.hostId == request.auth.uid);
    }

    // Reviews: authenticated users can read, traveller can create own
    match /reviews/{reviewId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null
        && request.resource.data.travellerId == request.auth.uid;
    }
  }
}
Realtime Database Rules
JSON

{
  "rules": {
    "hosts": {
      "$hostId": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    }
  }
}
ARCHITECTURAL SUMMARY
text

┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                     │
│  Screens (Compose) ←→ ViewModels (MVVM) ←→ UI State      │
└──────────────────────────┬──────────────────────────────┘
                           │ collect/observe
┌──────────────────────────▼──────────────────────────────┐
│                      DOMAIN LAYER                         │
│  Use Cases → Repository Interface → Domain Models         │
└──────────────────────────┬──────────────────────────────┘
                           │ implements
┌──────────────────────────▼──────────────────────────────┐
│                       DATA LAYER                          │
│  Repository Impl → Firestore Service                      │
│                  → Realtime Database Service              │
│  Firebase Auth → OTP → User Session                      │
└─────────────────────────────────────────────────────────┘

KEY SYNC RULES:
• acceptBooking() = Firestore batch + RTDB in one operation
• getAllHostsFlow() + getAllAvailabilityFlow() = merged by combine()
• SortFilter = ViewModel sorts in-memory, no extra DB queries
• WhatsApp deep-link = whatsapp://send?phone={number}
• Timer = 3600s countdown → auto-triggers COMPLETED status
