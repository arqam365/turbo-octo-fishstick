package com.nextlevelprogrammers.elearn.data.remote

import android.util.Log
import com.nextlevelprogrammers.elearn.model.AuthRequest
import com.nextlevelprogrammers.elearn.model.AuthResponse
import com.nextlevelprogrammers.elearn.model.CategoryResponse
import com.nextlevelprogrammers.elearn.model.ContentResponse
import com.nextlevelprogrammers.elearn.model.CourseResponse
import com.nextlevelprogrammers.elearn.model.CreateOrderRequest
import com.nextlevelprogrammers.elearn.model.CreateOrderResponseWrapper
import com.nextlevelprogrammers.elearn.model.Order
import com.nextlevelprogrammers.elearn.model.PurchasedCoursesResponse
import com.nextlevelprogrammers.elearn.model.RazorpayOrderResponse
import com.nextlevelprogrammers.elearn.model.SectionResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiService {

    private val BASE_URL = "https://development-begoniaorchid-977741295366.asia-south1.run.app"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun authenticateUser(authRequest: AuthRequest): AuthResponse {
        return try {
            val response: HttpResponse = client.post("$BASE_URL/v1/user/onboard") {
                contentType(ContentType.Application.Json)
                setBody(authRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<AuthResponse>()
            } else {
                AuthResponse(success = false, message = "Failed to authenticate: ${response.status}")
            }
        } catch (e: Exception) {
            AuthResponse(success = false, message = e.localizedMessage ?: "Unknown error")
        }
    }

    suspend fun getCategories(showInactive: Boolean = false): CategoryResponse {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/v1/categories") {
                url {
                    parameters.append("show_inactive", showInactive.toString())
                }
            }

            Log.d("API", "Response Status: ${response.status}")

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<CategoryResponse>()
                Log.d("API", "Response Body: $responseBody")
                responseBody
            } else {
                Log.w("API", "API request failed with status: ${response.status}")
                CategoryResponse(emptyList()) // Return empty list in case of failure
            }
        } catch (e: Exception) {
            Log.e("API", "API request failed with exception: ${e.localizedMessage}")
            CategoryResponse(emptyList()) // Handle error gracefully
        }
    }

    suspend fun getCourses(categoryId: String): CourseResponse {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/v1/categories/$categoryId/courses")

            Log.d("API", "Response Status: ${response.status}")

            if (response.status == HttpStatusCode.OK) {
                val responseBody: CourseResponse = response.body()
                Log.d("API", "Response Body: $responseBody")
                responseBody // ✅ Return CourseResponse
            } else {
                Log.w("API", "API request failed with status: ${response.status}")
                CourseResponse(emptyList()) // ✅ Return empty list in case of failure
            }
        } catch (e: Exception) {
            Log.e("API", "API request failed with exception: ${e.localizedMessage}")
            CourseResponse(emptyList()) // ✅ Handle errors gracefully
        }
    }

    suspend fun getSections(courseId: String): SectionResponse? {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/v1/course/$courseId") {
                url {
                    parameters.append("allow_unpublished", "true")
                    parameters.append("allow_expired", "true")
                }
            }

            Log.d("API", "GetSections - Response Status: ${response.status}")

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<SectionResponse>()
                Log.d("API", "GetSections - Response Body: $responseBody")
                responseBody
            } else {
                Log.w("API", "GetSections - API request failed with status: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("API", "GetSections - API request failed: ${e.localizedMessage}")
            null
        }
    }

    suspend fun getContent(courseId: String, sectionId: String): ContentResponse? {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/v1/course/$courseId/section/$sectionId")

            Log.d("API", "Response Status: ${response.status}")

            if (response.status == HttpStatusCode.OK) {
                val responseBody: ContentResponse = response.body()
                Log.d("API", "Content Response: $responseBody")
                responseBody
            } else {
                Log.w("API", "Failed to fetch content with status: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("API", "API request failed: ${e.localizedMessage}")
            null
        }
    }

    suspend fun createCourseOrder(courseId: String, request: CreateOrderRequest): CreateOrderResponseWrapper {
        val response: HttpResponse = client.post("$BASE_URL/v1/order/course/$courseId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val raw = response.bodyAsText()
        Log.d("RAW_ORDER_RESPONSE", raw)
        return response.body()
    }

    suspend fun getPurchasedCourses(userId: String): List<Order> {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/v1/user/$userId/library/courses")
            if (response.status == HttpStatusCode.OK) {
                val parsed = response.body<PurchasedCoursesResponse>()
                parsed.userData.orders
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("API", "getPurchasedCourses error: ${e.localizedMessage}")
            emptyList()
        }
    }
}