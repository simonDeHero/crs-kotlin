package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.auth.AuthController
import com.deliveryhero.services.crs.api.restaurant.RestaurantsController
import com.deliveryhero.services.crs.util.FeignAuthInterceptor
import com.deliveryhero.services.crs.util.RequestToken
import feign.Client
import feign.Contract
import feign.codec.Decoder
import feign.codec.Encoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import feign.Feign
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.cloud.openfeign.FeignClientsConfiguration
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties", properties = ["server.port=\${crs.port}"])
@Import(FeignClientsConfiguration::class)
class RestaurantsControllerInMemoryFeignBuilderTest {

    @Value("\${username}") private lateinit var username: String
    @Value("\${password}") private lateinit var password: String

    @Value("\${crs.port}") private lateinit var port: String
    @Value("\${crs.url}") private lateinit var url: String

    @Autowired private lateinit var authController: AuthController
    @Autowired private lateinit var restaurantsController: RestaurantsController

    @Autowired private lateinit var decoder: Decoder
    @Autowired private lateinit var encoder: Encoder
    @Autowired private lateinit var contract: Contract

    @BeforeAll
    fun setupFeignClients() {

        /*
         explicitly use the default client as otherwise the Ribbon LoadBalancerFeignClient would be used, which needs
         more configuration
         */
        val builder = Feign.builder().client(Client.Default(null, null))
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)

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