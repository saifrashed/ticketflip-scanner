package com.ticketflip.scanner.data.model.request

/**
 * Scan request
 * The request body when a ticket is scanned.
 */
data class ScanRequest(
    var eventId: String,
    var ticketId: String
)