package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.auth.Token
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
class RestaurantsControllerInMemoryRestTemplateTest {

    @Value("\${username}") private lateinit var username: String
    @Value("\${password}") private lateinit var password: String
    @Autowired private lateinit var restTemplate: TestRestTemplate

    @Test
    fun testLoginAndGetRestaurants() {

        val loginHeaders = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            accept = listOf(MediaType.APPLICATION_JSON)
        }
        val map = LinkedMultiValueMap<String, String>().apply {
            add("username", username)
            add("password", password)
        }
        val loginRequest = HttpEntity<MultiValueMap<String, String>>(map, loginHeaders)
        val token = restTemplate.postForObject("/api/1/auth/form", loginRequest, Token::class.java)

        val restaurantsHeaders = HttpHeaders().apply {
            accept = listOf(MediaType.APPLICATION_JSON)
            set("Authorization", "Bearer " + token.token)
        }
        val restaurantsRequest = HttpEntity<Any?>(restaurantsHeaders)
        val restaurantsResponse = restTemplate.exchange<String>(
                "/api/1/restaurants", HttpMethod.GET, restaurantsRequest, String::class.java)

        println(restaurantsResponse.body)
    }
}