package com.deliveryhero.services.crs.api.delivery

import com.deliveryhero.services.crs.api.HashableEnum

/**
 * Several types of payment.
 *
 * @author manzke
 */
enum class PaymentType : HashableEnum<PaymentType> {
    NONE,
    CASH,
    CREDIT_CARD,
    INTERNAL,
    DEBITOR,
    ONLINE,
    PAYPAL,
    COUPON,
    SUMUP,
    UNKNOWN;


    companion object {

        fun fromNameOrUnknown(name: String): PaymentType {
            for (paymentType in PaymentType.values()) {
                if (paymentType.name.equals(name, ignoreCase = true)) {
                    return paymentType
                }
            }
            return UNKNOWN
        }
    }
}
