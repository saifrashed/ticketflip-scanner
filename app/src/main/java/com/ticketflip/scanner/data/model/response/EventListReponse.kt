package com.ticketflip.scanner.data.model.response

import com.google.gson.annotations.SerializedName

data class EventListReponse(
    @SerializedName("events")
    var events: ArrayList<EventResponse>,

    @SerializedName("userName")
    var userName: String,

    @SerializedName("userEmail")
    var userEmail: String,

    @SerializedName("firstName")
    var firstName: String,

    @SerializedName("lastName")
    var lastName: String,
)