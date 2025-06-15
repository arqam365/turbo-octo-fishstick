package com.nextlevelprogrammers.elearner

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nextlevelprogrammers.elearner.data.remote.ApiService
import com.nextlevelprogrammers.elearner.data.repository.ContentRepository
import com.nextlevelprogrammers.elearner.data.repository.CourseRepository
import com.nextlevelprogrammers.elearner.data.repository.SectionRepository
import com.nextlevelprogrammers.elearner.model.AuthRequest
import com.nextlevelprogrammers.elearner.model.ContentItem
import com.nextlevelprogrammers.elearner.ui.design.AboutUsScreen
import com.nextlevelprogrammers.elearner.ui.design.CartScreen
import com.nextlevelprogrammers.elearner.ui.design.ContactUsScreen
import com.nextlevelprogrammers.elearner.ui.design.ContentScreen
import com.nextlevelprogrammers.elearner.ui.design.GetStartedScreen
import com.nextlevelprogrammers.elearner.ui.design.LibraryScreen
import com.nextlevelprogrammers.elearner.ui.design.MainScreen
import com.nextlevelprogrammers.elearner.ui.design.PdfViewerScreen
import com.nextlevelprogrammers.elearner.ui.design.SectionScreen
import com.nextlevelprogrammers.elearner.ui.design.SubjectScreen
import com.nextlevelprogrammers.elearner.ui.design.VideoPlayerScreen
import com.nextlevelprogrammers.elearner.ui.design.YouTubeLivePlayer
import com.nextlevelprogrammers.elearner.ui.theme.ELearnTheme
import com.nextlevelprogrammers.elearner.viewmodel.ContentViewModel
import com.nextlevelprogrammers.elearner.viewmodel.CourseViewModel
import com.nextlevelprogrammers.elearner.viewmodel.LibraryViewModel
import com.nextlevelprogrammers.elearner.viewmodel.SectionViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : ComponentActivity(), PaymentResultListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    var onRazorpaySuccess: (() -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)
        val clientId = getString(R.string.client_id)
        val razorpayKey = getString(R.string.razorpay_api_key)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )


        setContent {
            ELearnTheme {

                val view = LocalView.current
                val window = (view.context as Activity).window
                val isDarkTheme = isSystemInDarkTheme()
                val colorScheme = MaterialTheme.colorScheme

                LaunchedEffect(isDarkTheme) {
                    window.statusBarColor = colorScheme.background.toArgb()
                    WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = !isDarkTheme
                }
                val navController = rememberNavController()

                // 🔥 Make isAuthenticated reactive
                var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

                // 🔥 Ensure real-time auth state changes trigger navigation
                LaunchedEffect(auth) {
                    isAuthenticated = auth.currentUser != null
                }

                val startDestination = if (isAuthenticated) {
                    Routes.HOME_SCREEN
                } else {
                    Routes.GET_STARTED
                }

                androidx.navigation.compose.NavHost(navController, startDestination = startDestination) {
                    composable(Routes.GET_STARTED) {
                        GetStartedScreen(
                            navController = navController,
                            onGoogleSignInClick = { signInWithGoogle(navController, clientId) },
                            onDemoSignInClick = { email, password -> signInWithDemo(email, password, navController) },
                        )
                    }
                    composable(Routes.HOME_SCREEN) {
                        MainScreen(navController = navController)
                    }
                    composable(Routes.CART_SCREEN) {
                        CartScreen(navController)
                    }
                    composable("subject_screen/{categoryName}/{userId}") { backStackEntry ->
                        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Unknown"
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        SubjectScreen(navController, categoryName, userId)
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

                        val nextVideos = navController.previousBackStackEntry
                            ?.savedStateHandle?.get<List<ContentItem>>("nextVideos") ?: emptyList()

                        Log.d("VideoPlayerScreen", "Received FullHD=$fullHdUrl HD=$hdUrl SD=$sdUrl")

                        VideoPlayerScreen(
                            videoQualities = mapOf(
                                "Full HD" to fullHdUrl,
                                "HD" to hdUrl,
                                "SD" to sdUrl
                            ),
                            nextVideos = nextVideos.map { it.content_name }, // or title
                            onVideoSelected = { selectedVideoTitle ->
                                val selectedContent = nextVideos.find { it.content_name == selectedVideoTitle }
                                selectedContent?.let {
                                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                                        set("fullHdUrl", it.full_hd_video_uri ?: "")
                                        set("hdUrl", it.hd_video_uri ?: "")
                                        set("sdUrl", it.sd_video_uri ?: "")
                                        set("nextVideos", nextVideos) // pass again for continuity
                                    }
                                    navController.navigate("videoPlayerScreen")
                                }
                            }
                        )
                    }
                    composable("pdfViewerScreen/{pdfUrl}") { backStackEntry ->
                        val pdfUrl = backStackEntry.arguments?.getString("pdfUrl") ?: ""
                        PdfViewerScreen(pdfUrl)
                    }
                    composable(Routes.PROFILE_SCREEN) {
                        com.nextlevelprogrammers.elearner.ui.design.MainScreenProfile.Screen(
                            navController = navController,
                            onSignOutClick = {
                                signOut(navController)
                            }
                        )
                    }
                    composable(Routes.LIBRARY_SCREEN) {
                        val apiService = remember { ApiService() }
                        val repository = remember { CourseRepository(apiService) }
                        val viewModel = remember { LibraryViewModel(repository) }

                        val uid = FirebaseAuth.getInstance().currentUser?.uid

                        if (uid != null) {
                            LibraryScreen(
                                viewModel = viewModel,
                                uid = uid,
                                navController = navController,
                                onCourseClick = { selectedCourse ->
                                    navController.navigate("sectionScreen/${selectedCourse.course_id}")
                                }
                            )
                        } else {
                            // Show fallback if not logged in
                        }
                    }


                    //Navigation Route For Composable Screen
                    //Also don't write initialization codes in nav graph it causes data leaks.

                    composable(
                        route = Routes.YOUTUBE_PLAYER + "?videoId={videoId}",
                        arguments = listOf(navArgument("videoId") { defaultValue = "" })
                    ) { backStackEntry ->
                        val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                        YouTubeLivePlayer(videoId = videoId, navController = navController)
                    }
                    //Make Sure to Check For the nullability and empty string before calling navController

                    composable(Routes.ABOUT_US_SCREEN) {
                        AboutUsScreen()
                    }

                    composable(Routes.CONTACT_US_SCREEN) {
                        ContactUsScreen()
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
                            navController.navigate(Routes.HOME_SCREEN) {
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

    private fun signInWithDemo(email: String, password: String, navController: NavHostController) {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = it.user
                val firebaseUid = user?.uid ?: return@addOnSuccessListener
                Log.d("DemoLogin", "✅ Logged in with demo: UID = $firebaseUid")

                sendUIDToBackend(firebaseUid, {
                    navController.navigate(Routes.HOME_SCREEN) {
                        popUpTo(Routes.GET_STARTED) { inclusive = true }
                    }
                }, {
                    Log.e("DemoLogin", "❌ Backend auth failed for demo login")
                }, navController)
            }
            .addOnFailureListener {
                val errorMsg = it.localizedMessage ?: "Unknown error"
                Toast.makeText(this, "Demo login failed: $errorMsg", Toast.LENGTH_SHORT).show()
                Log.e("DemoLogin", "❌ Firebase demo login failed: $errorMsg")
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
                        navController.navigate(Routes.HOME_SCREEN) {
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

    fun startRazorpayPayment(
        orderId: String,
        amount: Int,
        currency: String,
        onPaymentSuccess: () -> Unit
    ) {
        val checkout = Checkout()
        val key = getString(R.string.razorpay_api_key)
        checkout.setKeyID(key)

        val options = JSONObject().apply {
            put("name", "E-Learn")
            put("description", "Course Purchase")
            put("order_id", orderId)
            put("currency", currency)
            put("amount", amount) // amount in paisa
            put("prefill", JSONObject().apply {
                put("email", FirebaseAuth.getInstance().currentUser?.email ?: "")
            })
        }

        try {
            checkout.open(this, options)
        } catch (e: Exception) {
            Log.e("Razorpay", "Error starting Razorpay", e)
        }

        this.onRazorpaySuccess = onPaymentSuccess
    }

    /** 🔥 Sign Out */
    private fun signOut(navController: NavHostController) {
        auth.signOut()
        lifecycleScope.launch(Dispatchers.Main) {
            navController.navigate(Routes.GET_STARTED) {
                popUpTo(Routes.HOME_SCREEN) { inclusive = true }
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Log.d("Razorpay", "✅ Payment Success: $razorpayPaymentId")
        onRazorpaySuccess?.invoke()
        onRazorpaySuccess = null
    }
    override fun onPaymentError(code: Int, response: String?) {
        try {
            Log.e("Razorpay", "❌ Payment Error [$code]: $response")

            val description = JSONObject(response ?: "{}")
                .optJSONObject("error")
                ?.optString("description", "Payment failed. Please try again.")

            Toast.makeText(this, description, Toast.LENGTH_LONG).show()

            // Optionally show retry or navigate user away
        } catch (e: Exception) {
            Log.e("Razorpay", "Exception in onPaymentError: ${e.localizedMessage}")
            Toast.makeText(this, "Payment failed due to an unexpected error.", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}