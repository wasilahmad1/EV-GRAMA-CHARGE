package com.evgrama.charge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evgrama.charge.ui.theme.DeepDarkGrey
import com.evgrama.charge.ui.theme.ElectricBlue
import com.evgrama.charge.ui.theme.MutedGrey
import com.evgrama.charge.ui.theme.SurfaceGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostSetupScreen(onSetupComplete: () -> Unit) {
    var socketType by remember { mutableStateOf("15A") }
    var address by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepDarkGrey)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Set Up Your Charging Point",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Socket Type Dropdown
        Text("Socket Type", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SurfaceGrey),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(socketType)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(SurfaceGrey)
            ) {
                DropdownMenuItem(
                    text = { Text("5A", color = Color.White) },
                    onClick = { socketType = "5A"; expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("15A", color = Color.White) },
                    onClick = { socketType = "15A"; expanded = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Address
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Exact Address", color = MutedGrey) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = SurfaceGrey,
                focusedLabelColor = ElectricBlue
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Price
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price Per Hour (₹)", color = MutedGrey) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = SurfaceGrey,
                focusedLabelColor = ElectricBlue
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description", color = MutedGrey) },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = SurfaceGrey,
                focusedLabelColor = ElectricBlue
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Upload Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(SurfaceGrey, RoundedCornerShape(12.dp))
                .border(1.dp, ElectricBlue, RoundedCornerShape(12.dp)), // Dashed border not natively easy in Compose without Canvas
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📷", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Upload Socket Image", color = ElectricBlue, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onSetupComplete,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Complete Setup", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
