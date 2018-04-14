package com.deliveryhero.services.crs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@EnableSwagger2
@Configuration
class CrsConfig {

    @Bean
    fun docket(): Docket =
            Docket(DocumentationType.SWAGGER_2)
                    .useDefaultResponseMessages(false)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.deliveryhero.services.crs"))
                    .paths(PathSelectors.any())
                    .build()
                    .apiInfo(ApiInfoBuilder().description("The CRS REST API").build())
}