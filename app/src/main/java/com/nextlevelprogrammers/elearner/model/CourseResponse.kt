package com.nextlevelprogrammers.elearner.model

import kotlinx.serialization.Serializable

@Serializable // ✅ Add this annotation
data class CourseResponse(
    val data: List<Course>
)

@Serializable // ✅ Add this annotation
data class Course(
    val course_id: String,
    val course_name: String,
    val course_description: String,
    val course_price: Int,
    val category_id: String,
    val razorpay_item_id: String,
    val head_img: String,
    val head_img_bucket_uri: String,
    val allow_free_access: Boolean,
    val can_be_purchased: Boolean,
    val is_published: Boolean,
    val expires_at: String?,
    val createdAt: String,
    val updatedAt: String,
    val purchased: Boolean = false
)

@Serializable
data class CreateOrderRequest(
    val user_id: String
)

@Serializable
data class CreateOrderResponse(
    val success: Boolean,
    val orderId: String,
    val message: String? = null
)