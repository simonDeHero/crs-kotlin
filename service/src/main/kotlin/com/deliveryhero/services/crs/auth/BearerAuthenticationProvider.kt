package com.deliveryhero.services.crs.auth

import org.slf4j.LoggerFactory
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.ws.rs.NotAuthorizedException

@Component
class BearerAuthenticationProvider(
        roleHierarchy: RoleHierarchy,
        private val userDetailsService: UserDetailsService
) : AuthenticationProvider {

    companion object {
        private val LOG = LoggerFactory.getLogger(BearerAuthenticationProvider::class.java)
    }

    private var roleHierarchyMapper: RoleHierarchyAuthoritiesMapper = RoleHierarchyAuthoritiesMapper(roleHierarchy)

    override fun authenticate(authentication: Authentication): Authentication {

        var authToken = authentication as BearerAuthenticationToken

        val token = authToken.token
        val userDetails: UserDetails
        try {
            userDetails = userDetailsService.getForToken(token)
        } catch (e: AuthenticationException) {
            throw e
        } catch (ex: NotAuthorizedException) {
            throw BadCredentialsException("The icash service returned 401.", ex)
        } catch (ex: Exception) {
            val msg = "Error while loading UserDetails."
            LOG.debug(msg, ex)
            throw AuthenticationServiceException(msg, ex)
        }

        val roles = HashSet<GrantedAuthority>()

        roles.add(SimpleGrantedAuthority(Role.Constants.RESTAURANT))
        roles.addAll(roleHierarchyMapper.mapAuthorities(roles))

        // create a new authenticated token with owner details
        authToken = BearerAuthenticationToken(authToken.token, roles)
        authToken.details = userDetails
        authToken.isAuthenticated = true

        return authToken
    }

    override fun supports(authentication: Class<*>) = BearerAuthenticationToken::class.java == authentication
}
