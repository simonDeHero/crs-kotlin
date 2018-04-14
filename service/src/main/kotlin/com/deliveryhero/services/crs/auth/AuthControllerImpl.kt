package com.deliveryhero.services.crs.auth

import com.deliveryhero.services.crs.api.AuthController
import com.deliveryhero.services.crs.api.Token
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(AuthController.PATH)
class AuthControllerImpl(
        private val authService: AuthService)
    : AuthController {

    override fun login(username: String, password: String): Token = authService.login(username, password)
}