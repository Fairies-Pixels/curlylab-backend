package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.model.UserAuth
import com.curlylab.curlylabback.model.UserRefreshToken
import com.curlylab.curlylabback.repository.RefreshTokenRepository
import com.curlylab.curlylabback.repository.UserAuthRepository
import com.curlylab.curlylabback.repository.UserRepository
import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.security.crypto.bcrypt.BCrypt.gensalt
import org.springframework.security.crypto.bcrypt.BCrypt.hashpw
import java.time.LocalDateTime
import java.util.*

class AuthServiceTests {

    private lateinit var userRepo: UserRepository
    private lateinit var userAuthRepo: UserAuthRepository
    private lateinit var refreshRepo: RefreshTokenRepository
    private lateinit var jwt: JwtService

    private lateinit var service: AuthService

    @BeforeEach
    fun setup() {
        userRepo = mockk()
        userAuthRepo = mockk()
        refreshRepo = mockk()
        jwt = mockk()

        service = AuthService(userRepo, userAuthRepo, refreshRepo, jwt)
    }

    @Test
    fun `если емэйл уже зарегистрирован, должна выбрасываться ошибка`() {
        every { userAuthRepo.findByEmail("test@mail.com") } returns mockk()

        assertThrows<EmailAlreadyExistsException> {
            service.register("test@mail.com", "123", "user")
        }
    }

    @Test
    fun `при регистации должен создаться пользователь и вернуться токен`() {
        every { userAuthRepo.findByEmail(any()) } returns null
        every { userRepo.add(any()) } returns true
        every { userAuthRepo.add(any()) } returns true
        every { refreshRepo.save(any()) } returns true
        every { jwt.generateAccessToken(any()) } returns "access-token"

        val tokens = service.register("new@mail.com", "password", "username")

        assertEquals("access-token", tokens.access)
        Assertions.assertNotNull(tokens.refresh)

        verify { userRepo.add(any()) }
        verify { userAuthRepo.add(any()) }
        verify { refreshRepo.save(any()) }
    }

    @Test
    fun `при регистрации пароль должен хэшироваться`() {
        every { userAuthRepo.findByEmail(any()) } returns null
        every { userRepo.add(any()) } returns true
        every { userAuthRepo.add(any()) } returns true
        every { refreshRepo.save(any()) } returns true
        every { jwt.generateAccessToken(any()) } returns "access-token"

        service.register("new@mail.com", "password", "username")

        verify {
            userAuthRepo.add(match {
                it.passwordHash != "password"
            })
        }
    }

    @Test
    fun `при авторизации через несуществующий емэйл должна выбрасываться ошибка`() {
        every { userAuthRepo.findByEmail(any()) } returns null

        assertThrows<IllegalArgumentException> {
            service.login("missing@mail.com", "123")
        }
    }

    @Test
    fun `при авторизации с существующим емэйлом и неправильным паролем, должна выбрасываться ошибка`() {
        val userId = UUID.randomUUID()

        val auth = UserAuth(
            userId = userId,
            email = "test@mail.com",
            passwordHash = hashpw("correct", gensalt()),
            salt = "",
            createdAt = LocalDateTime.now()
        )

        every { userAuthRepo.findByEmail(any()) } returns auth

        assertThrows<IllegalArgumentException> {
            service.login("test@mail.com", "wrong")
        }
    }

    @Test
    fun `при корректной авторизации должен вернуться токен`() {
        val userId = UUID.randomUUID()

        val auth = UserAuth(
            userId = userId,
            email = "test@mail.com",
            passwordHash = hashpw("password", gensalt()),
            salt = "",
            createdAt = LocalDateTime.now()
        )

        val user = User(
            id = userId,
            username = "user",
            createdAt = LocalDateTime.now()
        )

        every { userAuthRepo.findByEmail(any()) } returns auth
        every { userRepo.get(userId) } returns user
        every { refreshRepo.save(any()) } returns true
        every { jwt.generateAccessToken(user) } returns "access-token"

        val tokens = service.login("test@mail.com", "password")

        assertEquals("access-token", tokens.access)
        Assertions.assertNotNull(tokens.refresh)

        verify { refreshRepo.save(any()) }
    }

    @Test
    fun `если токен недействителен, при выходе должна вернуться ошибка`() {
        every { refreshRepo.findValid(any()) } returns null

        assertThrows<IllegalArgumentException> {
            service.logout("bad-token")
        }
    }

    @Test
    fun `при выходе, токен должен аннулироваться`() {
        val token = UserRefreshToken(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            token = "refresh",
            expiresAt = LocalDateTime.now().plusDays(1),
            createdAt = LocalDateTime.now(),
            revoked = false
        )

        every { refreshRepo.findValid("refresh") } returns token
        every { refreshRepo.revoke(token.id) } returns true

        service.logout("refresh")

        verify { refreshRepo.revoke(token.id) }
    }
}