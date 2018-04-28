package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.StatusController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(StatusController.PATH)
class StatusControllerImpl : StatusController {

    override fun getStatus() = "ok"
}