package com.deliveryhero.services.crs

import com.deliveryhero.services.legacy.webkick.api.LegacyRestaurantInfo
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.time.Duration

@EnableCaching
@EnableSwagger2
@Configuration
class CrsConfig {

    /*
    https://stackoverflow.com/questions/46238790/how-to-use-spring-annotations-like-autowired-or-value-in-kotlin-for-primitive
    https://stackoverflow.com/questions/38761294/why-doesnt-kotlin-allow-to-use-lateinit-with-primitive-types
     */
    @Value("\${cache.restaurants.ttlmins:10}")
    private var restaurantsCacheTtlMins: Long = 0L // ugly...

    /*
    http://localhost:8080/crs/v2/api-docs
    http://localhost:8080/crs/swagger-ui.html
     */
    @Bean
    fun docket() =
            Docket(DocumentationType.SWAGGER_2)
                    .useDefaultResponseMessages(false)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.deliveryhero.services.crs"))
                    .paths(PathSelectors.any())
                    .build()
                    .apiInfo(ApiInfoBuilder().description("The CRS REST API").build())

    @Bean
    fun restaurantsCacheManager(lettuceConnectionFactory: LettuceConnectionFactory): RedisCacheManager {

        val serializer = Jackson2JsonRedisSerializer<Any>(ObjectMapper().typeFactory
                .constructCollectionType(List::class.java, LegacyRestaurantInfo::class.java))

        val cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(restaurantsCacheTtlMins))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(lettuceConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build()
    }
}