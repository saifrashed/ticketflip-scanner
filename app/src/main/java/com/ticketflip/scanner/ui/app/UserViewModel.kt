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

    /**
     *  data for token
     */
    var token: String = ""
        private set



    fun getUser(token: String) {
        _userResource.value = Resource.Loading()

        viewModelScope.launch {
            val result = userRepository.getUser(token)

            setToken(token)

            _userResource.value = result
        }
    }


    fun setToken(token: String) {
        this.token = token
    }



}