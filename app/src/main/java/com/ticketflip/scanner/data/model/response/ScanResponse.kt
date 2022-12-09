package com.ticketflip.scanner.data.model.response

import com.google.gson.annotations.SerializedName

data class ScanResponse(
    @SerializedName("isValid")
    var isValid: Boolean,

    @SerializedName("message")
    var message: String,
)