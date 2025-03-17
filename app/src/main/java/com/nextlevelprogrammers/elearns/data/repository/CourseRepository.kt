package com.nextlevelprogrammers.elearns.data.repository

import android.util.Log
import com.nextlevelprogrammers.elearns.data.remote.ApiService
import com.nextlevelprogrammers.elearns.model.CourseResponse

class CourseRepository(private val apiService: ApiService) {

    suspend fun getCourses(categoryId: String): CourseResponse {
        return try {
            apiService.getCourses(categoryId) // ✅ Now correctly calls API service
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching courses: ${e.localizedMessage}")
            CourseResponse(emptyList()) // ✅ Return empty list on failure
        }
    }

    suspend fun getCategoryName(categoryId: String): String {
        return try {
            val response = apiService.getCategories() // ✅ Get all categories
            val category = response.data.firstOrNull { it.category_id == categoryId }
            category?.category_name ?: "Unknown Category" // ✅ Extract category name safely
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching category name: ${e.localizedMessage}")
            "Unknown Category"
        }
    }
}