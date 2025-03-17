package com.nextlevelprogrammers.elearns.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextlevelprogrammers.elearns.data.repository.ContentRepository
import com.nextlevelprogrammers.elearns.model.ContentItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContentViewModel(private val repository: ContentRepository) : ViewModel() {

    private val _contents = MutableStateFlow<List<ContentItem>>(emptyList())
    val contents: StateFlow<List<ContentItem>> = _contents

    fun getSectionDetail(courseId: String, sectionId: String) {
        viewModelScope.launch {
            val sectionDetail = repository.getSectionDetail(courseId, sectionId)
            sectionDetail?.let {
                _contents.value = it.contents
            }
        }
    }
}