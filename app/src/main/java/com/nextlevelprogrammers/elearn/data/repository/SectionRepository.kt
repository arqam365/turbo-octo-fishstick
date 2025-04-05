package com.nextlevelprogrammers.elearn.data.repository

import com.nextlevelprogrammers.elearn.data.remote.ApiService
import com.nextlevelprogrammers.elearn.model.SectionCourse

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