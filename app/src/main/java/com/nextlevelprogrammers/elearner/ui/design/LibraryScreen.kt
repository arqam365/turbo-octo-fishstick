package com.nextlevelprogrammers.elearner.ui.design

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextlevelprogrammers.elearner.model.Course
import com.nextlevelprogrammers.elearner.viewmodel.LibraryViewModel
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    uid: String,
    navController: NavHostController,
    onCourseClick: (Course) -> Unit
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val purchasedCourses by viewModel.purchasedCourses.collectAsState()

    LaunchedEffect(uid) {
        viewModel.loadPurchasedCourses(uid)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Courses") })
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
        }
    ) { innerPadding ->
        if (purchasedCourses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("You haven't purchased any course yet")
                }
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(purchasedCourses.size) { index ->
                    val course = purchasedCourses[index]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCourseClick(course) }
                    ) {
                        PurchasedCourseCard(
                            imageUrl = course.head_img,
                            courseTitle = course.course_name
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}