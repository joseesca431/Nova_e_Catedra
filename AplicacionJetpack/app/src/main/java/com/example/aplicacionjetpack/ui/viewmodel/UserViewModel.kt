package com.example.aplicacionjetpack.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.dto.UserResponse
import com.example.aplicacionjetpack.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserUiState(
    val user: UserResponse? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(UserUiState())
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = AuthManager.userId
        if (userId == null) {
            uiState = uiState.copy(isLoading = false, error = "No se pudo encontrar el ID de usuario.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val result = userRepository.getUserProfile(userId)
            result.onSuccess { user ->
                uiState = uiState.copy(isLoading = false, user = user)
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = "No se pudo cargar el perfil.")
            }
        }
    }
}
