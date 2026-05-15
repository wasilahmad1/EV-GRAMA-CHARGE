package com.evgrama.charge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evgrama.charge.ui.theme.DeepDarkGrey
import com.evgrama.charge.ui.theme.ElectricBlue
import com.evgrama.charge.ui.theme.MutedGrey
import com.evgrama.charge.ui.theme.SurfaceGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepDarkGrey)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        Text(
            text = "Welcome Back",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email or Phone Number", color = MutedGrey) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = SurfaceGrey,
                focusedLabelColor = ElectricBlue,
                cursorColor = ElectricBlue
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = MutedGrey) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = SurfaceGrey,
                focusedLabelColor = ElectricBlue,
                cursorColor = ElectricBlue
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { /* TODO */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?", color = ElectricBlue)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Divider(modifier = Modifier.weight(1f), color = SurfaceGrey)
            Text(" OR ", color = MutedGrey, modifier = Modifier.padding(horizontal = 16.dp))
            Divider(modifier = Modifier.weight(1f), color = SurfaceGrey)
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SurfaceGrey)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Placeholder for Google Icon
                Text("G", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Sign in with Google", color = Color.White)
            }
        }
    }
}
