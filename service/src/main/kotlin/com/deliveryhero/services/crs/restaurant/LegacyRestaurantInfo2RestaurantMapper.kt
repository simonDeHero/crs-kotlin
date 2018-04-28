package com.deliveryhero.services.crs.restaurant

import com.deliveryhero.services.crs.api.restaurant.Address
import com.deliveryhero.services.crs.api.delivery.Currency
import com.deliveryhero.services.crs.api.restaurant.PlatformRestaurant
import com.deliveryhero.services.crs.api.restaurant.Restaurant
import com.deliveryhero.services.legacy.webkick.api.*
import com.ninecookies.common.model.Iso3166Alpha2Code
import java.util.stream.Collectors

object LegacyRestaurantInfo2RestaurantMapper {

    fun map(legacyRestaurantInfo: LegacyRestaurantInfo): Restaurant {

        val isRestaurantDelivery =
                if (legacyRestaurantInfo.contractPlan[LegacyContractPlanOption.TRANSPORT.name] != null) 1 else 0

        val restaurant = legacyRestaurantInfo.restaurant
        val platformRestaurants = restaurant.deliveryPlatformRestaurants?.stream()?.map { map(it) }?.collect(Collectors.toList())

        // TODO how to do this with stream?
        val featureFlags = HashMap<String, Boolean>()
        legacyRestaurantInfo.options.forEach { k, v -> featureFlags[k] = v.toString().toBoolean() }

        val restaurantAddress = restaurant.restaurantAddress

        // TODO supportPhone is mapped in the LegacyGoMetadata case
        return Restaurant(restaurant.realName, restaurantAddress.phone, restaurantAddress.phone,
                restaurant.timezone, map(restaurantAddress), map(restaurant.currency),
                isRestaurantDelivery != 0, platformRestaurants, legacyRestaurantInfo.contractPlan,
                featureFlags, legacyRestaurantInfo.operatorCode)
    }

    fun map(legacyRestaurantAddress: LegacyRestaurantAddress): Address {

        val concatenatedAddress: String
        // only for HH restos (i.e. GB), see in icash: com.ninecookies.geo.StreetAddress.Format#BUILDING_FIRST
        if ("GB".equals(legacyRestaurantAddress.country.isoCode, ignoreCase = true)) {
            concatenatedAddress = legacyRestaurantAddress.building + " " + legacyRestaurantAddress.street
        } else {
            concatenatedAddress = legacyRestaurantAddress.street + " " + legacyRestaurantAddress.building
        }
        return Address(concatenatedAddress, legacyRestaurantAddress.zip, legacyRestaurantAddress.city,
                legacyRestaurantAddress.country.name, Iso3166Alpha2Code.valueOf(legacyRestaurantAddress.country.isoCode),
                legacyRestaurantAddress.street, legacyRestaurantAddress.building, legacyRestaurantAddress.latitude,
                legacyRestaurantAddress.longitude)
    }

    fun map(legacyCurrency: LegacyCurrency): Currency =
            Currency(legacyCurrency.symbol, legacyCurrency.code, legacyCurrency.name, legacyCurrency.decimal.toInt())

    fun map(legacyDeliveryPlatformRestaurant: LegacyDeliveryPlatformRestaurant): PlatformRestaurant =
            PlatformRestaurant(legacyDeliveryPlatformRestaurant.externalId, legacyDeliveryPlatformRestaurant.name,
                    legacyDeliveryPlatformRestaurant.deliveryPlatform.type,
                    legacyDeliveryPlatformRestaurant.supportsMenuItemAvailability)
}