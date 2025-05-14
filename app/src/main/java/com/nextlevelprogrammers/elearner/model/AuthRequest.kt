package com.nextlevelprogrammers.elearner.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val uid: String
)