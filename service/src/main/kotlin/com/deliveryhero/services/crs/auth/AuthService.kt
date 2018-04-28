package com.deliveryhero.services.crs.auth

import com.deliveryhero.services.crs.api.auth.Token
import com.deliveryhero.services.crs.error.AuthenticationException
import com.deliveryhero.services.crs.webkick.WebkickApiFactory
import com.deliveryhero.services.legacy.webkick.api.WebkickOperatorApi
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

private const val SESSION_COOKIE: String = "SESSION9C"

@Service
class AuthService(webkickApiFactory: WebkickApiFactory) {

    private var operatorApi: WebkickOperatorApi = webkickApiFactory.operatorApi

    fun login(username: String, password: String): Token {

        val loginResponse = operatorApi.login(username, password)

        if (loginResponse.status != HttpStatus.FOUND.value() ||
                !loginResponse.cookies.containsKey(SESSION_COOKIE)) {
            throw AuthenticationException("invalid credentials")
        }

        return Token(loginResponse.cookies[SESSION_COOKIE]!!.value)
    }
}