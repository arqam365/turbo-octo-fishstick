package com.nextlevelprogrammers.elearn.ui.design

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.nextlevelprogrammers.elearn.R
import com.nextlevelprogrammers.elearn.Routes
import com.nextlevelprogrammers.elearn.viewmodel.CategoryViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 🔹 Top Bar with Profile and Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileBadge(imageUrl = "https://via.placeholder.com/150")

                Row {
                    IconButton(onClick = { /* TODO: Handle Search Click */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* TODO: Handle Menu Click */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Title
            Text(text = "Let's learn", fontSize = 24.sp)
            Text(text = "something new", fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Learning Categories
            LearningCategories(navController = navController)

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 User Library Section (Purchased Courses)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Library",
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "View All",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { /* TODO: Navigate to full library */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

// Example: Purchased Course Card
            PurchasedCourseCard(
                imageUrl = "https://via.placeholder.com/150",
                courseTitle = "Mastering Android Development",
                instructor = "John Doe"
            )
        }
    }
}

// 🔹 Profile Image Badge
@Composable
fun ProfileBadge(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
    )
}

// 🔹 Learning Categories Section
@Composable
fun LearningCategories(navController: NavController, viewModel: CategoryViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()

    Column {
        for (i in categories.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                i.forEachIndexed { index, category ->
                    CategoryCard(
                        title = category.category_name,
                        categoryId = category.category_id,
                        color = getRandomCategoryColor(),
                        modifier = Modifier.weight(1f),
                        navController = navController // ✅ Pass the navigation controller
                    )

                    // 🔹 Add spacing except for the last item
                    if (index < i.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

fun getRandomCategoryColor(): Color {
    val colors = listOf(
        Color(0xFFE87C5B), // Orange
        Color(0xFF98A6FA), // Light Blue
        Color(0xFF2E358B), // Dark Blue
        Color(0xFFF6C179), // Yellow
        Color(0xFF8BC34A), // Green
        Color(0xFF009688)  // Teal
    )
    return colors[Random.nextInt(colors.size)]
}

// 🔹 Single Category Card
@Composable
fun CategoryCard(title: String, categoryId: String, color: Color, modifier: Modifier, navController: NavController) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                navController.navigate("subject_screen/$categoryId/$userId")
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color.White, fontSize = 16.sp)
    }
}

// 🔹 Purchased Course Card
@Composable
fun PurchasedCourseCard(
    imageUrl: String,
    courseTitle: String,
    instructor: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { /* TODO: Open Course Details */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Course Image
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Course Info
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = courseTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = instructor, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

// 🔹 Bottom Navigation Bar
@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { /* TODO: Navigate to Home */ },
            icon = { Icon(painterResource(id = R.drawable.ic_home), contentDescription = "Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to Notes */ },
            icon = { Icon(painterResource(id = R.drawable.ic_notes), contentDescription = "Notes") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.CART_SCREEN) },
            icon = { Icon(painterResource(id = R.drawable.ic_cart), contentDescription = "Cart") },
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(Routes.PROFILE_SCREEN)
            },
            icon = { Icon(painterResource(id = R.drawable.ic_profile), contentDescription = "Profile") }
        )
    }
}