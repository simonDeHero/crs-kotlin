package com.deliveryhero.services.crs.api.delivery

import java.math.BigDecimal

/**
 * Represents an individual article/position in [Delivery].
 *
 * @author alex.panchenko
 * @since 2015-01-21
 */
data class OrderItem(

        /**
         * How often this item is contained in the order.
         */
        val amount: Int,
        //public int article;
        /**
         * The name of the item.
         */
        val name: String,
        /**
         * The category of the item.
         *
         * @see Category.name
         */
        val category: String? = null,
        /**
         * The number of this item in the menu (a.k.a. "PLU").
         */
        val menuNumber: String? = null,
        /**
         * The remote code (a.k.a. "item code")
         */
        val remoteCode: String? = null,
        /**
         * A optional comment to this item by the customer.
         */
        var comment: String? = null,
        //public String shortName;
        /**
         * The price for a single unit of this item.
         */
        val price: BigDecimal,
        /**
         * The summed-up/total cost for all units of this item.
         */
        val total: BigDecimal,

        /**
         * Modifications for this item, e.g. additional sauce or cheese.
         */
        var modifiers: MutableList<OrderItem>? = null
)
