package com.deliveryhero.services.crs.auth

import com.deliveryhero.services.crs.restaurant.RestaurantService
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class UserDetailsService(private val restaurantService: RestaurantService) {

    @Cacheable(value = ["userDetails"], key = "'v1:' + #token", cacheManager = "userDetailsCacheManager")
    fun getForToken(token: String): UserDetails {
        val restaurants = restaurantService.getAll(token)
        if (restaurants.isEmpty() || restaurants.size > 1) {
            throw IllegalStateException("not exactly 1 restaurant, but " + restaurants.size)
        }
        return UserDetails(restaurants[0], token)
    }
}