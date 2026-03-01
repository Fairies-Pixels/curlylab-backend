package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.service.*
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.*

class AuthControllerTests {

    private lateinit var authService: AuthService
    private lateinit var googleAuthService: GoogleAuthService
    private lateinit var controller: AuthController

    @BeforeEach
    fun setUp() {
        authService = mockk()
        googleAuthService = mockk()
        controller = AuthController(authService, googleAuthService)
    }

    @Test
    fun `регистрация успешна возвращает статус OK и токены`() {
        val tokens = Tokens(
            access = "access",
            refresh = "refresh"
        )

        every { authService.register(any(), any(), any()) } returns tokens

        val response = controller.register(
            RegisterRequest("test@mail.com", "pass", "vika")
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tokens, response.body)
        verify { authService.register("test@mail.com", "pass", "vika") }
    }

    @Test
    fun `регистрация с существующим email возвращает CONFLICT`() {
        every {
            authService.register(any(), any(), any())
        } throws EmailAlreadyExistsException("email already exists")

        val response = controller.register(
            RegisterRequest("test@mail.com", "pass", "vika")
        )

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertTrue((response.body as Map<*, *>)["error"].toString().contains("already exists"))
    }

    @Test
    fun `логин успешен возвращает статус OK и токены`() {
        val tokens = Tokens(
            access = "access",
            refresh = "refresh"
        )

        every { authService.login(any(), any()) } returns tokens

        val response = controller.login(
            LoginRequest("test@mail.com", "pass")
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tokens, response.body)
    }

    @Test
    fun `логин с неверными данными возвращает BAD REQUEST`() {
        every { authService.login(any(), any()) } throws IllegalArgumentException()

        val response = controller.login(
            LoginRequest("test@mail.com", "wrong")
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(
            mapOf("error" to "Invalid credentials"),
            response.body
        )
    }

    @Test
    fun `google авторизация успешна возвращает статус OK`() {
        val tokens = Tokens(
            access = "access",
            refresh = "refresh"
        )

        every { googleAuthService.loginWithGoogle(any()) } returns tokens

        val response = controller.google(
            GoogleRequest("validToken")
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tokens, response.body)
    }

    @Test
    fun `google авторизация с неверным токеном возвращает UNAUTHORIZED`() {
        every {
            googleAuthService.loginWithGoogle(any())
        } throws IllegalArgumentException()

        val response = controller.google(
            GoogleRequest("badToken")
        )

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals(
            mapOf("error" to "Invalid Google token"),
            response.body
        )
    }

    @Test
    fun `google авторизация с другой ошибкой возвращает BAD REQUEST`() {
        every {
            googleAuthService.loginWithGoogle(any())
        } throws RuntimeException("google failed")

        val response = controller.google(
            GoogleRequest("badToken")
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(
            mapOf("error" to "google failed"),
            response.body
        )
    }

    @Test
    fun `logout успешен возвращает статус OK`() {
        every { authService.logout(any()) } just Runs

        val response = controller.logout(
            LogoutRequest("refreshToken")
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(
            mapOf("message" to "Logged out successfully"),
            response.body
        )
        verify { authService.logout("refreshToken") }
    }

    @Test
    fun `logout с ошибкой возвращает BAD REQUEST`() {
        every { authService.logout(any()) } throws IllegalArgumentException("bad token")

        val response = controller.logout(
            LogoutRequest("refreshToken")
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(
            mapOf("error" to "bad token"),
            response.body
        )
    }
}