package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.auth.AuthController
import com.deliveryhero.services.crs.api.restaurant.RestaurantsController
import com.deliveryhero.services.crs.util.FeignAuthInterceptor
import com.deliveryhero.services.crs.util.RequestToken
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties", properties = ["server.port=\${crs.port}"])
@EnableFeignClients
class RestaurantsControllerInMemoryFeignTest {

    @TestConfiguration
    class Config {
        @Bean fun feignAuthInterceptor(): FeignAuthInterceptor = FeignAuthInterceptor()
    }

    @FeignClient(name = "authControllerClient", url = "\${crs.url}:\${crs.port}", path = AuthController.PATH)
    interface AuthControllerClient: AuthController

    @FeignClient(name = "restaurantsControllerClient", url = "\${crs.url}:\${crs.port}", path = RestaurantsController.PATH)
    interface RestaurantsControllerClient: RestaurantsController

    @Value("\${username}") private lateinit var username: String
    @Value("\${password}") private lateinit var password: String

//    @Autowired private lateinit var authController: AuthController
//    @Autowired private lateinit var restaurantsController: RestaurantsController

    @Autowired private lateinit var authController: AuthControllerClient
    @Autowired private lateinit var restaurantsController: RestaurantsControllerClient

    @Test
    fun testLoginAndGetRestaurants() {

        val token = authController.login(username, password)
        RequestToken.set(token)

        val restaurants = restaurantsController.getAll()
        println(restaurants)
    }
}