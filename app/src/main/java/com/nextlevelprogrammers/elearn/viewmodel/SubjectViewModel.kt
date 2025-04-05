package com.nextlevelprogrammers.elearn.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextlevelprogrammers.elearn.data.repository.CourseRepository
import com.nextlevelprogrammers.elearn.model.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubjectViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val _categoryName = MutableStateFlow("Loading...")
    val categoryName: StateFlow<String> = _categoryName

    fun getCourses(categoryId: String, userId: String) {
        viewModelScope.launch {
            val courseResponse = repository.getCourses(categoryId)
            val paidOrders = repository.getPurchasedCourseIds(userId) // ✅ Changed

            val updatedCourses = courseResponse.data.map { course ->
                val isPurchased = paidOrders.contains(course.course_id)
                course.copy(purchased = isPurchased)
            }

            _courses.value = updatedCourses
            _categoryName.value = repository.getCategoryName(categoryId)
        }
    }
}