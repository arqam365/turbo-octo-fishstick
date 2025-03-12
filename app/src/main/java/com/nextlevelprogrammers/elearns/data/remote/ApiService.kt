package com.nextlevelprogrammers.elearns.data.remote

import com.nextlevelprogrammers.elearns.model.AuthRequest
import com.nextlevelprogrammers.elearns.model.AuthResponse
import com.nextlevelprogrammers.elearns.model.CategoryResponse
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

    private val BASE_URL = "https://orchid-dev-2102724573.asia-south1.run.app"

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

            if (response.status == HttpStatusCode.OK) {
                response.body<CategoryResponse>()
            } else {
                CategoryResponse(emptyList()) // Return empty list in case of failure
            }
        } catch (e: Exception) {
            CategoryResponse(emptyList()) // Handle error gracefully
        }
    }
}