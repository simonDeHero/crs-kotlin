package com.deliveryhero.services.crs.auth

import com.deliveryhero.services.crs.api.auth.Token
import com.deliveryhero.services.crs.error.AuthenticationException
import com.deliveryhero.services.crs.webkick.WebkickApiFactory
import com.deliveryhero.services.legacy.webkick.api.WebkickOperatorApi
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthService(webkickApiFactory: WebkickApiFactory) {

    companion object {
        private const val SESSION_COOKIE: String = "SESSION9C"
    }

    private var operatorApi: WebkickOperatorApi = webkickApiFactory.operatorApi

    fun login(username: String, password: String): Token {

        val loginResponse = operatorApi.login(username, password)

        if (loginResponse.status != HttpStatus.FOUND.value() ||
                !loginResponse.cookies.containsKey(SESSION_COOKIE)) {
            throw AuthenticationException("An authentication error occured.")
        }

        return Token(loginResponse.cookies[SESSION_COOKIE]!!.value)
    }

    fun getUserDetails(): UserDetails {

        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication.details !is UserDetails) {
            throw IllegalStateException("Unsupported authentication details: " + authentication.details)
        }

        return authentication.details as UserDetails
    }
}