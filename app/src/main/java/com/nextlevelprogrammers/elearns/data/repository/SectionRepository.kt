package com.nextlevelprogrammers.elearns.data.repository

import com.nextlevelprogrammers.elearns.data.remote.ApiService
import com.nextlevelprogrammers.elearns.model.SectionResponse
import com.nextlevelprogrammers.elearns.model.SectionCourse

class SectionRepository(private val apiService: ApiService) {

    suspend fun getSections(courseId: String): SectionCourse? {
        return try {
            val response = apiService.getSections(courseId)
            response?.data
        } catch (e: Exception) {
            null
        }
    }
}