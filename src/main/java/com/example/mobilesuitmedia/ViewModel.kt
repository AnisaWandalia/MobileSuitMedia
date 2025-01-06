package com.example.mobilesuitmedia

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val apiService = ApiClient.apiService

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var currentPage by mutableStateOf(1)
        private set

    var totalPages by mutableStateOf(1)
        private set

    var isEmptyState by mutableStateOf(false)
        private set

    init {
        fetchUsers()
    }

    fun fetchUsers(isRefresh: Boolean = false) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getUsers(page = currentPage, perPage = 6)
                if (isRefresh) {
                    users = response.data
                } else {
                    users = users + response.data
                }
                totalPages = response.total_pages
                isEmptyState = users.isEmpty()
            } catch (e: Exception) {
                isEmptyState = users.isEmpty()
            } finally {
                isLoading = false
                isRefreshing = false
            }
        }
    }

    fun refreshUsers() {
        isRefreshing = true
        currentPage = 1
        fetchUsers(isRefresh = true)
    }

    fun loadNextPage() {
        if (currentPage < totalPages && !isLoading) {
            currentPage += 1
            fetchUsers()
        }
    }
}
