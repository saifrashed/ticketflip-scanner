package com.ticketflip.scanner.data.repository

import android.app.Application
import android.util.Log
import com.ticketflip.scanner.data.api.ApiClient
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.data.model.response.UserResponse
import kotlinx.coroutines.withTimeout

class UserRepository(context: Application) {
    private val apiClient = ApiClient().getApiService(context)

    suspend fun getUser(): Resource<UserResponse> {
        val response = try {
            Log.i("UserRepository", "SUCCESS")
            withTimeout(5_000) {
                apiClient.getUser().get(0)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", e.message ?: "No exception message available")
            return Resource.Error("An unknown error occured")
        }

        return Resource.Success(response)
    }
}