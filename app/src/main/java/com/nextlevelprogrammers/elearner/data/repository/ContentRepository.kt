package com.nextlevelprogrammers.elearner.data.repository

import com.nextlevelprogrammers.elearner.data.remote.ApiService
import com.nextlevelprogrammers.elearner.model.SectionDetail

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