package com.nextlevelprogrammers.elearner.ui.design

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen() {
    val context = LocalContext.current
    val phoneNumbers = listOf("+91 8924078524", "+91 9336366995", "+91 9235553719")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Contact Us") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("For contact us, reach us at:", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            phoneNumbers.forEach { number ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = "tel:$number".toUri()
                            }
                            context.startActivity(intent)
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = number, fontSize = 16.sp)
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call")
                }
                Divider()
            }
        }
    }
}