package com.nextlevelprogrammers.elearns.ui.design

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nextlevelprogrammers.elearns.data.remote.ApiService
import com.nextlevelprogrammers.elearns.data.repository.CourseRepository
import com.nextlevelprogrammers.elearns.viewmodel.SubjectViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(navController: NavController, categoryId: String) {
    val apiService = remember { ApiService() }
    val repository = remember { CourseRepository(apiService) }
    val viewModel = remember { SubjectViewModel(repository) }

    val courses by viewModel.courses.collectAsState()
    val categoryName by viewModel.categoryName.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(categoryId) {
        viewModel.getCourses(categoryId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(categoryName) }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (courses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                CourseList(courses) { course ->
                    navController.navigate("sectionScreen/${course.course_id}")
                }
            }
        }
    }
}

@Composable
fun CourseList(courses: List<com.nextlevelprogrammers.elearns.model.Course>, onCourseClick: (com.nextlevelprogrammers.elearns.model.Course) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(courses) { course ->
            CourseItem(course, onCourseClick)
        }
    }
}

@Composable
fun CourseItem(course: com.nextlevelprogrammers.elearns.model.Course, onCourseClick: (com.nextlevelprogrammers.elearns.model.Course) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        onClick = { onCourseClick(course) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.course_name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = course.course_description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}