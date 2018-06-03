package com.deliveryhero.services.crs.restaurant

import com.deliveryhero.services.crs.auth.AuthService
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class RestaurantsCacheKeyGenerator(private val authService: AuthService) : KeyGenerator {

    companion object {
        const val VERSION = "v1"
    }

    override fun generate(target: Any, method: Method, vararg params: Any?) =
            "$VERSION:" + if (params.size == 1 && params[0] != null) {
                params[0]
            } else {
                authService.getUserDetails().token
            }
}