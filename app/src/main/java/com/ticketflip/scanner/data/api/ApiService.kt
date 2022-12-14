package  com.ticketflip.scanner.data.api

import com.ticketflip.scanner.data.model.request.ScanRequest
import com.ticketflip.scanner.data.model.response.EventResponse
import com.ticketflip.scanner.data.model.response.ScanResponse
import com.ticketflip.scanner.data.model.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    /**
     * User Routes
     */
    @GET("/api/users/")
    suspend fun getUser(): List<UserResponse>

    /**
     * Event Routes
     */
    @GET("/api/events/active")
    suspend fun getEvents(): List<EventResponse>

    /**
     * Scan Routes
     */
    @POST("/api/tickets/scan")
    suspend fun scan(@Body body: ScanRequest): ScanResponse

}