package com.ticketflip.scanner.data.model.response

import com.google.gson.annotations.SerializedName

/**
 * User Response
 * A GSON Response body for a user.
 */
data class UserResponse(
    @SerializedName("_id")
    var userId: String,

    @SerializedName("userName")
    var userName: String,

    @SerializedName("userEmail")
    var userEmail: String,

    @SerializedName("firstName")
    var firstName: String,

    @SerializedName("lastName")
    var lastName: String,

    )