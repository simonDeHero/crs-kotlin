package com.deliveryhero.services.crs.api.delivery

/**
 * Possible events, due to which an SMS was sent.
 */
enum class SmsType {
    /**
     * The delivery to the customer has been started.
     */
    DELIVERY_STARTED,
    /**
     * The delivery to the customer has been delayed.
     */
    DELAYED
}
