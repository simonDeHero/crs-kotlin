package com.deliveryhero.services.crs.status

import com.deliveryhero.services.crs.api.status.Status
import com.deliveryhero.services.crs.api.status.StatusController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(StatusController.PATH)
class StatusControllerImpl : StatusController {

    override fun getStatus() = Status
}