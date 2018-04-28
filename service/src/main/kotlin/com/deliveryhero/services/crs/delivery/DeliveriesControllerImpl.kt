package com.deliveryhero.services.crs.delivery

import com.deliveryhero.services.crs.api.delivery.DeliveriesController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(DeliveriesController.PATH)
class DeliveriesControllerImpl(private val deliveriesService: DeliveriesService) : DeliveriesController {

    override fun getDeliveryById(id: String) = deliveriesService.getById(id)

    override fun getNewDeliveriesIds(): ResponseEntity<List<String>> {
        val newDeliveriesIds = deliveriesService.getNewDeliveriesIds()
        return if (newDeliveriesIds.isEmpty()) ResponseEntity.noContent().build() else ResponseEntity.ok().build()
    }
}