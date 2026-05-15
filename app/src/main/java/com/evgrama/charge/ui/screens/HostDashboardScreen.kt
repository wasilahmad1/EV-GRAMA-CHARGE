package com.evgrama.charge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evgrama.charge.ui.theme.*

@Composable
fun HostDashboardScreen() {
    var isActive by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepDarkGrey)
            .padding(24.dp)
    ) {
        // Status Toggle
        StatusToggle(isActive = isActive, onToggle = { isActive = it })

        Spacer(modifier = Modifier.height(32.dp))

        // Earnings Card
        EarningsSummaryCard()

        Spacer(modifier = Modifier.height(32.dp))

        Text("Pending Requests", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(pendingRequests) { request ->
                RequestCard(request)
            }
        }
    }
}

@Composable
fun StatusToggle(isActive: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        onClick = { onToggle(!isActive) },
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) ElectricGreen else SurfaceGrey
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                if (isActive) "Accepting EVs" else "Currently Offline",
                color = if (isActive) Color.Black else Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Switch(
                checked = isActive,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Black.copy(alpha = 0.3f),
                    uncheckedThumbColor = MutedGrey,
                    uncheckedTrackColor = DeepDarkGrey
                )
            )
        }
    }
}

@Composable
fun EarningsSummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Earnings Summary", color = MutedGrey, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("₹850", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Earned", color = MutedGrey, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("12", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Sessions", color = MutedGrey, fontSize = 14.sp)
                }
            }
        }
    }
}

data class ChargeRequest(val name: String, val eta: String, val avatar: String)
val pendingRequests = listOf(
    ChargeRequest("Rahul Nair", "15 mins", "👤"),
    ChargeRequest("Anjali S.", "45 mins", "👤")
)

@Composable
fun RequestCard(request: ChargeRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(DeepDarkGrey),
                contentAlignment = Alignment.Center
            ) {
                Text(request.avatar, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(request.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("ETA: ${request.eta}", color = MutedGrey, fontSize = 14.sp)
            }
            Row {
                IconButton(onClick = { /* TODO */ }, modifier = Modifier.background(ElectricGreen.copy(alpha = 0.1f), CircleShape)) {
                    Text("✅", color = ElectricGreen)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { /* TODO */ }, modifier = Modifier.background(ErrorRed.copy(alpha = 0.1f), CircleShape)) {
                    Text("❌", color = ErrorRed)
                }
            }
        }
    }
}
