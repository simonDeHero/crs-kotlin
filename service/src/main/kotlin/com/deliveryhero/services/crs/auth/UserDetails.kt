package com.deliveryhero.services.crs.auth

import com.deliveryhero.services.legacy.webkick.api.LegacyRestaurantInfo

// an "empty" constructor is needed for redis-cache deserialization, so unfortunately nullable types
data class UserDetails(val restaurantInfo: LegacyRestaurantInfo? = null, val token: String? = null)