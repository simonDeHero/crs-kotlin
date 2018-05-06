package com.deliveryhero.services.crs.auth

enum class Role(val roleName: String) {

    ADMIN(Constants.ADMIN),
    SERVICE(Constants.SERVICE),
    RESTAURANT(Constants.RESTAURANT);

    override fun toString(): String {
        return roleName
    }

    object Constants {
        private val PREFIX = "ROLE_"
        val ADMIN = PREFIX + "ADMIN"
        val SERVICE = PREFIX + "SERVICE"
        val RESTAURANT = PREFIX + "RESTAURANT"
    }
}
