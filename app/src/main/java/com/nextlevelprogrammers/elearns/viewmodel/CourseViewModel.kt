package com.nextlevelprogrammers.elearns.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextlevelprogrammers.elearns.data.repository.CourseRepository
import com.nextlevelprogrammers.elearns.model.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    fun fetchCourses(categoryId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getCourses(categoryId) // ✅ returns List<Course>
                _courses.value = response.data
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // ✅ Get single course by ID
    fun getCourseById(courseId: String): Course? {
        return _courses.value.find { it.course_id == courseId }
    }
}