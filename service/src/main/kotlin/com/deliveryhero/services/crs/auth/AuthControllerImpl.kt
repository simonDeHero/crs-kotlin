package com.deliveryhero.services.crs.auth

import com.deliveryhero.services.crs.api.auth.AuthController
import com.deliveryhero.services.crs.api.auth.UsernamePasswordCredentials
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping(AuthController.PATH)
class AuthControllerImpl(private val authService: AuthService): AuthController {

    override fun login(username: String, password: String) = authService.login(username, password)

    override fun login(@RequestBody @Valid usernamePasswordCredentials: UsernamePasswordCredentials) =
            authService.login(usernamePasswordCredentials.username, usernamePasswordCredentials.password)
}