package com.deliveryhero.services.crs.api.auth

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

@ApiModel(value = "usernamePasswordCredentials", description = "A model for credentials with username and password.")
data class UsernamePasswordCredentials(

        @ApiModelProperty(
                notes = "The username.",
                required = true,
                example = "de-10117-lieferheld-restaurant")
        @get:NotBlank val username: String,

        @ApiModelProperty(
                notes = "The password.",
                required = true)
        @get:NotBlank val password: String
)
