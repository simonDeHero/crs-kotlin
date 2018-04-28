package com.deliveryhero.services.crs.api.delivery

import java.math.BigDecimal

data class PriceModifier(

        /**
         * The localized name of the fee.
         */
        val name: String,
        /**
         * The amount by how much the total is modified. This value will be applied to the total with "+"!
         */
        val value: BigDecimal,
        val includedInPrice: Boolean = true
)
