package com.deliveryhero.services.crs.auth

object RequestToken {

    private val threadLocal = ThreadLocal<String>()

    fun set(token: String?) = threadLocal.set(token)

    fun get(): String? = threadLocal.get()

    fun clean() = threadLocal.remove()
}