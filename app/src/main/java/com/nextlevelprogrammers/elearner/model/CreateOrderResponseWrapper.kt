package com.nextlevelprogrammers.elearner.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderResponseWrapper(
    val message: String,
    val data: RazorpayOrderResponse? = null,
    val order: RazorpayOrderResponse? = null,
    val status: String? = null
) {
    fun getValidOrder(): RazorpayOrderResponse? {
        return order ?: data
    }
}