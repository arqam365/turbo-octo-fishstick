package com.nextlevelprogrammers.elearner.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.nextlevelprogrammers.elearner.R
import com.nextlevelprogrammers.elearner.Routes

class MainScreenProfile {
    companion object {
        @Composable
        fun Screen(modifier: Modifier = Modifier, onSignOutClick: () -> Unit, navController: NavHostController) {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                val navBackStackEntry = navController.currentBackStackEntryAsState().value
                val currentRoute = navBackStackEntry?.destination?.route ?: ""

                val currentUser = FirebaseAuth.getInstance().currentUser?.let {
                    UserData(
                        uid = it.uid,
                        displayName = it.displayName,
                        email = it.email,
                        photoUrl = it.photoUrl?.toString(),
                        phoneNumber = it.phoneNumber
                    )
                }
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController, currentRoute = currentRoute) }
                ) { paddingValues ->
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ProfileCard(onSignOutClick = onSignOutClick, user = currentUser)
                    }
                }
            }
        }
    }

    data class UserData(
        val uid: String,
        val displayName: String?,
        val email: String?,
        val photoUrl: String?,
        val phoneNumber: String?
    )
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    onSignOutClick: () -> Unit,
    user: MainScreenProfile.UserData?,
) {
    val userName = user?.displayName ?: "Unknown User"
    val userEmail = user?.email ?: "No email"
    val userPhoto = user?.photoUrl
    val userPhone = user?.phoneNumber ?: "No phone number"

    Column(modifier = modifier.fillMaxSize()) {
        // Top Profile Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(MaterialTheme.colorScheme.primary)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = userPhoto,
                    contentDescription = "User Photo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(userName, color = MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(userEmail,color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Email info only if phone/email exists
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("Account Overview", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))

            ProfileOption(
                icon = Icons.Default.ShoppingCart,
                label = "My Orders",
                bgColor = Color(0xFFD0F8CE)
            )

            Button(
                onClick = onSignOutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Sign Out", fontSize = 16.sp)
            }
        }
    }
}