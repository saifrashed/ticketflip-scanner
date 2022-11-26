package com.ticketflip.scanner.data.repository

import android.app.Application
import android.util.Log
import com.ticketflip.scanner.data.api.ApiClient
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.data.model.response.EventResponse
import com.ticketflip.scanner.data.model.response.UserResponse
import kotlinx.coroutines.withTimeout

class EventRepository(context: Application) {
    private val apiClient = ApiClient().getApiService(context)

    suspend fun getEvents(): Resource<List<EventResponse>> {
        val response = try {
            Log.i("EventRepository", "SUCCESS")
            withTimeout(5_000) {
                apiClient.getEvents()
            }
        } catch (e: Exception) {
            Log.e("EventRepository", e.message ?: "No exception message available")
            return Resource.Error("An unknown error occured")
        }

        if(response.isEmpty()) {
            return Resource.Empty()
        } else {
            return Resource.Success(response)

        }
    }


    suspend fun getEventTicketCount(eventId: String): Resource<Int> {
        val response = try {
            Log.i("EventRepository", "SUCCESS")
            withTimeout(5_000) {
                apiClient.getEventTicketCount(eventId)
            }
        } catch (e: Exception) {
            Log.e("EventRepository", e.message ?: "No exception message available")
            return Resource.Error("An unknown error occured")
        }

        return Resource.Success(response)
    }

    suspend fun getEventCheckinCount(eventId: String): Resource<Int> {
        val response = try {
            Log.i("EventRepository", "SUCCESS")
            withTimeout(5_000) {
                apiClient.getEventCheckinCount(eventId)
            }
        } catch (e: Exception) {
            Log.e("EventRepository", e.message ?: "No exception message available")
            return Resource.Error("An unknown error occured")
        }

        return Resource.Success(response)
    }
}