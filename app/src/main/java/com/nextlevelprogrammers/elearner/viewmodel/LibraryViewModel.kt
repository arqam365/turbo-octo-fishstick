package com.nextlevelprogrammers.elearner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextlevelprogrammers.elearner.model.Course
import com.nextlevelprogrammers.elearner.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _purchasedCourses = MutableStateFlow<List<Course>>(emptyList())
    val purchasedCourses: StateFlow<List<Course>> = _purchasedCourses

    fun loadPurchasedCourses(uid: String) {
        viewModelScope.launch {
            try {
                val orders = repository.getPurchasedCourses(uid)
                val courses = orders.map { it.course }
                _purchasedCourses.value = courses
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}