package com.nextlevelprogrammers.elearn.ui.design

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun GetStartedScreen(
    navController: NavHostController,
    onGoogleSignInClick: () -> Unit // ✅ Pass function from MainActivity
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Welcome to E-Learn! Get Started", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onGoogleSignInClick() }, // ✅ Call the function from MainActivity
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign in with Google")
        }
    }
}