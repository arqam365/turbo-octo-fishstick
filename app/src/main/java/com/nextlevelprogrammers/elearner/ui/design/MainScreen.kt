package com.nextlevelprogrammers.elearner.ui.design

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.nextlevelprogrammers.elearner.R
import com.nextlevelprogrammers.elearner.Routes
import com.nextlevelprogrammers.elearner.data.remote.ApiService
import com.nextlevelprogrammers.elearner.data.repository.CourseRepository
import com.nextlevelprogrammers.elearner.viewmodel.CategoryViewModel
import com.nextlevelprogrammers.elearner.viewmodel.CourseViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val apiService = remember { ApiService() }
    val repository = remember { CourseRepository(apiService) }

    val courseViewModel: CourseViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CourseViewModel(repository) as T
            }
        }
    )
    val purchasedCourses by courseViewModel.purchasedCourses.collectAsState()

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val currentUser = FirebaseAuth.getInstance().currentUser
    val profilePhotoUrl = currentUser?.photoUrl?.toString()
    val displayName = currentUser?.displayName ?: "Learner"

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        userId?.let {
            courseViewModel.getPurchasedCourses(it)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .background(MaterialTheme.colorScheme.surface),
                tonalElevation = 4.dp
            ) {
                DrawerContent(navController)
            }
        }
    ) {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController, currentRoute) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProfileBadge(imageUrl = profilePhotoUrl.toString())
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Light,
                                letterSpacing = 0.15.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Row {
                        IconButton(onClick = { /* TODO: Handle Search Click */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = {
                            coroutineScope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Let's learn", fontSize = 24.sp)
                Text(text = "something new", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                LearningCategories(navController = navController)
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Library",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View All",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { navController.navigate(Routes.LIBRARY_SCREEN) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                ) {
                    purchasedCourses.forEach { course ->
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

@Composable
fun DrawerContent(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        Column {
            Text("Menu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Home", modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Routes.HOME_SCREEN) }
                .padding(vertical = 8.dp))
            Text("Library", modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Routes.LIBRARY_SCREEN) }
                .padding(vertical = 8.dp))
            Text("Profile", modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Routes.PROFILE_SCREEN) }
                .padding(vertical = 8.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Text("About Us", modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Routes.ABOUT_US_SCREEN) }
                .padding(vertical = 8.dp))
            Text("Contact Us", modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Routes.CONTACT_US_SCREEN) }
                .padding(vertical = 8.dp))
            Text("Terms & Conditions", modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Routes.TNC_SCREEN) }
                .padding(vertical = 8.dp))
        }
        Button(
            onClick = {
                auth.signOut()
                navController.navigate(Routes.GET_STARTED) {
                    popUpTo(0)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Log Out")
        }
    }
}

@Composable
fun ProfileBadge(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(48.dp)
            .padding(4.dp)
            .clip(CircleShape)
    )
}

// 🔹 Learning Categories Section
@Composable
fun LearningCategories(navController: NavController, viewModel: CategoryViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()
    val cardColors = listOf(Color(0xFF81C784), Color(0xFF64B5F6), Color(0xFFFFB74D), Color(0xFFBA68C8), Color(0xFFFF8A65))
    var colorIndex=0
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
                        modifier = Modifier.weight(1f),
                        navController = navController,
                        color=cardColors[colorIndex++]
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
        Color(0xFF81C784),
        Color(0xFF64B5F6),
        Color(0xFFFFB74D),
        Color(0xFFBA68C8),
        Color(0xFFFF8A65)
    )
    return colors[Random.nextInt(colors.size)]
}

// 🔹 Single Category Card
@Composable
fun CategoryCard(
    title: String,
    categoryId: String,
    modifier: Modifier,
    navController: NavController,
    color: Color
) {
    val backgroundColor = remember { getRandomCategoryColor() }

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
    courseTitle: String
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
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = courseTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// 🔹 Bottom Navigation Bar
@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Routes.HOME_SCREEN,
            onClick = {
                if (currentRoute != Routes.HOME_SCREEN) {
                    navController.navigate(Routes.HOME_SCREEN) {
                        popUpTo(Routes.HOME_SCREEN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(painterResource(id = R.drawable.ic_home), contentDescription = "Home") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.LIBRARY_SCREEN,
            onClick = {
                if (currentRoute != Routes.LIBRARY_SCREEN) {
                    navController.navigate(Routes.LIBRARY_SCREEN) {
                        popUpTo(Routes.HOME_SCREEN)
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(painterResource(id = R.drawable.ic_notes), contentDescription = "Library") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.CART_SCREEN,
            onClick = {
                if (currentRoute != Routes.CART_SCREEN) {
                    navController.navigate(Routes.CART_SCREEN) {
                        popUpTo(Routes.HOME_SCREEN)
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(painterResource(id = R.drawable.ic_cart), contentDescription = "Cart") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.PROFILE_SCREEN,
            onClick = {
                if (currentRoute != Routes.PROFILE_SCREEN) {
                    navController.navigate(Routes.PROFILE_SCREEN) {
                        popUpTo(Routes.HOME_SCREEN)
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(painterResource(id = R.drawable.ic_profile), contentDescription = "Profile") }
        )
    }
}