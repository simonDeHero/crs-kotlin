package com.deliveryhero.services.crs.auth

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class BearerAuthenticationToken(
        val token: String,
        authorities: Collection<GrantedAuthority>? = null
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials() = ""

    override fun getPrincipal(): Any? = details
}