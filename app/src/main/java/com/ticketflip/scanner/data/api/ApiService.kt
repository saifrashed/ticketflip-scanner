package com.hva.amsix.data.api

import com.ticketflip.scanner.data.model.response.EventListReponse
import com.ticketflip.scanner.data.model.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


interface ApiService {

    /**
     * Auth Routes
     */

    @GET("/api/users/")
    suspend fun getUser(@Header("x-auth-token") token: String): UserResponse


    /**
     * Event Routes
     */
    @GET("/api/events/active")
    suspend fun getEvents(): EventListReponse


    /**
     * Scan Routes
     */

}