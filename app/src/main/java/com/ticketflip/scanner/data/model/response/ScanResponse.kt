package com.ticketflip.scanner.data.model.response

import com.google.gson.annotations.SerializedName

data class ScanResponse(
    @SerializedName("isValid")
    var isValid: String,

    @SerializedName("message")
    var message: String,
)