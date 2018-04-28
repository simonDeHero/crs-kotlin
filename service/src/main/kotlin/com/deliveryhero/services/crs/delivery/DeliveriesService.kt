package com.deliveryhero.services.crs.delivery

import com.deliveryhero.services.crs.api.delivery.Delivery
import com.deliveryhero.services.crs.webkick.WebkickApiFactory
import com.deliveryhero.services.legacy.webkick.api.LegacyOrder
import com.deliveryhero.services.legacy.webkick.api.NewDeliveryResult
import com.deliveryhero.services.legacy.webkick.api.WebkickOperatorApi
import org.springframework.stereotype.Service
import java.util.*
import javax.ws.rs.BadRequestException
import javax.ws.rs.NotFoundException

@Service
class DeliveriesService(webkickApiFactory: WebkickApiFactory) {

    private var operatorApi: WebkickOperatorApi = webkickApiFactory.operatorApi

    fun getById(id: String): Delivery {

        val deliveries: List<LegacyOrder>?
        try {
            deliveries = operatorApi.findDeliveryById(null, id)
        } catch (e: BadRequestException) {
            // currently only the case when the given id is not a UUID. then Icash cannot parse it.
            throw NotFoundException()
        }

        // TODO check if "null" can be returned in the first place
        if (deliveries == null || deliveries.isEmpty()) {
            throw NotFoundException()
        }

        val deliveryDetails = operatorApi.findDeliveryDetails(null, id)

        return LegacyOrder2DeliveryMapper.toDelivery(deliveries[0], deliveryDetails)
    }

    fun getNewDeliveriesIds(): List<String> {

        val newDeliveryResult = operatorApi.checkForDeliveries(null)
        return if (newDeliveryResult == null) {
            listOf()
        } else {
            extractNewDeliveryIds(newDeliveryResult)
        }
    }

    private fun extractNewDeliveryIds(newDeliveryResult: NewDeliveryResult): List<String> {

        if (newDeliveryResult.isEmpty) {
            return ArrayList()
        }

        val ids = ArrayList<String>()
        if (newDeliveryResult.deliveryIds != null) {
            ids.addAll(newDeliveryResult.deliveryIds)
        }
        if (newDeliveryResult.expiringDeliveryIds != null) {
            ids.addAll(newDeliveryResult.expiringDeliveryIds)
        }

        return ids
    }
}