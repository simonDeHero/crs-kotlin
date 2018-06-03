package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.Person
import com.deliveryhero.services.crs.api.auth.Token
import com.deliveryhero.services.crs.api.error.Error
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class RestaurantsControllerMockMvcTest(
        @Autowired private var context: WebApplicationContext,
        @Value("\${username}") private var username: String,
        @Value("\${password}") private var password: String
) {

    private lateinit var mvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    private lateinit var testToken: Token

    @BeforeAll
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity()).build()
        // https://github.com/FasterXML/jackson-module-kotlin !!!
        objectMapper = jacksonObjectMapper().registerModules(Jdk8Module(), JavaTimeModule())

        val tokenResponse = mvc.perform(MockMvcRequestBuilders.post("/api/1/auth/form")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("password", password))
                .andExpect(MockMvcResultMatchers.status().isOk)

        testToken = objectMapper.readValue<Token>(tokenResponse.andReturn().response.contentAsString)
    }

    @Test
    fun `login and get restaurants`() {

        val restaurantsResponse = mvc.perform(MockMvcRequestBuilders.get("/api/1/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + testToken.token))
                .andExpect(MockMvcResultMatchers.status().isOk)

        println(restaurantsResponse.andReturn().response.contentAsString)

        // get cached value
        mvc.perform(MockMvcRequestBuilders.get("/api/1/restaurants")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + testToken.token))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun testNullUsername_byType() {

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/auth/form")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("password", password))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        val responseBody = resultActions.andReturn().response.contentAsString
        println("response: $responseBody")

        val error = objectMapper.readValue<Error>(responseBody)
        Assertions.assertEquals("Model validation failed.", error.message)
    }

    @Test
    fun testCreatePerson() {

        val person = Person("simon", "dr", 67)

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/person")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person))
                .header("Authorization", "Bearer " + testToken.token))
                .andExpect(MockMvcResultMatchers.status().isOk)

        val responseBody = resultActions.andReturn().response.contentAsString
        println(responseBody)

        val createdPerson = objectMapper.readValue<Person>(responseBody)
        Assertions.assertEquals(person.name, createdPerson.name)
        Assertions.assertEquals(person.title, createdPerson.title)
        Assertions.assertEquals(person.age, createdPerson.age)
    }

    @Test
    fun testPersonNameNull_byType() {

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/person")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"age":67}""")
                .header("Authorization", "Bearer " + testToken.token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        val responseBody = resultActions.andReturn().response.contentAsString
        println("response: $responseBody")

        val error = objectMapper.readValue<Error>(responseBody)
        Assertions.assertEquals("Model validation failed.", error.message)
    }

    @Test
    fun testPersonTitleNull_byAnnotation() {

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/person")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"simon", "age":67}""")
                .header("Authorization", "Bearer " + testToken.token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        val responseBody = resultActions.andReturn().response.contentAsString
        println("response: $responseBody")

        val error = objectMapper.readValue<Error>(responseBody)
        Assertions.assertEquals("Model validation failed.", error.message)
    }

    @Test
    fun testPersonAgeNegative_byAnnotation() {

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/person")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"simon", "title":"no-dr-:-(", "age":-1}""")
                .header("Authorization", "Bearer " + testToken.token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        val responseBody = resultActions.andReturn().response.contentAsString
        println("response: $responseBody")

        val error = objectMapper.readValue<Error>(responseBody)
        Assertions.assertEquals("Model validation failed.", error.message)
    }
}