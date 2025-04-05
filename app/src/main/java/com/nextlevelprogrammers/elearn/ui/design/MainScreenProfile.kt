package com.nextlevelprogrammers.elearn.ui.design

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth

class MainScreenProfile {
    companion object {
        @Composable
        fun Screen(modifier: Modifier = Modifier, onSignOutClick: () -> Unit) {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val currentUser = FirebaseAuth.getInstance().currentUser?.let {
                    UserData(
                        uid = it.uid,
                        displayName = it.displayName,
                        email = it.email,
                        photoUrl = it.photoUrl?.toString(),
                        phoneNumber = it.phoneNumber
                    )
                }
                ProfileCard(onSignOutClick = onSignOutClick, user = currentUser)
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
fun ProfileCard(
    modifier: Modifier = Modifier,
    onSignOutClick: () -> Unit,
    user: MainScreenProfile.UserData?,
) {
    val userName = user?.displayName ?: "User Name"
    val userEmail = user?.email ?: "Email not available"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp),
            modifier = modifier.padding(20.dp, 32.dp)
        ) {
            Log.d("UserRow", "Rendering Row: user_name = $userName, user_profile_picture = ${user?.photoUrl}")

            AsyncImage(
                model = user?.photoUrl,
                contentDescription = "UserProfilePicture",
                modifier = modifier.size(120.dp).clip(CircleShape)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = userName,
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = userEmail,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = onSignOutClick,
                modifier = modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Sign Out", fontSize = 20.sp)
            }
        }
    }
}