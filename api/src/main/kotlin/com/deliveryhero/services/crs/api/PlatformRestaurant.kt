package com.deliveryhero.services.crs.api

import io.swagger.annotations.ApiModelProperty

/**
 * The restaurant representation on the platform.
 */
data class PlatformRestaurant(

        @ApiModelProperty(notes = "The platform's restaurant id (external id).", required = true,
                readOnly = true, allowEmptyValue = false, example = "88548db8-37cc-4083-b5a7-b721f5c3843a")
        val id: String,

        @ApiModelProperty(notes = "The name of the restaurant on the platform like 'Luigi's Pizza'.", required = true,
                readOnly = true, allowEmptyValue = false, example = "Luigi's Pizza")
        val name: String,

        @ApiModelProperty(notes = "The platform this restaurant is selling on.", required = true,
                readOnly = true, allowEmptyValue = false, example = "€")
        val platform: String,

        @ApiModelProperty(notes = """
            Whether this platform restaurant supports menu item availability, i.e. getting menus and updating
            availability of items.""", required = true, readOnly = true, allowEmptyValue = false)
        val supportsMenuItemAvailability: Boolean
)