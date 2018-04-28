package com.deliveryhero.services.crs.api.auth

import javax.validation.constraints.NotBlank

/**
 * A model for credentials with username and password.
 *
 * @author manzke
 */
data class UsernamePasswordCredentials(

        /**
         * The username.
         */
        @NotBlank val username: String,
        /**
         * The password.
         */
        @NotBlank val password: String
)
