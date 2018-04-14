package com.deliveryhero.services.crs.restaurant

import com.deliveryhero.services.crs.api.Restaurant
import com.deliveryhero.services.crs.api.RestaurantsController
import com.deliveryhero.services.crs.mapping.LegacyRestaurantInfo2RestaurantMapper
import com.deliveryhero.services.legacy.webkick.api.LegacyRestaurantInfo
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.stream.Collectors

@RestController
@RequestMapping(RestaurantsController.PATH)
class RestaurantsControllerImpl(
        private val restaurantService: RestaurantService)
    : RestaurantsController {

    override fun getAll(): List<Restaurant> = map(restaurantService.getAll())

    override fun getById(id: String): Restaurant {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun map(legacyRestaurantInfo: LegacyRestaurantInfo): Restaurant {
        return LegacyRestaurantInfo2RestaurantMapper.map(legacyRestaurantInfo)
    }

    private fun map(legacyRestaurantsInfos: List<LegacyRestaurantInfo>): List<Restaurant> {
        return legacyRestaurantsInfos.stream().map { map(it) }.collect(Collectors.toList())
    }
}