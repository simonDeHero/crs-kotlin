package com.deliveryhero.services.crs.api.delivery

import java.math.BigDecimal

/**
 * Payment information.
 *
 * @author manzke
 */
data class Payment(

        /**
         * Whether the order was already paid online or not.
         */
        val paid: Boolean,
        /**
         * The currency code.
         */
        val currency: String,
        /**
         * The currency symbol, e.g. `` or CHF.
         */
        val currencySymbol: String,
        /**
         * The total amount, the customer has to pay.
         */
        val total: BigDecimal,
        /**
         * The minimum order value.
         */
        val minimumOrderValue: BigDecimal,
        /**
         * How the order is paid.
         */
        val paymentType: PaymentType,
        /**
         * The name of the payment method.
         */
        val paymentMethod: String
)
