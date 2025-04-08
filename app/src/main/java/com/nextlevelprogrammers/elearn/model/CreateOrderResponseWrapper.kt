package com.nextlevelprogrammers.elearn.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderResponseWrapper(
    val message: String,
    val order: RazorpayOrderResponse,
    val status: String
)