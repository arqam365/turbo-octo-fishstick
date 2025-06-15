package com.nextlevelprogrammers.elearner.ui.design

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Us") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SectionTitle("About Us – CHEMISTRY POINT NK")

            Section("Who We Are", listOf(
                "CHEMISTRY POINT NK is a premier educational institute focused on academic and competitive excellence.",
                "We provide complete subject coaching for Classes 9 to 12.",
                "Over 3000+ students have already trusted us in their academic journey."
            ))

            Section("Our Journey", listOf(
                "2010 – Started as a passionate initiative to guide students in science and academics.",
                "2020 – Established as a full-fledged institute: CHEMISTRY POINT NK.",
                "Proud to have helped 500+ students successfully pursue their passion through our programs."
            ))

            Section("What We Offer", listOf(
                "Classes 9 to 12 – Full syllabus coaching (CBSE & State Boards).",
                "Subjects Offered: Physics, Chemistry, Biology, Mathematics, English, Social Science & more.",
                "Competitive Exams:",
                "• JEE (Main + Advanced)",
                "• NEET (UG)",
                "• NTSE, Olympiads, and foundation courses for younger students."
            ))

            Section("Our Teaching Approach", listOf(
                "Smart digital boards in all classrooms.",
                "Live online classes with real-time interaction.",
                "Doubt-clearing sessions in every class.",
                "Visualized learning with animations and concept illustrations.",
                "Regular assignments, quizzes, and test series.",
                "Performance tracking with detailed progress analytics.",
                "Focused improvement strategies for each student."
            ))

            Section("Support System", listOf(
                "24x7 Doubt Resolution – Submit queries anytime.",
                "One-on-One Mentorship – Personalized academic guidance.",
                "Parent Connect – Monthly progress reports and performance discussions."
            ))

            Section("Access & Learning Tools", listOf(
                "Fully supported via our mobile app.",
                "Class recordings available for revision.",
                "PDF notes and printed material shared regularly for all subjects."
            ))

            Section("Our Achievements", listOf(
                "3000+ students trained and mentored.",
                "500+ students successfully pursued their dreams and passions through our institute.",
                "Consistent track record of board exam toppers and high-performing students."
            ))

            Text(
                text = "“To guide, inspire, and prepare every student to confidently achieve academic success and life goals.”",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    )
    Divider(thickness = 1.dp)
}

@Composable
private fun Section(header: String, items: List<String>) {
    Text(
        text = header,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
    items.forEach { item ->
        Text(
            text = "• $item",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
    }
}