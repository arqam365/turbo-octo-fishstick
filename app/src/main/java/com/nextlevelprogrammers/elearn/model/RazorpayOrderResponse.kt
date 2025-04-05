package com.nextlevelprogrammers.elearn.model

import kotlinx.serialization.Serializable

@Serializable
data class RazorpayOrderResponse(
    val order_id: String,
    val user_id: String,
    val course_id: String,
    val amount: Int,
    val status: String,
    val expiration_time: String? = null,
    val createdAt: String,
    val updatedAt: String
)