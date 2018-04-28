package com.deliveryhero.services.crs.api.delivery

import com.deliveryhero.services.crs.api.HashableEnum

/**
 * Possible types of transporting a delivery.
 */
enum class TransportType(val id: Int) : HashableEnum<TransportType> {
    /**
     * The customer picks up the delivery in the restaurant.
     */
    PICKUP_CUSTOMER(3),
    /**
     * An external driver delivers the order, e.g. from Hurrier.
     */
    PICKUP_LOGISTICS(4),
    /**
     * A mixed mode.
     */
    MIXED(5),
    /**
     * ?
     */
    NONE(6),
    /**
     * ?
     */
    UNKNOWN(7),
    /**
     * A driver of the restaurant delivers the order.
     */
    RESTAURANT_DELIVERY(8);


    companion object {

        fun getById(id: Int): TransportType? {
            for (reason in values()) {
                if (id == reason.id) {
                    return reason
                }
            }
            return null
        }
    }
}
