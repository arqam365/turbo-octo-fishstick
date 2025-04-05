package com.nextlevelprogrammers.elearn.data.repository

import android.util.Log
import com.nextlevelprogrammers.elearn.data.remote.ApiService
import com.nextlevelprogrammers.elearn.model.CourseResponse
import com.nextlevelprogrammers.elearn.model.CreateOrderRequest
import com.nextlevelprogrammers.elearn.model.RazorpayOrderResponse

class CourseRepository(private val apiService: ApiService) {

    suspend fun getCourses(categoryId: String): CourseResponse {
        return try {
            apiService.getCourses(categoryId)
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching courses: ${e.localizedMessage}")
            CourseResponse(emptyList())
        }
    }

    suspend fun getCategoryName(categoryId: String): String {
        return try {
            val response = apiService.getCategories()
            val category = response.data.firstOrNull { it.category_id == categoryId }
            category?.category_name ?: "Unknown Category"
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching category name: ${e.localizedMessage}")
            "Unknown Category"
        }
    }

    // ✅ NEW: Fetch Purchased Courses for LibraryScreen
    suspend fun getPurchasedCourseIds(userId: String): List<String> {
        return try {
            val orders = apiService.getPurchasedCourses(userId)
            orders.filter { it.status == "paid" }
                .map { it.course_id }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching purchased courses: ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun createCourseOrder(courseId: String, userId: String): RazorpayOrderResponse? {
        return try {
            val response = apiService.createCourseOrder(courseId, CreateOrderRequest(userId))
            response.data
        } catch (e: Exception) {
            Log.e("CourseRepository", "Error creating course order: ${e.localizedMessage}")
            null
        }
    }
}