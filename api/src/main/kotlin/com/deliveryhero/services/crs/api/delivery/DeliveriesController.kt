package com.deliveryhero.services.crs.api.delivery

import com.deliveryhero.services.crs.api.Error
import io.swagger.annotations.*
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Api(value = DeliveriesController.PATH, description = "This is the deliveries resource that allows to manage deliveries.")
interface DeliveriesController {

    companion object {
        const val PATH = "api/1/deliveries"
    }

    /*
     TODO
     - etag,
     - service authentication in @ApiImplicitParam, i.e. Authorization "Bearer &#x3C;token&#x3E;" or "CRS-HMAC &#x3C;values&#x3E;"
     */
    @ApiOperation(value = """
        Returns all delivery ids for deliveries in state <code>NEW</code>. The ids are sorted by creation timestamp -
        oldest to latest. If there are any deliveries that expire soon, their ids will simply be appended to the end of
        the list - sorted oldest to latest.""")
    @ApiResponses(value = [
        (ApiResponse(code = 200, message = "List with ids of new deliveries", response = Array<String>::class)),
        (ApiResponse(code = 204, message = "No new deliveries are found. Body is empty then", response = Unit::class)),
        (ApiResponse(code = 401, message = "Authentication Header missing or not valid", response = Error::class)),
        (ApiResponse(code = 500, message = "Internal Error", response = Error::class))])
    @ApiImplicitParam(name = "authorization", value = "Authorization token", required = true, dataType = "string",
            paramType = "header", example = "'Bearer <token>'")
    @GetMapping(path = ["ids/new"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getNewDeliveriesIds(): ResponseEntity<List<String>>

    /*
     TODO
     - etag,
     - service authentication in @ApiImplicitParam, i.e. Authorization "Bearer &#x3C;token&#x3E;" or "CRS-HMAC &#x3C;values&#x3E;"
     */
    @ApiOperation(value = "Returns the information about a specific delivery.")
    @ApiResponses(value = [
        (ApiResponse(code = 200, message = "Information about the Delivery", response = Delivery::class)),
        (ApiResponse(code = 401, message = "Authentication Header missing or not valid", response = Error::class)),
        (ApiResponse(code = 404, message = "If the delivery does not exist", response = Error::class)),
        (ApiResponse(code = 500, message = "Internal Error", response = Error::class))])
    @ApiImplicitParam(name = "authorization", value = "Authorization token", required = true, dataType = "string",
            paramType = "header", example = "'Bearer <token>'")
    @GetMapping(path = ["{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDeliveryById(
            @ApiParam(value = "id of the delivery", required = true)
            @PathVariable
            id: String
    ): Delivery
}