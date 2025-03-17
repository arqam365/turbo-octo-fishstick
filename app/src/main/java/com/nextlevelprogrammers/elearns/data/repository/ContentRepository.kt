package com.nextlevelprogrammers.elearns.data.repository

import com.nextlevelprogrammers.elearns.data.remote.ApiService
import com.nextlevelprogrammers.elearns.model.SectionDetail

class ContentRepository(private val apiService: ApiService) {

    suspend fun getSectionDetail(courseId: String, sectionId: String): SectionDetail? {
        return try {
            val response = apiService.getContent(courseId, sectionId)
            response?.data
        } catch (e: Exception) {
            null // Handle error gracefully
        }
    }
}