package com.nextlevelprogrammers.elearner.ui.design

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TncScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Terms and Conditions") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("⸻\n\nTerms and Conditions – CHEMISTRY POINT NK App\n\nEffective Date: [Insert Date]\n\nBy accessing or using the CHEMISTRY POINT NK mobile application (the “App”), you agree to the following Terms and Conditions:\n\n⸻", style = MaterialTheme.typography.titleMedium)

            Section("1. User Agreement", listOf(
                "By registering on the App, you confirm that all the information provided by you is accurate and complete.",
                "You agree to abide by all rules, regulations, and instructions provided by CHEMISTRY POINT NK."
            ))

            Section("2. Intellectual Property", listOf(
                "All content (videos, notes, PDFs, test papers, images, graphics, and audio) is the sole property of CHEMISTRY POINT NK.",
                "You are strictly prohibited from:",
                "• Sharing, distributing, or copying any content outside the App.",
                "• Uploading our content to any third-party platform.",
                "• Using the content for commercial or non-personal educational purposes."
            ))

            Section("3. Payment and Refund Policy", listOf(
                "All payments made for courses, classes, subscriptions, or materials are non-refundable under any circumstances.",
                "Once enrolled, students are responsible for managing their own schedules and progress.",
                "No partial or full refunds will be entertained due to missed classes or personal issues."
            ))

            Section("4. Account Access", listOf(
                "Your account is meant for personal, individual use only.",
                "Sharing login credentials with others or accessing the App through unauthorized means is strictly prohibited.",
                "CHEMISTRY POINT NK reserves the right to suspend or terminate access without notice if misuse is detected."
            ))

            Section("5. Conduct and Use", listOf(
                "Students must maintain respectful behavior during live sessions, doubt discussions, and in-app chats.",
                "Any abusive, misleading, or disruptive behavior can lead to permanent suspension from the platform."
            ))

            Section("6. Changes to Content and Features", listOf(
                "CHEMISTRY POINT NK reserves the right to modify, add, or remove any features, content, or services without prior notice.",
                "Regular updates may be provided to improve app functionality and security."
            ))

            Section("7. Data Privacy", listOf(
                "All personal data (name, contact, academic details) will be stored securely.",
                "We do not sell or share your data with any third-party entity.",
                "Usage analytics may be collected to enhance user experience and performance."
            ))

            Section("8. Limitation of Liability", listOf(
                "CHEMISTRY POINT NK is not responsible for any technical failures, app downtime, or loss of data due to device-related issues.",
                "While we strive to offer accurate and updated content, we do not guarantee specific outcomes or exam results."
            ))

            Section("9. Termination of Service", listOf(
                "If you violate any of the terms listed here, your access to the App may be terminated immediately without a refund."
            ))

            Section("10. Jurisdiction", listOf(
                "These terms are governed by the laws of India.",
                "Any disputes will be subject to the jurisdiction of courts located in Lucknow, Uttar Pradesh."
            ))
        }
    }
}

@Composable
fun Section(title: String, bulletPoints: List<String>) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = title, style = MaterialTheme.typography.titleSmall)
    Spacer(modifier = Modifier.height(4.dp))
    bulletPoints.forEach { line ->
        Text(text = if (line.startsWith("•")) line else "• $line", style = MaterialTheme.typography.bodyMedium)
    }
}