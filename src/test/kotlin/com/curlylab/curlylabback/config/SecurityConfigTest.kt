package com.curlylab.curlylabback.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@WebMvcTest(TestController::class)
@Import(SecurityConfig::class)
class SecurityConfigTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `GET запрос должен проходить без авторизации`() {
        mockMvc.perform(get("/test"))
            .andExpect(status().isOk)
            .andExpect(content().string("OK"))
    }

    @Test
    fun `POST запрос должен проходить без CSRF токена`() {
        mockMvc.perform(
            post("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"test":"value"}""")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `сессия не должна созаться (не создаются куки)`() {
        mockMvc.perform(get("/test"))
            .andExpect(status().isOk)
            .andExpect(cookie().doesNotExist("JSESSIONID"))
    }
}


@RestController
class TestController {

    @GetMapping("/test")
    fun get() = "OK"

    @PostMapping("/test")
    fun post() = "OK"
}