package com.deliveryhero.services.crs.api.delivery

import com.deliveryhero.services.crs.api.HashableEnum

/**
 * Values for tracking states.
 */
enum class TrackingStateType : HashableEnum<TrackingStateType> {
    /**
     * Tracking is not enabled.
     */
    NOT_TRACKED,
    /**
     * Tracking is enabled.
     */
    TRACKED,
    /**
     * The driver picked up the order at the restaurant.
     */
    DRIVER_PICKED_UP,
    /**
     * Driver is on his way.
     */
    EN_ROUTE,
    /**
     * Driver has successfully delivered the order to the customer.
     */
    DELIVERED,
    /**
     * The order was cancelled.
     */
    CANCELLED
}
