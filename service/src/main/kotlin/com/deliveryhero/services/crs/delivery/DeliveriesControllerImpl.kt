package com.deliveryhero.services.crs.delivery

import com.deliveryhero.services.crs.api.delivery.DeliveriesController
import com.deliveryhero.services.crs.api.delivery.DeliveryState
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DeliveriesControllerImpl(private val deliveriesService: DeliveriesService) : DeliveriesController {

    override fun getDeliveryById(@PathVariable id: String) = deliveriesService.getById(id)

    override fun getNewDeliveriesIds(): ResponseEntity<List<String>> {
        val newDeliveriesIds = deliveriesService.getNewDeliveriesIds()
        return if (newDeliveriesIds.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(newDeliveriesIds)
        }
    }

    override fun changeState(@PathVariable id: String, @RequestBody deliveryState: DeliveryState) =
            deliveriesService.changeState(id, deliveryState)
}