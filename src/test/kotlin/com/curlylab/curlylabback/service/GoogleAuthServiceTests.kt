package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.*
import com.curlylab.curlylabback.repository.*
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.util.*

class GoogleAuthServiceTests {

    private lateinit var userRepo: UserRepository
    private lateinit var providerRepo: UserProviderRepository
    private lateinit var refreshRepo: RefreshTokenRepository
    private lateinit var jwt: JwtService

    private lateinit var service: GoogleAuthService

    private val clientId = "test-client-id"

    @BeforeEach
    fun setup() {
        userRepo = mockk()
        providerRepo = mockk()
        refreshRepo = mockk()
        jwt = mockk()

        service = GoogleAuthService(
            userRepo,
            providerRepo,
            refreshRepo,
            jwt,
            clientId
        )
    }

    @Test
    fun `ошибка при недействительном google токене`() {
        mockkConstructor(GoogleIdTokenVerifier::class)

        every {
            anyConstructed<GoogleIdTokenVerifier>()
                .verify(any<String>())
        } returns null

        assertThrows<IllegalArgumentException> {
            service.loginWithGoogle("bad-token")
        }
    }

    @Test
    fun `должен успешно авторизовывать существующего google пользователя`() {

        mockGoogleVerification()

        val userId = UUID.randomUUID()

        val provider = UserProvider(
            userId = userId,
            provider = "GOOGLE",
            providerUserId = "google-id",
            email = "test@gmail.com",
            createdAt = LocalDateTime.now()
        )

        val user = User(
            id = userId,
            username = "test",
            createdAt = LocalDateTime.now()
        )

        every { providerRepo.find("GOOGLE", "google-id") } returns provider
        every { userRepo.get(userId) } returns user
        every { jwt.generateAccessToken(user) } returns "access"
        every { refreshRepo.save(any()) } returns true

        val tokens = service.loginWithGoogle("valid")

        assertEquals("access", tokens.access)
        Assertions.assertNotNull(tokens.refresh)

        verify { refreshRepo.save(any()) }
    }

    @Test
    fun `должен создавать нового google пользователя, если его нет`() {

        mockGoogleVerification()

        every { providerRepo.find("GOOGLE", "google-id") } returns null
        every { userRepo.add(any()) } returns true
        every { providerRepo.add(any()) } returns true
        every { jwt.generateAccessToken(any()) } returns "access"
        every { refreshRepo.save(any()) } returns true

        val tokens = service.loginWithGoogle("valid")

        assertEquals("access", tokens.access)

        verify { userRepo.add(any()) }
        verify { providerRepo.add(any()) }
    }

    @Test
    fun `ошибка при ненайденном пользователе после того как найден провайдер`() {

        mockGoogleVerification()

        val provider = UserProvider(
            userId = UUID.randomUUID(),
            provider = "GOOGLE",
            providerUserId = "google-id",
            email = "test@gmail.com",
            createdAt = LocalDateTime.now()
        )

        every { providerRepo.find("GOOGLE", "google-id") } returns provider
        every { userRepo.get(provider.userId) } returns null

        assertThrows<IllegalStateException> {
            service.loginWithGoogle("valid")
        }
    }

    private fun mockGoogleVerification() {
        mockkConstructor(GoogleIdTokenVerifier::class)

        val payload = Payload().apply {
            subject = "google-id"
            set("email", "test@gmail.com")
            set("picture", "http://image.url")
        }

        val idToken = mockk<GoogleIdToken>()
        every { idToken.payload } returns payload

        every {
            anyConstructed<GoogleIdTokenVerifier>()
                .verify(any<String>())
        } returns idToken
    }
}