package com.nextlevelprogrammers.elearns.ui.design

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.nextlevelprogrammers.elearns.R
import com.nextlevelprogrammers.elearns.viewmodel.CategoryViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
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

            // 🔹 Top Mentors Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Top Mentors", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(
                    text = "Show All",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { /* TODO: Show all mentors */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Mentor Card
            MentorCard(
                imageUrl = "https://via.placeholder.com/150",
                name = "Kali Mona",
                title = "Leading UI/UX Expert"
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
                navController.navigate("subject_screen/$categoryId")
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color.White, fontSize = 16.sp)
    }
}

// 🔹 Mentor Card
@Composable
fun MentorCard(imageUrl: String, name: String, title: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Mentor Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = name, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(text = title, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(Icons.Default.Person, contentDescription = "Profile Arrow", tint = Color.Gray)
        }
    }
}

// 🔹 Bottom Navigation Bar
@Composable
fun BottomNavigationBar() {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { /* TODO: Navigate to Home */ },
            icon = { Icon(painterResource(id = R.drawable.ic_home), contentDescription = "Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to Bookmarks */ },
            icon = { Icon(painterResource(id = R.drawable.ic_bookmark), contentDescription = "Bookmarks") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to Notes */ },
            icon = { Icon(painterResource(id = R.drawable.ic_notes), contentDescription = "Notes") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to Profile */ },
            icon = { Icon(painterResource(id = R.drawable.ic_profile), contentDescription = "Profile") }
        )
    }
}