package com.deliveryhero.services.crs.util

import com.deliveryhero.services.crs.api.Token

object RequestToken {

    private var token: Token? = null

    fun set(token: Token?) {
        this.token = token
    }

    fun get(): Token? = token
}