package com.deliveryhero.services.crs.restaurant

import com.deliveryhero.services.crs.webkick.WebkickApiFactory
import com.deliveryhero.services.legacy.webkick.api.LegacyRestaurantInfo
import com.deliveryhero.services.legacy.webkick.api.WebkickOperatorApi
import com.google.common.collect.ImmutableList
import org.springframework.stereotype.Service

@Service
class RestaurantService(webkickApiFactory: WebkickApiFactory) {

    private var operatorApi: WebkickOperatorApi = webkickApiFactory.operatorApi

    fun getAll(): List<LegacyRestaurantInfo> {

        val legacyRestaurantInfo = operatorApi.getRestaurantInfo(null);
        if (legacyRestaurantInfo == null
                || legacyRestaurantInfo.restaurant == null
                || legacyRestaurantInfo.restaurant.id == null) {
            throw IllegalStateException("Invalid restaurant info response.")
        }

        return ImmutableList.of(legacyRestaurantInfo)
    }
}