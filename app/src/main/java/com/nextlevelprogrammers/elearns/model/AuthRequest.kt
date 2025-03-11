package com.nextlevelprogrammers.elearns.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val uid: String
)