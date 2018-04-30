package com.deliveryhero.services.crs.restaurant

import com.deliveryhero.services.crs.auth.RequestToken
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class RestaurantsCacheKeyGenerator: KeyGenerator {

    companion object {
        const val VERSION = "v1"
    }

    // do NOT call, when not authenticated, as there is no token then
    override fun generate(target: Any, method: Method, vararg params: Any?) = VERSION + ":" + RequestToken.get()
}