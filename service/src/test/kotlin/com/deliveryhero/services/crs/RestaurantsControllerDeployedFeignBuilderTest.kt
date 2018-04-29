package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.auth.AuthController
import com.deliveryhero.services.crs.api.restaurant.RestaurantsController
import com.deliveryhero.services.crs.util.FeignAuthInterceptor
import com.deliveryhero.services.crs.util.RequestToken
import feign.Client
import feign.Contract
import feign.codec.Decoder
import feign.codec.Encoder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import feign.Feign
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.cloud.openfeign.FeignClientsConfiguration
import org.springframework.test.context.ContextConfiguration

@RunWith(SpringRunner::class)
@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(classes = [FeignClientsConfiguration::class])
/*
@WebMvcTest needed, otherwise:
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type
'org.springframework.boot.autoconfigure.http.HttpMessageConverters' available: expected at least 1 bean which qualifies as
autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
 */
@WebMvcTest
class RestaurantsControllerDeployedFeignBuilderTest {

    @Value("\${username}") private lateinit var username: String
    @Value("\${password}") private lateinit var password: String

    @Value("\${crs.port}") private lateinit var port: String
    @Value("\${crs.url}") private lateinit var url: String

    private lateinit var authController: AuthController
    private lateinit var restaurantsController: RestaurantsController

    @Autowired private lateinit var decoder: Decoder
    @Autowired private lateinit var encoder: Encoder
    private var client: Client = Client.Default(null, null)
    @Autowired private lateinit var contract: Contract

    @Before
    fun setupFeignClients() {

        val builder = Feign.builder().client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)

        //complete endpoint paths are needed here, when setting up manually
        authController = builder.target<AuthController>(AuthController::class.java,
                url + ":" + port + "/crs/" + AuthController.PATH)
        restaurantsController = builder
                .requestInterceptor(FeignAuthInterceptor())
                .target<RestaurantsController>(RestaurantsController::class.java,
                        url + ":" + port + "/crs/" + RestaurantsController.PATH)
    }

    @Test
    fun testLoginAndGetRestaurants() {

        val token = authController.login(username, password)
        RequestToken.set(token)

        val restaurants = restaurantsController.getAll()
        println(restaurants)
    }
}