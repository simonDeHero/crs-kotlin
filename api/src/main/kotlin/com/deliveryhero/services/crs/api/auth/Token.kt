package com.deliveryhero.services.crs.api.auth

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(value = "token", description = "A model containing authentication information.")
data class Token(

        @ApiModelProperty(
                notes = "The token value for authentication.",
                required = true,
                readOnly = true,
                example = "furgfg7556NBggzrf95785Tf46VFR")
        val token: String
)