package com.deliveryhero.services.crs.api.delivery

import com.deliveryhero.services.crs.api.HashableEnum

/**
 * State of delivery, initial state is `NEW`. If the delivery reception was never confirmed by the app, a
 * cron job will set it to `UNDELIVERABLE`. When the user doesn't react to the delivery, a cron job will set
 * the delivery to `EXPIRED` after the expiresAt-date is reached, or to `MISSED` if it was
 * `UNDELIVERABLE`. The states `ACCEPTED`, `PREORDER_ACCEPTED`, `REJECTED`
 * and `CLOSED` are set by the user in the app. So: `NEW` and `UNDELIVERABLE` are only
 * intermediate, no final states.
 */
enum class DeliveryStateType : HashableEnum<DeliveryStateType> {
    /**
     * =0
     */
    NEW,
    /**
     * =1
     */
    ACCEPTED,
    /**
     * =2
     */
    REJECTED,
    /**
     * =3
     */
    CLOSED,
    /**
     * Was received by client, but was not accepted in time.
     * =4
     */
    EXPIRED,
    /**
     * =5
     */
    PREORDER_ACCEPTED,
    /**
     * Was received by the client, but was not accepted in time.
     * =6
     */
    INITIALIZING,
    /**
     * Platform delivery is waiting for the `ASSIGNED` event from logistics and is not visible to restaurant
     * yet.
     * =7 ex: DELIVERING_TO_POS
     */
    WAITING_FOR_TRANSPORT,
    /**
     * Delivery was accepted and then cancelled by the restaurant.
     * =8
     */
    CANCELLED,
    /**
     * Was not received by client.
     * =9
     */
    MISSED,
    /**
     * Delivery was cancelled by the platform.
     * =10
     */
    CANCELLED_BY_PLATFORM,
    /**
     * Delivery was cancelled by the transport provider.
     * =11
     */
    CANCELLED_BY_TRANSPORT
}
