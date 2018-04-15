package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.AuthController
import com.deliveryhero.services.crs.api.RestaurantsController
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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import feign.Feign
import org.springframework.cloud.openfeign.FeignClientsConfiguration
import org.springframework.context.annotation.Import

@RunWith(SpringRunner::class)
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
    @Autowired private lateinit var client: Client
    @Autowired private lateinit var contract: Contract

    // for @BeforeClass, see https://stackoverflow.com/questions/35554076/how-do-i-manage-unit-test-resources-in-kotlin-such-as-starting-stopping-a-datab
    @Before
    fun setupFeignClients() {

        val builder = Feign.builder().client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)

        authController = builder.target<AuthController>(AuthController::class.java, url + ":" + port)
        restaurantsController = builder
                .requestInterceptor(FeignAuthInterceptor())
                .target<RestaurantsController>(RestaurantsController::class.java, url + ":" + port)
    }

    @Test
    fun testLoginAndGetRestaurants() {

        /*
        at org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient.execute
        ....
        Caused by: com.netflix.client.ClientException: Load balancer does not have available server for client: localhost
            at com.netflix.loadbalancer.LoadBalancerContext.getServerFromLoadBalancer(LoadBalancerContext.java:483)
            at com.netflix.loadbalancer.reactive.LoadBalancerCommand$1.call(LoadBalancerCommand.java:184)
         */

        val token = authController.login(username, password)
        RequestToken.set(token)

        val restaurants = restaurantsController.getAll()
        println(restaurants)
    }
}