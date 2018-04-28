package com.deliveryhero.services.crs.api.delivery

import com.deliveryhero.services.crs.api.HashableEnum

enum class RejectReasonType(val id: Int) : HashableEnum<RejectReasonType> {

    BLACKLISTED_CUSTOMER(1),
    DUPLICATE_ORDER(2),
    OUTSIDE_DELIVERY_AREA(3),
    TOO_BUSY_NO_DRIVERS(4),
    TOO_BUSY_KITCHEN(5),
    INCOMPLETE_ADDRESS(6),
    CARD_READER_NOT_AVAILABLE(7),
    /**
     * Minimum order value not reached.
     */
    MOV_NOT_REACHED(8),
    OTHER(10),
    /**
     * Restaurant cooked the order but customer did not show up.
     */
    CUSTOMER_NO_SHOW(11),
    /**
     * Restaurant was delayed and customer did not want to wait and left.
     */
    CUSTOMER_LEFT(12),
    TOO_BUSY(20),
    PRODUCT_UNAVAILABLE(21),
    CLOSED(22),
    TECHNICAL_PROBLEM(23),
    CUSTOMER_NO_ANSWER(24),
    BAD_WEATHER(25),
    PLATFORM_ACCOUNT_PROBLEM(26);

    companion object {

        fun getById(id: Int): RejectReasonType? {
            for (reason in values()) {
                if (id == reason.id) {
                    return reason
                }
            }
            return null
        }
    }
}
