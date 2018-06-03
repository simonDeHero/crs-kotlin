package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.auth.*
import com.deliveryhero.services.crs.logging.RequestIdFilter
import com.deliveryhero.services.crs.logging.StubLoggingFilter
import com.deliveryhero.services.legacy.webkick.api.LegacyRestaurantInfo
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.*
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.filter.ShallowEtagHeaderFilter
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.time.Duration

@EnableCaching
@EnableSwagger2
@EnableWebSecurity
@PropertySource("classpath:swagger-docu.properties")
@Configuration
// https://springfox.github.io/springfox/docs/current/#springfox-support-for-jsr-303
@Import(value = [BeanValidatorPluginsConfiguration::class])
@EnableConfigurationProperties(CrsProperties::class)
class CrsConfig : WebSecurityConfigurerAdapter() {

    /*
    https://stackoverflow.com/questions/46238790/how-to-use-spring-annotations-like-autowired-or-value-in-kotlin-for-primitive
    https://stackoverflow.com/questions/38761294/why-doesnt-kotlin-allow-to-use-lateinit-with-primitive-types
     */
    @Value("\${cache.restaurants.ttlmins:10}")
    private var restaurantsCacheTtlMins: Long = 0L // ugly...
    @Value("\${cache.userDetails.ttlmins:10}")
    private var userDetailsCacheTtlMins: Long = 0L

    @Autowired
    private lateinit var bearerAuthenticationProvider: BearerAuthenticationProvider
    @Autowired
    private lateinit var http401UnauthorizedEntryPoint: Http401UnauthorizedEntryPoint

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(bearerAuthenticationProvider)
    }

    override fun configure(http: HttpSecurity) {
        val bearerAuthenticationFilter = BearerAuthenticationFilter(authenticationManager(), http401UnauthorizedEntryPoint)
        http
                .addFilterBefore(RequestIdFilter, BasicAuthenticationFilter::class.java)
                .addFilterBefore(StubLoggingFilter(), BasicAuthenticationFilter::class.java)
                .addFilterBefore(bearerAuthenticationFilter, BasicAuthenticationFilter::class.java)
                .addFilterBefore(ShallowEtagHeaderFilter(), BearerAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers(
                        "/api/*/status",
                        "/api/*/version",
                        "/api/*/auth/form",
                        "v2/api-docs",
                        "swagger-ui.html"
                ).permitAll()
                .antMatchers("/api/*/**").fullyAuthenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(http401UnauthorizedEntryPoint)
                .and()
                .cors() // https://docs.spring.io/spring-security/site/docs/current/reference/html/cors.html
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    fun roleHierarchy() =
            RoleHierarchyImpl().apply {
                setHierarchy("""
                ROLE_ADMIN > ROLE_SERVICE
                ROLE_ADMIN > ROLE_RESTAURANT
                ROLE_SERVICE
                ROLE_RESTAURANT""")
            }

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

    @Primary // necessary as there are to beans of type RedisCacheManager
    @Bean
    fun restaurantsCacheManager(lettuceConnectionFactory: LettuceConnectionFactory): RedisCacheManager {

        val serializer = Jackson2JsonRedisSerializer<List<LegacyRestaurantInfo>>(ObjectMapper().typeFactory
                .constructCollectionType(List::class.java, LegacyRestaurantInfo::class.java))

        val cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(restaurantsCacheTtlMins))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(lettuceConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build()
    }

    @Bean
    fun userDetailsCacheManager(lettuceConnectionFactory: LettuceConnectionFactory): RedisCacheManager {

        val cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(userDetailsCacheTtlMins))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(Jackson2JsonRedisSerializer(UserDetails::class.java)))

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(lettuceConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build()
    }
}