package com.nextlevelprogrammers.elearns.ui.design

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SubjectScreen(navController: NavController, categoryName: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Subjects for $categoryName", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        val subjects = when (categoryName) {
            "9th" -> listOf("Mathematics", "Science", "English", "Social Studies")
            "Science" -> listOf("Physics", "Chemistry", "Biology")
            "Math" -> listOf("Algebra", "Geometry", "Calculus")
            else -> listOf("General Subject 1", "General Subject 2")
        }

        subjects.forEach { subject ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = { /* TODO: Navigate to subject details if needed */ }
            ) {
                Text(
                    text = subject,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}