package com.nextlevelprogrammers.elearns.model

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val course_id: String
)

@Serializable
data class Category(
    val category_id: String,
    val category_name: String,
    val category_description: String,
    val is_active: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val courses: List<Course>
)

@Serializable
data class CategoryResponse(
    val data: List<Category>
)