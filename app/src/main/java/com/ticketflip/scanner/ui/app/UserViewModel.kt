package com.ticketflip.scanner.ui.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hva.amsix.util.SessionManager
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.data.model.response.UserResponse
import com.ticketflip.scanner.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * View model for User
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application)
    private val sessionManager = SessionManager(application)


    /**
     * Live data for user
     */
    val userResource: LiveData<Resource<UserResponse>>
        get() = _userResource

    private val _userResource: MutableLiveData<Resource<UserResponse>> =
        MutableLiveData(Resource.Empty())

    init {
        viewModelScope.launch {
            getUser()
        }
    }

    fun getUser() {
        _userResource.value = Resource.Loading()

        viewModelScope.launch {
            val result = userRepository.getUser()

            _userResource.value = result
        }
    }


    fun setToken(token: String) {
        sessionManager.saveAuthToken(token)
        getUser()
    }

    fun clearToken() {
        sessionManager.clearAuthToken()
    }


}