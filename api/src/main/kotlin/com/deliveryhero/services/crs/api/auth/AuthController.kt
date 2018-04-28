package com.deliveryhero.services.crs.api.auth

import com.deliveryhero.services.crs.api.Error
import io.swagger.annotations.*
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.constraints.NotBlank

//@FeignClient(name = "authControllerClient", url = "\${crs.url}:\${crs.port}", path = AuthController.PATH)
@Api(value = AuthController.PATH, description = "This is the authentication resource that allows to manage authentication")
interface AuthController {

    companion object {
        const val PATH = "api/1/auth"
    }

    @ApiOperation(value = """
        Returns a token object which can be used for subsequent calls. The token has to be put in the Authorization
        Header with "Bearer " prefix.""")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Login successfully", response = Token::class),
        ApiResponse(code = 400, message = "Validation Error, e.g. required parameter missing", response = Error::class),
        ApiResponse(code = 401, message = "Login failed because of unknown user or wrong password",
                response = Error::class),
        ApiResponse(code = 500, message = "Internal Error", response = Error::class)])
    @PostMapping(path = ["form"], produces = [MediaType.APPLICATION_JSON_VALUE],
            consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun login(
            @ApiParam(value = "email or operator code", required = true)
            @RequestParam("username")
            @NotBlank
            username: String,

            @ApiParam(value = "password for the email or operator code", required = true)
            @RequestParam("password")
            @NotBlank
            password: String
    ): Token

    @ApiOperation(value = """
        Returns a token object which can be used for subsequent calls. The token has to be put in the Authorization
        Header with "Bearer " prefix.""")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Login successfully", response = Token::class),
        ApiResponse(code = 400, message = "Validation Error, e.g. required parameter missing", response = Error::class),
        ApiResponse(code = 401, message = "Login failed because of unknown user or wrong password",
                response = Error::class),
        ApiResponse(code = 500, message = "Internal Error", response = Error::class)])
    @PostMapping(path = ["form"], produces = [MediaType.APPLICATION_JSON_VALUE],
            consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(
            @ApiParam(value = "username (email or operator code) password credentials", required = true)
            @RequestBody usernamePasswordCredentials: UsernamePasswordCredentials
    ): Token
}