package com.nextlevelprogrammers.elearner.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nextlevelprogrammers.elearner.Routes
import com.nextlevelprogrammers.elearner.data.remote.ApiService
import com.nextlevelprogrammers.elearner.model.AuthRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val context: Context, private val clientId: String) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    /** 🔥 Check if User is Already Authenticated */
    fun checkAuthentication() {
        _isAuthenticated.value = auth.currentUser != null
    }

    /** 🔥 Sign Out */
    fun signOut(navController: NavHostController) {
        auth.signOut()
        _isAuthenticated.value = false
        navController.navigate(Routes.GET_STARTED) {
            popUpTo(Routes.HOME_SCREEN) { inclusive = true }
        }
    }

    /** 🔥 Sign in with Google using Credential Manager API */
    fun signInWithGoogle(activity: Activity, navController: NavHostController) {
        val credentialManager = CredentialManager.create(activity.applicationContext) // ✅ Fix: Use applicationContext

        val googleIdTokenRequest = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdTokenRequest)
            .build()

        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(activity, request)
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

                    // Send UID to Backend
                    sendUIDToBackend(
                        firebaseUid = firebaseUid,
                        onSuccess = {
                            navController.navigate(Routes.HOME_SCREEN) {
                                popUpTo(Routes.GET_STARTED) { inclusive = true }
                            }
                        },
                        onError = { Log.e(TAG, "❌ Backend authentication failed.") }
                    )
                } else {
                    Log.e(TAG, "❌ Firebase authentication failed: ${task.exception?.localizedMessage}")
                }
            }
    }

    /** 🔥 Send UID to Backend */
    fun sendUIDToBackend(firebaseUid: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val authRequest = AuthRequest(uid = firebaseUid)
                val response = ApiService().authenticateUser(authRequest)

                if (response.success) {
                    _isAuthenticated.value = true
                    withContext(Dispatchers.Main) { onSuccess() }
                } else {
                    withContext(Dispatchers.Main) { onError(response.message ?: "Authentication failed") }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.localizedMessage ?: "Unknown error") }
            }
        }
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}