package com.nextlevelprogrammers.elearn.data.repository

import com.nextlevelprogrammers.elearn.data.remote.ApiService
import com.nextlevelprogrammers.elearn.model.SectionDetail

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