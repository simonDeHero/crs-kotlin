package com.deliveryhero.services.crs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

/*
https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#build-tool-plugins-maven-packaging
https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-create-a-deployable-war-file
 */
@SpringBootApplication
class CrsApplication: SpringBootServletInitializer() {

    override fun configure(builder: SpringApplicationBuilder?): SpringApplicationBuilder? {
        return builder?.sources(CrsApplication::class.java)
    }
}

fun main(args: Array<String>) {
    runApplication<CrsApplication>(*args)
}
