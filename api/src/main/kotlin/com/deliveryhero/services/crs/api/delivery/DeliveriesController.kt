package com.deliveryhero.services.crs.api.delivery

import com.deliveryhero.services.crs.api.error.Error
import io.swagger.annotations.*
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Api(value = DeliveriesController.PATH, description = "This is the deliveries resource that allows to manage deliveries.")
@RequestMapping(DeliveriesController.PATH)
interface DeliveriesController {

    companion object {
        const val PATH = "api/1/deliveries"
    }

    /*
     TODO
     - service authentication in @ApiImplicitParam, i.e. Authorization "Bearer &#x3C;token&#x3E;" or "CRS-HMAC &#x3C;values&#x3E;"
     */
    @ApiOperation(value = "Returns all delivery ids for deliveries in state `NEW`. The ids are sorted by creation "
            + "timestamp - oldest to latest. If there are any deliveries that expire soon, their ids will simply be "
            + "appended to the end of the list - sorted oldest to latest.")
    @ApiResponses(value = [
        (ApiResponse(
                code = 200,
                message = "List with ids of new deliveries",
                response = Array<String>::class)),
        (ApiResponse(
                code = 204,
                message = "No new deliveries are found. Body is empty then",
                response = Unit::class)),
        (ApiResponse(
                code = 401,
                message = "Authentication Header missing or not valid",
                response = Error::class)),
        (ApiResponse(
                code = 500,
                message = "Internal Error",
                response = Error::class))])
    @ApiImplicitParam(
            name = "authorization",
            value = "Authorization token",
            required = true,
            dataType = "string",
            paramType = "header",
            example = "'Bearer <token>'")
    @GetMapping(
            path = ["ids/new"],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getNewDeliveriesIds(): ResponseEntity<List<String>>

    /*
     TODO
     - service authentication in @ApiImplicitParam, i.e. Authorization "Bearer &#x3C;token&#x3E;" or "CRS-HMAC &#x3C;values&#x3E;"
     */
    @ApiOperation(value = "Returns the information about a specific delivery.")
    @ApiResponses(value = [
        (ApiResponse(
                code = 200,
                message = "Information about the Delivery",
                response = Delivery::class)),
        (ApiResponse(
                code = 401,
                message = "Authentication Header missing or not valid",
                response = Error::class)),
        (ApiResponse(
                code = 404,
                message = "If the delivery does not exist",
                response = Error::class)),
        (ApiResponse(
                code = 500,
                message = "Internal Error",
                response = Error::class))])
    @ApiImplicitParam(
            name = "authorization",
            value = "Authorization token",
            required = true,
            dataType = "string",
            paramType = "header",
            example = "'Bearer <token>'")
    @GetMapping(
            path = ["{id}"],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDeliveryById(
            @ApiParam(value = "id of the delivery", required = true)
            @PathVariable
            id: String
    ): Delivery

    @ApiOperation(value = "Changes the state of the order", notes = "\${DeliveriesController.changeState.notes}")
    @ApiResponses(value = [
        (ApiResponse(
                code = 200,
                message = "State has been changed successfully",
                response = Delivery::class)),
        (ApiResponse(
                code = 400,
                message = "If an unsupported state has been given or the additional state change " +
                        "parameters are invalid",
                response = Error::class)),
        (ApiResponse(
                code = 401,
                message = "Authorization Header missing or not valid",
                response = Error::class)),
        (ApiResponse(
                code = 404,
                message = "If the delivery does not exist",
                response = Error::class)),
        (ApiResponse(
                code = 409,
                message = "The state change is not allowed, e.g. by configuration or due to the current state of "
                        + "the delivery",
                response = Error::class)),
        (ApiResponse(
                code = 500,
                message = "Internal Error",
                response = Error::class))])
    @ApiImplicitParam(
            name = "authorization",
            value = "Authorization token",
            required = true,
            dataType = "string",
            paramType = "header",
            example = "'Bearer <token>'")
    @PutMapping(
            path = ["{id}/state"],
            produces = [MediaType.APPLICATION_JSON_VALUE],
            consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun changeState(
            @ApiParam(value = "id of the delivery", required = true)
            @PathVariable
            id: String,

            @ApiParam(value = "id of the delivery", required = true)
            @RequestBody
            deliveryState: DeliveryState
    ): Delivery
}