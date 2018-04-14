package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.Token
import com.deliveryhero.services.crs.auth.TokenFilter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class RestaurantsControllerMockMvcTest {

    @Autowired private lateinit var context: WebApplicationContext
    @Value("\${username}") private lateinit var username: String
    @Value("\${password}") private lateinit var password: String

    private lateinit var mvc: MockMvc

    @Before
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilters<DefaultMockMvcBuilder>(TokenFilter()).build()
    }

    @Test
    fun testLoginAndGetRestaurants() {

        val tokenResponse = mvc.perform(MockMvcRequestBuilders.post("/api/1/auth/form")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("password", password))
                .andExpect(MockMvcResultMatchers.status().isOk)

        // https://github.com/FasterXML/jackson-module-kotlin !!!
        val token = jacksonObjectMapper().readValue<Token>(tokenResponse.andReturn().response.contentAsString)

        val restaurantsResponse = mvc.perform(MockMvcRequestBuilders.get("/api/1/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.token))
                .andExpect(MockMvcResultMatchers.status().isOk)

        println(restaurantsResponse.andReturn().response.contentAsString)
    }
}