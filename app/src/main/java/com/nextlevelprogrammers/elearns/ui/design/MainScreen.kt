package com.nextlevelprogrammers.elearns.ui.design

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.nextlevelprogrammers.elearns.Routes

@Composable
fun MainScreen(navController: NavHostController) {
    val auth = remember { FirebaseAuth.getInstance() } // ✅ Get Firebase Auth instance
    val isAuthenticated = remember { mutableStateOf(auth.currentUser != null) } // ✅ Track auth state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isAuthenticated.value) "Welcome back!" else "Please sign in.",
            style = MaterialTheme.typography.headlineMedium
        ) // ✅ Fix

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                auth.signOut()
                isAuthenticated.value = false // ✅ Update state
                navController.navigate(Routes.GET_STARTED) {
                    popUpTo(Routes.MAIN_SCREEN) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Logout")
        }
    }
}