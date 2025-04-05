package com.nextlevelprogrammers.elearn.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val uid: String
)