package com.deliveryhero.services.crs.api

import io.swagger.annotations.ApiModelProperty

/**
 * A model containing authentication information.
 */
data class Token(

        @ApiModelProperty(notes = "The token value for authentication.", required = true, readOnly = true,
                allowEmptyValue = false, example = "furgfg7556NBggzrf95785Tf46VFR")
        val token: String
)