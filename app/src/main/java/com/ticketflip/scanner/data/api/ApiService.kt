package  com.ticketflip.scanner.data.api

import com.ticketflip.scanner.data.model.response.EventResponse
import com.ticketflip.scanner.data.model.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


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

    @GET("/api/events/{eventId}/tickets")
    suspend fun getEventTicketCount(@Header("x-auth-token") token: String, @Path("eventId") eventId: String): Int


    @GET("/api/events/{eventId}/check-in")
    suspend fun getEventCheckinCount(@Header("x-auth-token") token: String, @Path("eventId") eventId: String): Int


    /**
     * Scan Routes
     */

}