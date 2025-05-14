package com.nextlevelprogrammers.elearner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextlevelprogrammers.elearner.data.repository.SectionRepository
import com.nextlevelprogrammers.elearner.model.CourseSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SectionViewModel(private val repository: SectionRepository) : ViewModel() {

    private val _sections = MutableStateFlow<List<CourseSection>>(emptyList())
    val sections: StateFlow<List<CourseSection>> = _sections

    fun getSections(courseId: String) {
        viewModelScope.launch {
            val sectionCourse = repository.getSections(courseId)
            if (sectionCourse != null) {
                _sections.value = sectionCourse.course_sections
            } else {
                // Handle Error: Show error message
            }
        }
    }
}