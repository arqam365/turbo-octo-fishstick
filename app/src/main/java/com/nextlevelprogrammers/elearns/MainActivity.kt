package com.nextlevelprogrammers.elearns

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.*
import androidx.navigation.compose.composable
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nextlevelprogrammers.elearns.data.remote.ApiService
import com.nextlevelprogrammers.elearns.data.repository.ContentRepository
import com.nextlevelprogrammers.elearns.data.repository.CourseRepository
import com.nextlevelprogrammers.elearns.data.repository.SectionRepository
import com.nextlevelprogrammers.elearns.model.AuthRequest
import com.nextlevelprogrammers.elearns.model.CourseSection
import com.nextlevelprogrammers.elearns.ui.design.ContentScreen
import com.nextlevelprogrammers.elearns.ui.design.GetStartedScreen
import com.nextlevelprogrammers.elearns.ui.design.MainScreen
import com.nextlevelprogrammers.elearns.ui.design.PdfViewerScreen
import com.nextlevelprogrammers.elearns.ui.design.SectionScreen
import com.nextlevelprogrammers.elearns.ui.design.SubjectScreen
import com.nextlevelprogrammers.elearns.ui.design.VideoPlayerScreen
import com.nextlevelprogrammers.elearns.ui.theme.ELearnTheme
import com.nextlevelprogrammers.elearns.viewmodel.ContentViewModel
import com.nextlevelprogrammers.elearns.viewmodel.CourseViewModel
import com.nextlevelprogrammers.elearns.viewmodel.SectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)
        val clientId = getString(R.string.client_id)

        setContent {
            ELearnTheme {
                val navController = rememberNavController()

                // 🔥 Make isAuthenticated reactive
                var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

                // 🔥 Ensure real-time auth state changes trigger navigation
                LaunchedEffect(auth) {
                    isAuthenticated = auth.currentUser != null
                }

                val startDestination = if (isAuthenticated) {
                    Routes.MAIN_SCREEN
                } else {
                    Routes.GET_STARTED
                }

                androidx.navigation.compose.NavHost(navController, startDestination = startDestination) {
                    composable(Routes.GET_STARTED) {
                        GetStartedScreen(
                            navController = navController,
                            onGoogleSignInClick = { signInWithGoogle(navController, clientId) }
                        )
                    }
                    composable(Routes.MAIN_SCREEN) {
                        MainScreen(navController = navController)
                    }
                    composable("subject_screen/{categoryName}") { backStackEntry ->
                        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Unknown"
                        SubjectScreen(navController, categoryName)
                    }
                    composable("sectionScreen/{courseId}") { backStackEntry ->
                        val courseId = backStackEntry.arguments?.getString("courseId") ?: ""

                        val apiService = remember { ApiService() }
                        val repository = remember { SectionRepository(apiService) }
                        val viewModel = remember { SectionViewModel(repository) }

                        val sections by viewModel.sections.collectAsState()

                        LaunchedEffect(Unit) {
                            viewModel.getSections(courseId)
                        }

                        if (sections.isEmpty()) {
                            CircularProgressIndicator()
                        } else {
                            SectionScreen(navController, sections)
                        }
                    }
                    composable("contentScreen/{courseId}/{sectionId}") { backStackEntry ->
                        val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                        val sectionId = backStackEntry.arguments?.getString("sectionId") ?: ""

                        val apiService = remember { ApiService() }
                        val repository = remember { ContentRepository(apiService) }
                        val viewModel = remember { ContentViewModel(repository) }

                        val contents by viewModel.contents.collectAsState()

                        LaunchedEffect(Unit) {
                            viewModel.getSectionDetail(courseId, sectionId)
                        }

                        if (contents.isEmpty()) {
                            CircularProgressIndicator()
                        } else {
                            ContentScreen(navController, contents)
                        }
                    }
                    composable("videoPlayerScreen") { backStackEntry ->
                        val fullHdUrl = navController.previousBackStackEntry?.savedStateHandle?.get<String>("fullHdUrl") ?: ""
                        val hdUrl = navController.previousBackStackEntry?.savedStateHandle?.get<String>("hdUrl") ?: ""
                        val sdUrl = navController.previousBackStackEntry?.savedStateHandle?.get<String>("sdUrl") ?: ""

                        Log.d("VideoPlayerScreen", "Received FullHD=$fullHdUrl HD=$hdUrl SD=$sdUrl")

                        VideoPlayerScreen(
                            videoQualities = mapOf(
                                "Full HD" to fullHdUrl,
                                "HD" to hdUrl,
                                "SD" to sdUrl
                            ),
                            nextVideos = List {  }
                        )
                    }
                    composable("pdfViewerScreen/{pdfUrl}") { backStackEntry ->
                        val pdfUrl = backStackEntry.arguments?.getString("pdfUrl") ?: ""
                        PdfViewerScreen(pdfUrl)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun signInWithGoogle(navController: NavHostController, clientId: String) {
        val googleIdTokenRequest = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdTokenRequest)
            .build()

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(this@MainActivity, request)
                handleSignInResult(result, navController)
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Google Sign-In failed: ${e.localizedMessage}")
            }
        }
    }

    /** 🔥 Handle Sign-In Result */
    private fun handleSignInResult(result: GetCredentialResponse, navController: NavHostController) {
        val credential = result.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdToken.idToken, navController)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }

    /** 🔥 Authenticate with Firebase using Google ID Token */
    private fun firebaseAuthWithGoogle(idToken: String, navController: NavHostController) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val firebaseUid = user?.uid ?: return@addOnCompleteListener
                    Log.d(TAG, "✅ Firebase UID: $firebaseUid")

                    sendUIDToBackend(firebaseUid, {
                        lifecycleScope.launch(Dispatchers.Main) {
                            navController.navigate(Routes.MAIN_SCREEN) {
                                popUpTo(Routes.GET_STARTED) { inclusive = true }
                            }
                        }
                    }, {
                        Log.e(TAG, "❌ Backend authentication failed.")
                    }, navController)
                } else {
                    Log.e(TAG, "❌ Firebase authentication failed: ${task.exception?.localizedMessage}")
                }
            }
    }

    /** 🔥 Send UID to Backend */
    private fun sendUIDToBackend(
        firebaseUid: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        navController: NavHostController
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val authRequest = AuthRequest(uid = firebaseUid)

                // Log the request being sent
                Log.d("API_REQUEST", "Sending UID: $firebaseUid")

                val response = ApiService().authenticateUser(authRequest)

                if (response.success || response.message?.contains("successfully", ignoreCase = true) == true || response.message?.contains("209") == true) {
                    Log.d(TAG, "✅ User authenticated successfully or already exists!")

                    withContext(Dispatchers.Main) {
                        navController.navigate(Routes.MAIN_SCREEN) {
                            popUpTo(Routes.GET_STARTED) { inclusive = true }
                        }
                    }
                } else {
                    Log.e(TAG, "❌ Authentication failed: ${response.message}")
                    withContext(Dispatchers.Main) {
                        onError(response.message ?: "Authentication failed")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API_ERROR", "Exception: ${e.localizedMessage ?: "Unknown error"}")
                    onError(e.localizedMessage ?: "Unknown error")
                }
            }
        }
    }

    /** 🔥 Sign Out */
    private fun signOut(navController: NavHostController) {
        auth.signOut()
        lifecycleScope.launch(Dispatchers.Main) {
            navController.navigate(Routes.GET_STARTED) {
                popUpTo(Routes.MAIN_SCREEN) { inclusive = true }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}