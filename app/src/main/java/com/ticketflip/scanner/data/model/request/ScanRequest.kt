package com.ticketflip.scanner.data.model.request

import com.google.gson.annotations.SerializedName

data class ScanRequest(
    var eventId: String,
    var ticketId: String
)