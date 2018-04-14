package com.deliveryhero.services.crs.util

import feign.RequestInterceptor
import feign.RequestTemplate

class FeignAuthInterceptor : RequestInterceptor {

    override fun apply(requestTemplate: RequestTemplate?) {

        val token = RequestToken.get()
        if (token != null) {
            requestTemplate!!.header("Authorization", "Bearer " + token.token)
        }
    }
}