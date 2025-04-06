package com.nextlevelprogrammers.elearn.ui.design

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nextlevelprogrammers.elearn.MainActivity
import com.nextlevelprogrammers.elearn.data.remote.ApiService
import com.nextlevelprogrammers.elearn.data.repository.CourseRepository
import com.nextlevelprogrammers.elearn.viewmodel.SubjectViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(navController: NavController, categoryId: String, userId: String) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val apiService = remember { ApiService() }
    val repository = remember { CourseRepository(apiService) }
    val viewModel = remember { SubjectViewModel(repository) }

    val courses by viewModel.courses.collectAsState()
    val categoryName by viewModel.categoryName.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var selectedCourse by remember { mutableStateOf<com.nextlevelprogrammers.elearn.model.Course?>(null) }

    LaunchedEffect(categoryId, userId) {
        viewModel.getCourses(categoryId, userId)
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
                CourseList(
                    courses,
                    onCourseClick = { course ->
                        navController.navigate("sectionScreen/${course.course_id}")
                    },
                    onBuyClick = { course ->
                        selectedCourse = course
                    }
                )

                selectedCourse?.let { course ->
                    AlertDialog(
                        onDismissRequest = { selectedCourse = null },
                        title = { Text("Purchase Course") },
                        text = { Text("To access \"${course.course_name}\", please purchase it.") },
                        confirmButton = {
                            TextButton(onClick = {
                                coroutineScope.launch {
                                    val response = repository.createCourseOrder(course.course_id, userId)

                                    if (response != null) {
                                        // 🔥 Call Razorpay SDK here
                                        activity?.startRazorpayPayment(
                                            orderId = response.order_id,
                                            amount = response.amount,
                                            currency = "INR",
                                            onPaymentSuccess = {
                                                // ✅ Re-fetch courses after successful payment
                                                coroutineScope.launch {
                                                    viewModel.getCourses(categoryId, userId)
                                                }
                                            }
                                        )
                                    } else {
                                        Log.e("ORDER_FAILED", "Order creation failed")
                                    }

                                    selectedCourse = null
                                }
                            }) {
                                Text("Buy Now")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { selectedCourse = null }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseList(
    courses: List<com.nextlevelprogrammers.elearn.model.Course>,
    onCourseClick: (com.nextlevelprogrammers.elearn.model.Course) -> Unit,
    onBuyClick: (com.nextlevelprogrammers.elearn.model.Course) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(courses) { course ->
            CourseItem(course, onCourseClick, onBuyClick)
        }
    }
}

@Composable
fun CourseItem(
    course: com.nextlevelprogrammers.elearn.model.Course,
    onCourseClick: (com.nextlevelprogrammers.elearn.model.Course) -> Unit,
    onBuyClick: (com.nextlevelprogrammers.elearn.model.Course) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        onClick = {
            if (course.purchased) {
                onCourseClick(course)
            } else {
                onBuyClick(course)
            }
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.course_name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = course.course_description, fontSize = 14.sp, color = Color.Gray)
            if (!course.purchased) {
                Text(
                    text = "Buy to access",
                    fontSize = 12.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}