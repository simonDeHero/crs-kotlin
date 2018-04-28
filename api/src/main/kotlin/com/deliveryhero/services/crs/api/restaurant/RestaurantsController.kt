package com.deliveryhero.services.crs.api.restaurant

import com.deliveryhero.services.crs.api.Error
import io.swagger.annotations.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

//@FeignClient(name = "restaurantsControllerClient", url = "\${crs.url}:\${crs.port}", path = RestaurantsController.PATH)
@Api(value = RestaurantsController.PATH,
        description = "This resource manages the restaurants of the current authenticated restaurant owner.")
interface RestaurantsController {

    companion object {
        const val PATH = "api/1/restaurants"
    }

    /*
     TODO
     - etag,
     - service authentication in @ApiImplicitParam, i.e. Authorization "Bearer &#x3C;token&#x3E;" or "CRS-HMAC &#x3C;values&#x3E;"
     */
    @ApiOperation(value = """
        Lists the available restaurants for the current authenticated user. The list only contains basic information
        like name and address and does not provide more complex fields like: "contractPlan" or "featureFlags".""")
    @ApiResponses(value = [
        (ApiResponse(code = 200, message = "Success", response = Array<Restaurant>::class)),
        (ApiResponse(code = 401, message = "Authentication Required", response = Error::class)),
        (ApiResponse(code = 500, message = "Internal Server Error", response = Error::class)),
        (ApiResponse(code = 502, message = "If an error with an upstream external service was encountered.",
                response = Error::class))])
    @ApiImplicitParam(name = "authorization", value = "Authorization token", required = true, dataType = "string",
            paramType = "header", example = "'Bearer <token>'")
    @GetMapping
    fun getAll(): List<Restaurant>

    /*
     TODO
     - etag,
     - service authentication in @ApiImplicitParam, i.e. Authorization "Bearer &#x3C;token&#x3E;" or "CRS-HMAC &#x3C;values&#x3E;"
     */
    @ApiOperation(value = "Gets the restaurant data for the given id.")
    @ApiResponses(value = [
        (ApiResponse(code = 200, message = "Success", response = Restaurant::class)),
        (ApiResponse(code = 401, message = "Authentication Required", response = Error::class)),
        (ApiResponse(code = 404, message = "If the restaurant with the given id does not exist.",
                response = Error::class)),
        (ApiResponse(code = 500, message = "Internal Server Error", response = Error::class)),
        (ApiResponse(code = 502, message = "If an error with an upstream external service was encountered.",
                response = Error::class))])
    @ApiImplicitParam(name = "authorization", value = "Authorization token", required = true, dataType = "string",
            paramType = "header", example = "'Bearer <token>'")
    @GetMapping(path = ["{id}"])
    fun getById(
            @ApiParam(value = "the icash restaurant id", required = true)
            @PathVariable
            id: String
    ): Restaurant
}