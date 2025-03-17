package com.nextlevelprogrammers.elearns.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextlevelprogrammers.elearns.data.repository.CourseRepository
import com.nextlevelprogrammers.elearns.model.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubjectViewModel(private val repository: CourseRepository) : ViewModel() {
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val _categoryName = MutableStateFlow("Loading...")
    val categoryName: StateFlow<String> = _categoryName

    fun getCourses(categoryId: String) {
        viewModelScope.launch {
            _categoryName.value = repository.getCategoryName(categoryId) // ✅ Fetch Category Name
            _courses.value = getCoursesFromApi(categoryId)
        }
    }

    private suspend fun getCoursesFromApi(categoryId: String): List<Course> {
        return try {
            val response = repository.getCourses(categoryId)
            response.data
        } catch (e: Exception) {
            Log.e("API", "Error fetching courses: ${e.localizedMessage}")
            emptyList()
        }
    }
}