package com.nextlevelprogrammers.elearn

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nextlevelprogrammers.elearn.data.remote.ApiService
import com.nextlevelprogrammers.elearn.data.repository.ContentRepository
import com.nextlevelprogrammers.elearn.data.repository.SectionRepository
import com.nextlevelprogrammers.elearn.model.AuthRequest
import com.nextlevelprogrammers.elearn.model.ContentItem
import com.nextlevelprogrammers.elearn.ui.design.CartScreen
import com.nextlevelprogrammers.elearn.ui.design.ContentScreen
import com.nextlevelprogrammers.elearn.ui.design.GetStartedScreen
import com.nextlevelprogrammers.elearn.ui.design.MainScreen
import com.nextlevelprogrammers.elearn.ui.design.PdfViewerScreen
import com.nextlevelprogrammers.elearn.ui.design.SectionScreen
import com.nextlevelprogrammers.elearn.ui.design.SubjectScreen
import com.nextlevelprogrammers.elearn.ui.design.VideoPlayerScreen
import com.nextlevelprogrammers.elearn.ui.theme.ELearnTheme
import com.nextlevelprogrammers.elearn.viewmodel.ContentViewModel
import com.nextlevelprogrammers.elearn.viewmodel.SectionViewModel
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
                        com.nextlevelprogrammers.elearn.ui.design.MainScreenProfile.Screen(
                            onSignOutClick = {
                                signOut(navController)
                            }
                        )
                    }
//                    composable(Routes.LIBRARY_SCREEN) {
//                        val apiService = remember { ApiService() }
//                        val repository = remember { CourseRepository(apiService) }
//                        val viewModel = remember { CourseViewModel(repository) }
//
//                        val purchasedCourses by viewModel.purchasedCourses.collectAsState()
//
//                        // Fetch purchased courses when screen opens
//                        LaunchedEffect(Unit) {
//                            viewModel.getPurchasedCourses()
//                        }
//
//                        if (purchasedCourses.isEmpty()) {
//                            CircularProgressIndicator()
//                        } else {
//                            LibraryScreen(
//                                purchasedCourses = purchasedCourses,
//                                onCourseClick = { selectedCourse ->
//                                    navController.navigate("sectionScreen/${selectedCourse.id}")
//                                }
//                            )
//                        }
//                    }
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
                popUpTo(Routes.MAIN_SCREEN) { inclusive = true }
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