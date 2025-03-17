package com.nextlevelprogrammers.elearns.model

import kotlinx.serialization.Serializable

@Serializable
data class SectionResponse(
    val data: SectionCourse
)

@Serializable
data class SectionCourse(
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
    val category: SectionCategory,
    val course_sections: List<CourseSection> = emptyList()
)

@Serializable
data class SectionCategory(
    val category_id: String,
    val category_name: String,
    val category_description: String,
    val is_active: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CourseSection(
    val section_id: String,
    val course_id: String,
    val section_name: String,
    val section_description: String,
    val section_index: Int,
    val createdAt: String,
    val updatedAt: String
)