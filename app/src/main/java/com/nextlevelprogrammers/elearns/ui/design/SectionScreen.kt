package com.nextlevelprogrammers.elearns.ui.design

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nextlevelprogrammers.elearns.model.CourseSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionScreen(navController: NavController, sections: List<CourseSection>) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Course Section") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SectionList(sections) { section ->
                navController.navigate("contentScreen/${section.course_id}/${section.section_id}")
            }
        }
    }
}

@Composable
fun SectionList(sections: List<CourseSection>, onSectionClick: (CourseSection) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sections) { section ->
            SectionItem(section, onSectionClick)
        }
    }
}

@Composable
fun SectionItem(section: CourseSection, onSectionClick: (CourseSection) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        onClick = { onSectionClick(section) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = section.section_name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = section.section_description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}