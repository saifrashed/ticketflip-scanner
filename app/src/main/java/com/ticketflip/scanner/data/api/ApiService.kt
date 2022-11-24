package  com.ticketflip.scanner.data.api

import com.ticketflip.scanner.data.model.response.EventResponse
import com.ticketflip.scanner.data.model.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Header


interface ApiService {

    /**
     * User Routes
     */

    @GET("/api/users/")
    suspend fun getUser(@Header("x-auth-token") token: String): List<UserResponse>


    /**
     * Event Routes
     */
    @GET("/api/events/active")
    suspend fun getEvents(@Header("x-auth-token") token: String): List<EventResponse>


    /**
     * Scan Routes
     */

}