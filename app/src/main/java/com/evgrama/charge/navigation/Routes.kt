package com.evgrama.charge.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object RoleSelection : Screen("role_selection")
    
    // Host Flow
    object HostSetup : Screen("host_setup")
    object HostDashboard : Screen("host_dashboard")
    object EditHostProfile : Screen("edit_host_profile")
    
    // Traveller Flow
    object HomeMap : Screen("home_map")
    object Booking : Screen("booking")
    object BookingLiveStatus : Screen("booking_live_status")
    object ChargingCalculator : Screen("charging_calculator")
    
    // Global
    object BookingHistory : Screen("booking_history")
    object RatingsReviews : Screen("ratings_reviews")
    object Notifications : Screen("notifications")
    object SettingsProfile : Screen("settings_profile")
}
