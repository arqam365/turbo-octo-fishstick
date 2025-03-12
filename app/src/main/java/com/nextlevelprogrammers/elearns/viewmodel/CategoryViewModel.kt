package com.nextlevelprogrammers.elearns.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextlevelprogrammers.elearns.data.remote.ApiService
import com.nextlevelprogrammers.elearns.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val apiService = ApiService()
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            val response = apiService.getCategories(showInactive = false)
            _categories.value = response.data
        }
    }
}