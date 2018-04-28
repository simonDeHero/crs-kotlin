package com.deliveryhero.services.crs.api.delivery

import com.deliveryhero.services.crs.api.HashableEnum

/**
 * Values for reasons, why e.g. a driver could cancel a delivery.
 */
enum class TransportCancelReasonType(val id: Int) : HashableEnum<TransportCancelReasonType> {

    // these values correspond to com.ninecookies.domain.DriverCancelReason and are loaded into DB via "icash.sql"!

    /**
     * A default value, e.g. for unknown reasons.
     */
    OTHER(0),
    /**
     * Customer not there at promised time.
     */
    CUSTOMER_NOT_THERE(1),
    /**
     * Incorrect address, no answer to phone.
     */
    INCORRECT_ADDRESS(2),
    /**
     * There was an accident with my scooter/car.
     */
    ACCIDENT(3);

    companion object {

        fun getById(id: Int): TransportCancelReasonType? {
            for (reason in values()) {
                if (id == reason.id) {
                    return reason
                }
            }
            return null
        }
    }
}
