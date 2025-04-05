package com.nextlevelprogrammers.elearn.model

import kotlinx.serialization.Serializable

@Serializable
data class Courses(
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
    val courses: List<Courses>
)

@Serializable
data class CategoryResponse(
    val data: List<Category>
)