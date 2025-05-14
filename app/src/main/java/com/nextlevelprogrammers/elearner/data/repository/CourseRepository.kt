package com.nextlevelprogrammers.elearner.data.repository

import android.util.Log
import com.nextlevelprogrammers.elearner.data.remote.ApiService
import com.nextlevelprogrammers.elearner.model.CourseResponse
import com.nextlevelprogrammers.elearner.model.CreateOrderRequest
import com.nextlevelprogrammers.elearner.model.CreateOrderResponseWrapper
import com.nextlevelprogrammers.elearner.model.Order
import com.nextlevelprogrammers.elearner.model.RazorpayOrderResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

class CourseRepository(private val apiService: ApiService) {

    suspend fun getRawResponse(response: HttpResponse): String {
        return response.bodyAsText()
    }

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
        val request = CreateOrderRequest(userId)
        Log.d("DEBUG_ORDER", "Sending create order request for courseId: $courseId with payload: $request")
        return try {
            // Directly receive the parsed response instead of HttpResponse
            val orderResponse: CreateOrderResponseWrapper = apiService.createCourseOrder(courseId, request)
            Log.d("DEBUG_ORDER", "Received order response: $orderResponse")

            val order = orderResponse.getValidOrder()
            if (order == null) {
                Log.e("DEBUG_ORDER", "Order creation failed: ${orderResponse.message}")
                return null
            }
            return order
        } catch (e: Exception) {
            Log.e("DEBUG_ORDER", "Error creating course order: ${e.localizedMessage}")
            null
        }
    }

    suspend fun getPurchasedCourses(userId: String): List<Order> {
        return apiService.getPurchasedCourses(userId)
    }
}