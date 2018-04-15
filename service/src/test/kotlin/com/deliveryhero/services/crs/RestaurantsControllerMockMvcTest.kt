package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.Person
import com.deliveryhero.services.crs.api.Token
import com.deliveryhero.services.crs.auth.TokenFilter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Assert
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

    @Test
    fun testNullUsername_byType() {

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/auth/form")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("password", password))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        val responseBody = resultActions.andReturn().response.contentAsString
        println("response: $responseBody")

//        val error = jacksonObjectMapper().readValue<Error>(responseBody)
//        Assert.assertEquals("validation-error", error.message)
    }

    @Test
    fun testCreatePerson() {

        val person = Person("simon", "dr", 67)

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/person")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper().writeValueAsString(person)))
                .andExpect(MockMvcResultMatchers.status().isOk)

        val responseBody = resultActions.andReturn().response.contentAsString
        println(responseBody)

        val createdPerson = jacksonObjectMapper().readValue<Person>(responseBody)
        Assert.assertEquals(person.name, createdPerson.name)
        Assert.assertEquals(person.title, createdPerson.title)
        Assert.assertEquals(person.age, createdPerson.age)
    }

    @Test
    fun testPersonNameNull_byType() {

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/person")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"age":67}"""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        val responseBody = resultActions.andReturn().response.contentAsString
        println("response: $responseBody")

//        val error = jacksonObjectMapper().readValue<Error>(responseBody)
//        Assert.assertEquals("validation-error", error.message)
    }

    @Test
    fun testPersonTitleNull_byAnnotation() {

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/person")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"simon", "age":67}"""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        val responseBody = resultActions.andReturn().response.contentAsString
        println("response: $responseBody")

//        val error = jacksonObjectMapper().readValue<Error>(responseBody)
//        Assert.assertEquals("validation-error", error.message)
    }

    @Test
    fun testPersonAgeNegative_byAnnotation() {

        val resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/1/person")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"simon", "title":"no-dr-:-(", "age":-1}"""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

        val responseBody = resultActions.andReturn().response.contentAsString
        println("response: $responseBody")

//        val error = jacksonObjectMapper().readValue<Error>(responseBody)
//        Assert.assertEquals("validation-error", error.message)
    }
}