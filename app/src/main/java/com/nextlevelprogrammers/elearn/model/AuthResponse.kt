package com.nextlevelprogrammers.elearn.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val success: Boolean = false,
    val message: String? = null,
    val uid: String? = null
)