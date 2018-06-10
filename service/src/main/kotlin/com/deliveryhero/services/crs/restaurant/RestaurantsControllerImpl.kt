package com.deliveryhero.services.crs.restaurant

import com.deliveryhero.services.crs.api.restaurant.Restaurant
import com.deliveryhero.services.crs.api.restaurant.RestaurantsController
import com.deliveryhero.services.legacy.webkick.api.LegacyRestaurantInfo
import org.springframework.web.bind.annotation.RestController

@RestController
class RestaurantsControllerImpl(private val restaurantService: RestaurantService) : RestaurantsController {

    override fun getAll() = map(restaurantService.getAll(null))

    override fun getById(id: String): Restaurant {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun map(legacyRestaurantInfo: LegacyRestaurantInfo) =
            LegacyRestaurantInfo2RestaurantMapper.map(legacyRestaurantInfo)

    private fun map(legacyRestaurantsInfos: List<LegacyRestaurantInfo>) =
            legacyRestaurantsInfos.asSequence().map { map(it) }.toCollection(mutableListOf())
}