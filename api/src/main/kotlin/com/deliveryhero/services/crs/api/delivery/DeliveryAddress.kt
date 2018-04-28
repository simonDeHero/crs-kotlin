package com.deliveryhero.services.crs.api.delivery

data class DeliveryAddress(

        /**
         * The internal id of the customer address.
         */
        val customerAddressId: String,
        /**
         * The name of the company, if the order should be delivered to a company.
         */
        val company: String? = null,
        /**
         * The street incl. number.
         */
        val street: String? = null,
        /**
         * The zip code.
         */
        val zip: String? = null,
        /**
         * The city
         */
        val city: String? = null,
        /**
         * The area, in Icash it is `suburb`.
         */
        val area: String? = null,
        /**
         * The block.
         */
        val block: String? = null,
        /**
         * The floor.
         */
        val floor: String? = null,
        /**
         * The apartment id/number, in Icash it is `door`.
         */
        val apartment: String? = null,
        /**
         * The building.
         */
        val building: String? = null,
        /**
         * Some additional info.
         */
        val info: String? = null,
        /**
         * The latitude.
         */
        val latitude: Double? = null,
        /**
         * The longitude.
         */
        val longitude: Double? = null,
        /**
         * The distance from the restaurant in meters. Is `-1`, if not available.
         */
        val distance: Int? = -1,
        /**
         * Whether the coordinates have been geocoded manually.
         */
        val geocodedManually: Boolean = false
)
