package com.deliveryhero.services.crs.api.delivery

import java.time.Instant

/**
 * A model containing information about the sending of a SMS.
 */
data class Sms(

        /**
         * When the sending of the sms was performed. Only provided if the sending was successful.
         */
        val timestamp: Instant,
        /**
         * Due to what kind of event the SMS was sent.
         */
        val type: SmsType,
        /**
         * In case of `DELIVERY_STARTED` this is the estimated time of arrival at the customer. Otherwise it is
         * `null`.
         */
        val eta: Instant? = null
)
