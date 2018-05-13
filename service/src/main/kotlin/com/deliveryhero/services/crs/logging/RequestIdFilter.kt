package com.deliveryhero.services.crs.logging

import org.slf4j.MDC
import java.util.*
import javax.servlet.*

object RequestIdFilter : Filter {

    override fun destroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        try {
            val mdcData = String.format("[requestId:%s] ", UUID.randomUUID().toString())
            MDC.put("mdcData", mdcData) //Referenced from logging configuration.
            chain!!.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }

    override fun init(filterConfig: FilterConfig?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}