package com.nextlevelprogrammers.elearner.model

import kotlinx.serialization.Serializable

@Serializable
data class PurchasedCoursesResponse(
    val userData: UserData
)

@Serializable
data class UserData(
    val firebase_uid: String,
    val is_admin: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val orders: List<Order>
)

@Serializable
data class Order(
    val order_id: String,
    val user_id: String,
    val course_id: String,
    val amount: Int,
    val status: String,
    val expiration_time: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val course: Course
)