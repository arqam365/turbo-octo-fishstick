package com.nextlevelprogrammers.elearns.model

import kotlinx.serialization.Serializable

@Serializable
data class ContentResponse(
    val message: String,
    val data: SectionDetail
)

@Serializable
data class SectionDetail(
    val section_id: String,
    val course_id: String,
    val section_name: String,
    val section_description: String,
    val section_index: Int,
    val createdAt: String,
    val updatedAt: String,
    val contents: List<ContentItem> = emptyList()
)

@Serializable
data class ContentItem(
    val content_id: String,
    val section_id: String,
    val content_name: String,
    val content_description: String,
    val content_index: Int,
    val content_type: String,
    val pdf_uri: String? = null,
    val pdf_gs_bucket_uri: String? = null,
    val full_hd_video_uri: String? = null,
    val full_hd_video_gs_bucket_uri: String? = null,
    val hd_video_uri: String? = null,
    val hd_video_gs_bucket_uri: String? = null,
    val sd_video_uri: String? = null,
    val sd_video_gs_bucket_uri: String? = null,
    val is_published: Boolean? = null,
    val createdAt: String,
    val updatedAt: String
)