package com.deliveryhero.services.crs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.validation.constraints.NotNull

@Component
@ConfigurationProperties(prefix = "crs")
class CrsProperties {
    /**
     * The URL of the webkick service, e.g. https://stgpos.9cookies.com
     */
    @NotNull lateinit var webkickDomain: String
}