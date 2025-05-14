package com.nextlevelprogrammers.elearner.data.repository

import com.nextlevelprogrammers.elearner.data.remote.ApiService
import com.nextlevelprogrammers.elearner.model.SectionCourse

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