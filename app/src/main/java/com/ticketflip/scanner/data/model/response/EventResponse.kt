package com.ticketflip.scanner.data.model.response

import com.google.gson.annotations.SerializedName

/**
 * Event Response
 * The GSON response body for a single event.
 */
data class EventResponse(
    @SerializedName("_id")
    var eventId: String,

    @SerializedName("eventName")
    var eventName: String,

    @SerializedName("eventDescription")
    var eventDescription: String,

    @SerializedName("eventStart")
    var eventStart: String,

    @SerializedName("eventEnd")
    var eventEnd: String,

    @SerializedName("eventAge")
    var eventAge: String,

    @SerializedName("eventImage")
    var eventImage: String,

    )