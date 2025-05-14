package com.nextlevelprogrammers.elearner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextlevelprogrammers.elearner.data.repository.CourseRepository
import com.nextlevelprogrammers.elearner.model.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val _purchasedCourses = MutableStateFlow<List<Course>>(emptyList())
    val purchasedCourses: StateFlow<List<Course>> = _purchasedCourses

    fun fetchCourses(categoryId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getCourses(categoryId)
                val purchasedIds = repository.getPurchasedCourseIds(userId)

                // ✅ Mark purchased field in each course
                val updatedCourses = response.data.map {
                    it.copy(purchased = it.course_id in purchasedIds)
                }

                _courses.value = updatedCourses
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // ✅ Get single course by ID
    fun getCourseById(courseId: String): Course? {
        return _courses.value.find { it.course_id == courseId }
    }

    fun getPurchasedCourses(userId: String) {
        viewModelScope.launch {
            try {
                val orders = repository.getPurchasedCourses(userId) // must return List<Order>
                val courseList = orders.map { it.course } // extracting Course from each Order
                _purchasedCourses.value = courseList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}