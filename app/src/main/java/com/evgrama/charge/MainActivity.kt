package com.evgrama.charge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class MainActivity : Activity() {
    private var role = UserRole.Traveller
    private var userName = ""
    private var userPhone = ""
    private var activeScreen = Screen.Login
    private var selectedHost: Host? = null
    private var activeBooking: Booking? = null
    private var hostAvailable = true
    private var hostSocket = SocketType.FifteenAmp
    private var hostPrice = 90
    private var reviewRating = 5
    private var userBalance = 1250
    private var vehicleModel = "Ather 450X"
    private var vehicleCapacity = 3.7
    private var hostStartHour = 9
    private var hostEndHour = 20
    private var selectedLanguage = "English"
    private var isVerified = false
    private var upiId = ""
    private val favouriteIds = mutableSetOf<String>()
    private val referralCode = "EVGRAMA${(1000..9999).random()}"

    private val userHistory = mutableListOf<Booking>()
    private val notifications = mutableListOf(
        "Welcome to EV-Grama! Your profile is 80% complete.",
        "New Host 'Hilltop Fast Point' is now online near you.",
        "Your last session at Lakshmi Home was rated 5 stars."
    )
    private val messages = mutableListOf(
        Message("h1", "Host", "Hello! Are you coming for the 2 PM slot?"),
        Message("user", "You", "Yes, I'm 5 mins away.")
    )
    private val communityNews = listOf(
        "New charging point opened in Kattappana Market!",
        "EV subsidy update: Check the local panchayat office for details.",
        "Rainy season safety: Tips for home charging during monsoon."
    )

    private val hosts = mutableListOf(
        Host("h1", "Lakshmi Home Charge", "Munnar Road, Idukki", SocketType.FifteenAmp, 90, 1.4, 4.8f, true, 10.0159, 77.0600),
        Host("h2", "Green Courtyard Plug", "Bus Stand Lane, Kattappana", SocketType.FiveAmp, 45, 2.7, 4.5f, true, 9.7224, 77.1166),
        Host("h3", "Hilltop Fast Point", "Tea Factory Junction", SocketType.FifteenAmp, 110, 4.2, 4.9f, false, 10.0889, 77.0595)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        render()
    }

    private fun render() {
        val scroll = ScrollView(this)
        scroll.setBackgroundColor(Colors.background)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(32))
        }
        scroll.addView(content)

        when (activeScreen) {
            Screen.Login -> renderLogin(content)
            Screen.TravellerHome -> renderTravellerHome(content)
            Screen.BookingStatus -> renderBookingStatus(content)
            Screen.HostDashboard -> renderHostDashboard(content)
            Screen.Calculator -> renderCalculator(content)
            Screen.Review -> renderReview(content)
            Screen.Support -> renderSupport(content)
            Screen.History -> renderHistory(content)
            Screen.Community -> renderCommunity(content)
            Screen.Profile -> renderProfile(content)
            Screen.Wallet -> renderWallet(content)
            Screen.Notifications -> renderNotifications(content)
            Screen.Chat -> renderChat(content)
            Screen.Favourites -> renderFavourites(content)
            Screen.Referrals -> renderReferrals(content)
            Screen.Checklist -> renderChecklist(content)
            Screen.VehicleProfile -> renderVehicleProfile(content)
            Screen.HostSchedule -> renderHostSchedule(content)
            Screen.Leaderboard -> renderLeaderboard(content)
            Screen.Settings -> renderSettings(content)
            Screen.Verification -> renderVerification(content)
            Screen.Payment -> renderPayment(content)
        }

        setContentView(scroll)
    }

    private fun renderLogin(root: LinearLayout) {
        title(root, "EV-Grama Charge")
        subtitle(root, "Community EV charging for travellers and home hosts.")
        spacer(root, 24)

        val nameInput = input("Your name")
        val phoneInput = input("Mobile number")
        root.addView(nameInput)
        root.addView(phoneInput)
        spacer(root, 16)

        section(root, "Choose role")
        row(root) {
            addView(choice("Traveller", role == UserRole.Traveller) { role = UserRole.Traveller; render() })
            addView(choice("Host", role == UserRole.Host) { role = UserRole.Host; render() })
        }

        primaryButton(root, "Continue") {
            userName = nameInput.text.toString().trim().ifEmpty { "Guest User" }
            userPhone = phoneInput.text.toString().trim().ifEmpty { "+91 98765 43210" }
            activeScreen = if (role == UserRole.Traveller) Screen.TravellerHome else Screen.HostDashboard
            render()
        }
    }

    private fun renderTravellerHome(root: LinearLayout) {
        header(root, "Nearby charging hosts", "Hi $userName. Pick a socket, request a session, or estimate charging time.")
        
        row(root) {
            addView(choice("⚡ Calculator", false) { activeScreen = Screen.Calculator; render() })
            addView(choice("🏆 League", false) { activeScreen = Screen.Leaderboard; render() })
        }
        row(root) {
            addView(choice("📋 History", false) { activeScreen = Screen.History; render() })
            addView(choice("🌐 Community", false) { activeScreen = Screen.Community; render() })
        }
        row(root) {
            addView(choice("🚗 My EV", false) { activeScreen = Screen.VehicleProfile; render() })
            addView(choice("⚙️ Settings", false) { activeScreen = Screen.Settings; render() })
        }
        
        spacer(root, 16)

        hosts.sortedBy { it.distanceKm }.forEach { host ->
            card(root) {
                row(this) {
                    addView(TextView(context).apply {
                        text = host.name
                        setTextColor(Color.WHITE)
                        textSize = 19f
                        typeface = Typeface.DEFAULT_BOLD
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    })
                    val isFav = favouriteIds.contains(host.id)
                    addView(TextView(context).apply {
                        text = if (isFav) "❤️" else "🤍"
                        textSize = 20f
                        setOnClickListener {
                            if (isFav) favouriteIds.remove(host.id) else favouriteIds.add(host.id)
                            render()
                        }
                    })
                    spacerHorizontal(this, 12)
                    addView(badge(if (host.available) "Open" else "Busy", host.available))
                }
                body(this, host.address)
                body(this, "${host.socket.label} • ${host.distanceKm} km • Rs ${host.pricePerHour}/hour • ${host.rating} rating")
                row(this) {
                    primaryButtonSmall(this, if (host.available) "Request" else "Join later") {
                        selectedHost = host
                        activeBooking = Booking(host.id, BookingStatus.Pending)
                        activeScreen = Screen.BookingStatus
                        render()
                    }
                    spacerHorizontal(this, 10)
                    secondaryButtonSmall(this, "Map") {
                        openMap(host.lat, host.lng, host.name)
                    }
                    spacerHorizontal(this, 10)
                    secondaryButtonSmall(this, "Message") {
                        activeScreen = Screen.Chat
                        render()
                    }
                }
            }
        }
    }

    private fun renderBookingStatus(root: LinearLayout) {
        val host = selectedHost ?: hosts.first()
        val booking = activeBooking ?: Booking(host.id, BookingStatus.Pending)
        header(root, "Booking request", host.name)

        card(root) {
            section(this, booking.status.label)
            row(this) {
                addView(TextView(context).apply {
                    text = if (isVerified) "✅ Verified Traveller" else "⚠️ Unverified Account"
                    setTextColor(if (isVerified) Colors.primary else Colors.warning)
                    textSize = 12f
                })
            }
            body(this, "Host: ${host.name}")
            body(this, "Socket: ${host.socket.label}")
            body(this, "Estimated cost: Rs ${host.pricePerHour}/hour")
            primaryButton(this, "Get Directions (Google Maps)") {
                openMap(host.lat, host.lng, host.name)
            }
        }

        when (booking.status) {
            BookingStatus.Pending -> {
                secondaryButton(root, "Simulate host accepting") {
                    activeBooking = booking.copy(status = BookingStatus.Accepted)
                    render()
                }
            }
            BookingStatus.Accepted -> {
                primaryButton(root, "Pay via UPI (Rs ${host.pricePerHour})") {
                    activeScreen = Screen.Payment
                    render()
                }
                secondaryButton(root, "Mark session completed") {
                    activeBooking = booking.copy(status = BookingStatus.Completed)
                    activeScreen = Screen.Review
                    render()
                }
            }
            BookingStatus.Completed -> {
                primaryButton(root, "Review host") {
                    activeScreen = Screen.Review
                    render()
                }
            }
        }
        secondaryButton(root, "Back to hosts") {
            activeScreen = Screen.TravellerHome
            render()
        }
    }

    private fun renderHostDashboard(root: LinearLayout) {
        header(root, "Host dashboard", "Manage availability, pricing, requests, and profile.")

        row(root) {
            addView(choice("👤 Profile", false) { activeScreen = Screen.Profile; render() })
            addView(choice("📅 Schedule", false) { activeScreen = Screen.HostSchedule; render() })
            addView(choice("⚙️ Settings", false) { activeScreen = Screen.Settings; render() })
        }
        spacer(root, 12)

        card(root) {
            section(this, "Your charging point")
            body(this, "Status: ${if (hostAvailable) "Available" else "Unavailable"}")
            body(this, "Socket: ${hostSocket.label}")
            body(this, "Price: Rs $hostPrice/hour")
            row(this) {
                addView(choice("5A", hostSocket == SocketType.FiveAmp) { hostSocket = SocketType.FiveAmp; render() })
                addView(choice("15A", hostSocket == SocketType.FifteenAmp) { hostSocket = SocketType.FifteenAmp; render() })
            }
            primaryButton(this, if (hostAvailable) "Pause availability" else "Go available") {
                hostAvailable = !hostAvailable
                hosts[0] = hosts[0].copy(available = hostAvailable, socket = hostSocket, pricePerHour = hostPrice)
                render()
            }
        }

        val priceInput = input("Price per hour").apply { setText(hostPrice.toString()) }
        root.addView(priceInput)
        primaryButton(root, "Save host profile") {
            hostPrice = priceInput.text.toString().toIntOrNull() ?: hostPrice
            hosts[0] = hosts[0].copy(available = hostAvailable, socket = hostSocket, pricePerHour = hostPrice)
            Toast.makeText(this, "Host profile saved", Toast.LENGTH_SHORT).show()
            render()
        }

        section(root, "Pending requests")
        card(root) {
            body(this, "Traveller: ${userName.ifEmpty { "Rahul Nair" }}")
            body(this, "Requested socket: ${hostSocket.label}")
            row(this) {
                addView(choice("Accept", false) { Toast.makeText(context, "Request accepted", Toast.LENGTH_SHORT).show() })
                addView(choice("Reject", false) { Toast.makeText(context, "Request rejected", Toast.LENGTH_SHORT).show() })
            }
        }
    }

    private fun renderCalculator(root: LinearLayout) {
        header(root, "Charging calculator", "Estimate range for your $vehicleModel.")
        val battery = input("Battery capacity kWh").apply { setText(vehicleCapacity.toString()) }
        val start = input("Current battery %").apply { setText("25") }
        val target = input("Target battery %").apply { setText("80") }
        root.addView(battery)
        root.addView(start)
        root.addView(target)

        card(root) {
            val needed = ((30 * (80 - 25)) / 100.0)
            body(this, "With a 15A socket, this adds about ${needed.roundToInt()} kWh in ${(needed / 3.3).roundToInt()} hours.")
            body(this, "Approx range gained: ${(needed * 6).roundToInt()} km")
        }

        primaryButton(root, "Calculate") {
            val cap = battery.text.toString().toDoubleOrNull() ?: 30.0
            val from = start.text.toString().toDoubleOrNull() ?: 25.0
            val to = target.text.toString().toDoubleOrNull() ?: 80.0
            val needed = cap * ((to - from).coerceAtLeast(0.0) / 100.0)
            val hours = needed / 3.3
            Toast.makeText(this, "${needed.roundToInt()} kWh, about ${hours.roundToInt()} hours", Toast.LENGTH_LONG).show()
        }
        secondaryButton(root, "Back") {
            activeScreen = Screen.TravellerHome
            render()
        }
    }

    private fun renderReview(root: LinearLayout) {
        val host = selectedHost ?: hosts.first()
        header(root, "Review session", host.name)
        section(root, "Rating")
        row(root) {
            (1..5).forEach { star ->
                addView(choice(star.toString(), reviewRating == star) {
                    reviewRating = star
                    render()
                })
            }
        }
        val review = input("Write a short review")
        root.addView(review)
        primaryButton(root, "Submit review") {
            val updated = ((host.rating + reviewRating) / 2.0).toFloat()
            hosts.replaceAll { if (it.id == host.id) it.copy(rating = updated) else it }
            Toast.makeText(this, "Thanks for reviewing ${host.name}", Toast.LENGTH_SHORT).show()
            activeScreen = Screen.TravellerHome
            render()
        }
    }

    private fun renderSupport(root: LinearLayout) {
        header(root, "Help & Support", "Get assistance or report issues.")
        card(root) {
            section(this, "Emergency Contact")
            body(this, "Roadside Assistance: +91 1800-456-789")
            primaryButton(this, "Call Support") {
                Toast.makeText(this@MainActivity, "Calling roadside assistance...", Toast.LENGTH_SHORT).show()
            }
        }
        card(root) {
            section(this, "App Issues")
            body(this, "Facing problems with booking or payment?")
            secondaryButton(this, "Report Technical Issue") {
                Toast.makeText(this@MainActivity, "Bug report sent to team.", Toast.LENGTH_SHORT).show()
            }
        }
        secondaryButton(root, "Back") { renderPrevious() }
    }

    private fun renderHistory(root: LinearLayout) {
        header(root, "Booking History", "Your past charging sessions.")
        if (userHistory.isEmpty()) {
            card(root) {
                body(this, "No past sessions found. Start charging now!")
            }
        } else {
            userHistory.forEach { booking ->
                val host = hosts.find { it.id == booking.hostId }
                card(root) {
                    body(this, "Host: ${host?.name ?: "Unknown"}")
                    body(this, "Status: ${booking.status.label}")
                    body(this, "Date: Oct 26, 2023")
                }
            }
        }
        secondaryButton(root, "Back") { activeScreen = Screen.TravellerHome; render() }
    }

    private fun renderCommunity(root: LinearLayout) {
        header(root, "Community Feed", "Local EV news and updates.")
        communityNews.forEach { news ->
            card(root) {
                body(this, news)
                row(this) {
                    addView(TextView(context).apply {
                        text = "Like • Comment • Share"
                        setTextColor(Colors.muted)
                        textSize = 12f
                    })
                }
            }
        }
        secondaryButton(root, "Back") { activeScreen = Screen.TravellerHome; render() }
    }

    private fun renderProfile(root: LinearLayout) {
        header(root, "Your Profile", "Manage your personal information.")
        card(root) {
            section(this, "Identity Status")
            row(this) {
                addView(TextView(context).apply {
                    text = if (isVerified) "Verified Citizen ✅" else "Verification Pending ⚠️"
                    setTextColor(if (isVerified) Colors.primary else Colors.warning)
                    textSize = 16f
                    typeface = Typeface.DEFAULT_BOLD
                })
            }
            if (!isVerified) {
                primaryButton(this, "Verify with Aadhaar") {
                    activeScreen = Screen.Verification
                    render()
                }
            }
        }
        card(root) {
            section(this, "Basic Info")
            body(this, "Name: $userName")
            body(this, "Phone: $userPhone")
            body(this, "UPI ID: ${upiId.ifEmpty { "Not set" }}")
        }
        primaryButton(root, "Edit Profile") {
            activeScreen = Screen.Login
            render()
        }
        secondaryButton(root, "Back to Dashboard") { renderPrevious() }
    }

    private fun renderVerification(root: LinearLayout) {
        header(root, "Identity Verification", "Secure your account with Aadhaar.")
        card(root) {
            body(this, "To build trust in the EV-Grama community, we verify all users.")
            val aadhaarIn = input("Enter 12-digit Aadhaar Number")
            addView(aadhaarIn)
            primaryButton(this, "Verify OTP") {
                if (aadhaarIn.text.length == 12) {
                    isVerified = true
                    Toast.makeText(this@MainActivity, "Identity Verified Successfully!", Toast.LENGTH_SHORT).show()
                    renderPrevious()
                } else {
                    Toast.makeText(this@MainActivity, "Please enter a valid 12-digit number", Toast.LENGTH_SHORT).show()
                }
            }
        }
        body(root, "🔒 Your data is encrypted and never shared with hosts.")
        secondaryButton(root, "Cancel") { renderPrevious() }
    }

    private fun renderPayment(root: LinearLayout) {
        val host = selectedHost ?: hosts.first()
        header(root, "UPI Payment", "Pay to ${host.name}")
        card(root) {
            section(this, "Amount to Pay")
            title(this, "Rs ${host.pricePerHour}")
            body(this, "Payment for 1-hour charging session.")
        }
        section(root, "Select UPI App")
        row(root) {
            addView(choice("PhonePe", false) { processMockPayment() })
            addView(choice("Google Pay", false) { processMockPayment() })
        }
        row(root) {
            addView(choice("Paytm", false) { processMockPayment() })
            addView(choice("BHIM", false) { processMockPayment() })
        }
        secondaryButton(root, "Pay with Wallet Balance") {
            if (userBalance >= host.pricePerHour) {
                userBalance -= host.pricePerHour
                processMockPayment()
            } else {
                Toast.makeText(this, "Insufficient wallet balance!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processMockPayment() {
        Toast.makeText(this, "Payment Successful! UPI Ref: ${System.currentTimeMillis()}", Toast.LENGTH_LONG).show()
        activeBooking = activeBooking?.copy(status = BookingStatus.Completed)
        activeScreen = Screen.Review
        render()
    }

    private fun renderPrevious() {
        activeScreen = if (role == UserRole.Traveller) Screen.TravellerHome else Screen.HostDashboard
        render()
    }

    private fun openMap(lat: Double, lng: Double, label: String) {
        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Fallback to web browser if Maps app is not installed
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    private fun renderWallet(root: LinearLayout) {
        header(root, "Your Wallet", "Manage credits and payments.")
        card(root) {
            section(this, "Available Balance")
            title(this, "Rs $userBalance")
            body(this, "Use these credits for instant booking.")
            primaryButton(this, "Add Money") {
                userBalance += 500
                Toast.makeText(this@MainActivity, "Rs 500 added to wallet!", Toast.LENGTH_SHORT).show()
                render()
            }
        }
        section(root, "Recent Transactions")
        card(root) {
            body(this, "Paid Lakshmi Home: -Rs 180")
            body(this, "Wallet Top-up: +Rs 500")
            body(this, "Paid Hilltop Fast: -Rs 220")
        }
        secondaryButton(root, "Back") { renderPrevious() }
    }

    private fun renderNotifications(root: LinearLayout) {
        header(root, "Notifications", "Stay updated on your sessions.")
        if (notifications.isEmpty()) {
            card(root) { body(this, "No new alerts.") }
        } else {
            notifications.forEach { note ->
                card(root) { body(this, note) }
            }
        }
        primaryButton(root, "Clear All") {
            notifications.clear()
            render()
        }
        secondaryButton(root, "Back") { renderPrevious() }
    }

    private fun renderChat(root: LinearLayout) {
        header(root, "Chat with Host", "Lakshmi Home Charge")
        val chatArea = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(Colors.surface, dp(12), Colors.line)
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        messages.forEach { msg ->
            val msgCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(8), dp(4), dp(8), dp(4))
                val isUser = msg.senderId == "user"
                gravity = if (isUser) Gravity.END else Gravity.START
                addView(TextView(context).apply {
                    text = "${msg.senderName}: ${msg.content}"
                    setTextColor(if (isUser) Colors.primary else Color.WHITE)
                    textSize = 14f
                })
            }
            chatArea.addView(msgCard)
        }
        root.addView(chatArea)
        spacer(root, 12)
        val msgInput = input("Type a message...")
        root.addView(msgInput)
        primaryButton(root, "Send") {
            if (msgInput.text.isNotEmpty()) {
                messages.add(Message("user", userName, msgInput.text.toString()))
                render()
            }
        }
        secondaryButton(root, "Back") { renderPrevious() }
    }

    private fun renderFavourites(root: LinearLayout) {
        header(root, "Favourite Hosts", "Quickly access your preferred spots.")
        val favs = hosts.filter { favouriteIds.contains(it.id) }
        if (favs.isEmpty()) {
            card(root) { body(this, "No favourites added yet.") }
        } else {
            favs.forEach { host ->
                card(root) {
                    row(this) {
                        addView(TextView(context).apply {
                            text = host.name
                            setTextColor(Color.WHITE)
                            textSize = 18f
                            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        })
                        primaryButtonSmall(this, "Go") {
                            selectedHost = host
                            activeBooking = Booking(host.id, BookingStatus.Pending)
                            activeScreen = Screen.BookingStatus
                            render()
                        }
                    }
                }
            }
        }
        secondaryButton(root, "Back") { activeScreen = Screen.TravellerHome; render() }
    }

    private fun renderReferrals(root: LinearLayout) {
        header(root, "Refer & Earn", "Invite friends and get Rs 100 credits.")
        card(root) {
            section(this, "Your Referral Code")
            title(this, referralCode)
            body(this, "Share this code with friends. When they complete their first charge, you both get Rs 100.")
            primaryButton(this, "Share Code") {
                Toast.makeText(this@MainActivity, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        }
        secondaryButton(root, "Back") { activeScreen = Screen.TravellerHome; render() }
    }

    private fun renderVehicleProfile(root: LinearLayout) {
        header(root, "My EV Profile", "Set your vehicle for better estimates.")
        card(root) {
            section(this, "Vehicle Details")
            val modelIn = input("EV Model (e.g., Ather 450)").apply { setText(vehicleModel) }
            val capIn = input("Battery Capacity (kWh)").apply { setText(vehicleCapacity.toString()) }
            addView(modelIn)
            addView(capIn)
            primaryButton(this, "Save Vehicle") {
                vehicleModel = modelIn.text.toString()
                vehicleCapacity = capIn.text.toString().toDoubleOrNull() ?: 3.7
                Toast.makeText(this@MainActivity, "Vehicle profile updated!", Toast.LENGTH_SHORT).show()
                renderPrevious()
            }
        }
        secondaryButton(root, "Back") { renderPrevious() }
    }

    private fun renderHostSchedule(root: LinearLayout) {
        header(root, "Service Hours", "Set when you are available to host.")
        card(root) {
            section(this, "Operating Window")
            body(this, "Current: $hostStartHour:00 to $hostEndHour:00")
            row(this) {
                addView(choice("Start +", false) { if (hostStartHour < 23) hostStartHour++; render() })
                addView(choice("Start -", false) { if (hostStartHour > 0) hostStartHour--; render() })
            }
            row(this) {
                addView(choice("End +", false) { if (hostEndHour < 23) hostEndHour++; render() })
                addView(choice("End -", false) { if (hostEndHour > hostStartHour) hostEndHour--; render() })
            }
            primaryButton(this, "Save Schedule") {
                Toast.makeText(this@MainActivity, "Operating hours saved!", Toast.LENGTH_SHORT).show()
                activeScreen = Screen.HostDashboard; render()
            }
        }
        secondaryButton(root, "Back") { activeScreen = Screen.HostDashboard; render() }
    }

    private fun renderLeaderboard(root: LinearLayout) {
        header(root, "Green League", "Top eco-friendly users this month.")
        card(root) {
            row(this) {
                addView(TextView(context).apply { text = "🥇 Rajesh M."; setTextColor(Color.WHITE); textSize = 16f; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) })
                addView(TextView(context).apply { text = "450 kWh"; setTextColor(Colors.primary); textSize = 16f })
            }
            spacer(this, 8)
            row(this) {
                addView(TextView(context).apply { text = "🥈 Suneetha V."; setTextColor(Color.WHITE); textSize = 16f; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) })
                addView(TextView(context).apply { text = "380 kWh"; setTextColor(Colors.primary); textSize = 16f })
            }
            spacer(this, 8)
            row(this) {
                addView(TextView(context).apply { text = "🥉 You (Guest)"; setTextColor(Color.WHITE); textSize = 16f; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) })
                addView(TextView(context).apply { text = "120 kWh"; setTextColor(Colors.primary); textSize = 16f })
            }
        }
        body(root, "Keep charging to climb the ranks and earn reward badges!")
        secondaryButton(root, "Back") { renderPrevious() }
    }

    private fun renderSettings(root: LinearLayout) {
        header(root, "App Settings", "Configure your experience.")
        card(root) {
            section(this, "Language")
            row(this) {
                addView(choice("English", selectedLanguage == "English") { selectedLanguage = "English"; render() })
                addView(choice("हिन्दी", selectedLanguage == "Hindi") { selectedLanguage = "Hindi"; render() })
                addView(choice("മലയാളം", selectedLanguage == "Malayalam") { selectedLanguage = "Malayalam"; render() })
            }
        }
        card(root) {
            section(this, "Account")
            secondaryButton(this, "Logout") { activeScreen = Screen.Login; render() }
        }
        secondaryButton(root, "Back") { renderPrevious() }
    }

    private fun renderChecklist(root: LinearLayout) {
        header(root, "Safety Checklist", "Check these before you plug in.")
        card(root) {
            body(this, "✅ Check for any burn marks on the socket.")
            body(this, "✅ Ensure the cable is not under tension.")
            body(this, "✅ Verify earthing is proper at the host location.")
            body(this, "✅ Keep the charger away from water or direct rain.")
        }
        primaryButton(root, "Understood") { renderPrevious() }
    }

    private fun header(root: LinearLayout, text: String, detail: String) {
        title(root, text)
        subtitle(root, detail)
        spacer(root, 18)
    }

    private fun title(root: LinearLayout, text: String) = root.addView(TextView(this).apply {
        this.text = text
        setTextColor(Color.WHITE)
        textSize = 30f
        typeface = Typeface.DEFAULT_BOLD
    })

    private fun subtitle(root: LinearLayout, text: String) = root.addView(TextView(this).apply {
        this.text = text
        setTextColor(Colors.muted)
        textSize = 16f
        setPadding(0, dp(8), 0, 0)
    })

    private fun section(root: LinearLayout, text: String) = root.addView(TextView(this).apply {
        this.text = text
        setTextColor(Colors.primary)
        textSize = 15f
        typeface = Typeface.DEFAULT_BOLD
        setPadding(0, dp(16), 0, dp(8))
    })

    private fun body(root: LinearLayout, text: String) = root.addView(TextView(this).apply {
        this.text = text
        setTextColor(Colors.text)
        textSize = 15f
        setPadding(0, dp(4), 0, dp(4))
    })

    private fun input(hintText: String) = EditText(this).apply {
        hint = hintText
        setHintTextColor(Colors.muted)
        setTextColor(Color.WHITE)
        textSize = 16f
        setSingleLine(true)
        background = rounded(Colors.surface, dp(12), Colors.line)
        setPadding(dp(14), 0, dp(14), 0)
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(54)).apply {
            setMargins(0, dp(8), 0, dp(8))
        }
    }

    private fun primaryButton(root: LinearLayout, text: String, onClick: () -> Unit) {
        root.addView(button(text, Colors.primary, Color.BLACK, onClick))
    }

    private fun primaryButtonSmall(root: LinearLayout, text: String, onClick: () -> Unit) {
        root.addView(button(text, Colors.primary, Color.BLACK, onClick).apply {
            layoutParams = LinearLayout.LayoutParams(0, dp(44), 1f)
        })
    }

    private fun secondaryButtonSmall(root: LinearLayout, text: String, onClick: () -> Unit) {
        root.addView(button(text, Colors.surface, Color.WHITE, onClick).apply {
            layoutParams = LinearLayout.LayoutParams(0, dp(44), 1f)
        })
    }

    private fun secondaryButton(root: LinearLayout, text: String, onClick: () -> Unit) {
        root.addView(button(text, Colors.surface, Color.WHITE, onClick))
    }

    private fun button(text: String, bg: Int, fg: Int, onClick: () -> Unit) = Button(this).apply {
        this.text = text
        setTextColor(fg)
        textSize = 15f
        typeface = Typeface.DEFAULT_BOLD
        background = rounded(bg, dp(12), bg)
        setOnClickListener { onClick() }
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            setMargins(0, dp(10), 0, 0)
        }
    }

    private fun choice(text: String, selected: Boolean, onClick: () -> Unit) = Button(this).apply {
        this.text = text
        setTextColor(if (selected) Color.BLACK else Color.WHITE)
        textSize = 14f
        background = rounded(if (selected) Colors.primary else Colors.surface, dp(12), Colors.line)
        setOnClickListener { onClick() }
        layoutParams = LinearLayout.LayoutParams(0, dp(48), 1f).apply {
            setMargins(dp(4), dp(4), dp(4), dp(4))
        }
    }

    private fun badge(text: String, positive: Boolean) = TextView(this).apply {
        this.text = text
        setTextColor(if (positive) Color.BLACK else Color.WHITE)
        textSize = 13f
        gravity = Gravity.CENTER
        background = rounded(if (positive) Colors.primary else Colors.warning, dp(20), Color.TRANSPARENT)
        layoutParams = LinearLayout.LayoutParams(dp(76), dp(32))
    }

    private fun card(root: LinearLayout, fill: LinearLayout.() -> Unit) {
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(16))
            background = rounded(Colors.surface, dp(14), Colors.line)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, dp(10), 0, dp(8))
            }
            fill()
        })
    }

    private fun row(root: LinearLayout, fill: LinearLayout.() -> Unit) {
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            fill()
        })
    }

    private fun spacer(root: LinearLayout, height: Int) = root.addView(Space(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(height))
    })

    private fun spacerHorizontal(root: LinearLayout, width: Int) = root.addView(Space(this).apply {
        layoutParams = LinearLayout.LayoutParams(dp(width), 1)
    })

    private fun rounded(color: Int, radius: Int, stroke: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
            setStroke(dp(1), stroke)
        }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()
}

private enum class Screen { Login, TravellerHome, BookingStatus, HostDashboard, Calculator, Review, Support, History, Community, Profile, Wallet, Notifications, Chat, Favourites, Referrals, Checklist, VehicleProfile, HostSchedule, Leaderboard, Settings, Verification, Payment }
private enum class UserRole { Traveller, Host }
private enum class BookingStatus(val label: String) { Pending("Pending host approval"), Accepted("Accepted"), Completed("Completed") }
private enum class SocketType(val label: String) { FiveAmp("5A regular socket"), FifteenAmp("15A fast socket") }

private data class Host(
    val id: String,
    val name: String,
    val address: String,
    val socket: SocketType,
    val pricePerHour: Int,
    val distanceKm: Double,
    val rating: Float,
    val available: Boolean,
    val lat: Double,
    val lng: Double
)

private data class Booking(val hostId: String, val status: BookingStatus)
private data class Message(val senderId: String, val senderName: String, val content: String)

private object Colors {
    val background = Color.rgb(16, 20, 24)
    val surface = Color.rgb(24, 32, 39)
    val primary = Color.rgb(33, 195, 111)
    val text = Color.rgb(229, 236, 232)
    val muted = Color.rgb(151, 164, 157)
    val line = Color.rgb(54, 67, 63)
    val warning = Color.rgb(179, 101, 36)
}
