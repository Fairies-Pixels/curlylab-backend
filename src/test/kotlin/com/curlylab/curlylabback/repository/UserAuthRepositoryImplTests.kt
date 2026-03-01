package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.UserAuth
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.time.LocalDateTime
import java.util.*
import kotlin.test.*

class UserAuthRepositoryImplTests {

    private lateinit var jdbc: JdbcTemplate
    private lateinit var repository: UserAuthRepositoryImpl

    @BeforeEach
    fun setUp() {
        jdbc = mockk()
        repository = UserAuthRepositoryImpl(jdbc)
    }

    @Test
    fun `findByEmail должен вернуть пользователя, если запись найдена`() {
        val email = "test@mail.com"
        val userAuth = UserAuth(
            userId = UUID.randomUUID(),
            email = email,
            passwordHash = "hash",
            salt = "salt",
            createdAt = LocalDateTime.now()
        )

        every {
            jdbc.query(any<String>(), any<RowMapper<UserAuth>>(), email)
        } returns listOf(userAuth)

        val result = repository.findByEmail(email)

        assertEquals(userAuth, result)
        verify { jdbc.query(any<String>(), any<RowMapper<UserAuth>>(), email) }
    }

    @Test
    fun `findByEmail должен вернуть null, если запись не найдена`() {
        val email = "notfound@mail.com"

        every {
            jdbc.query(any<String>(), any<RowMapper<UserAuth>>(), email)
        } returns emptyList()

        val result = repository.findByEmail(email)

        assertNull(result)
    }

    @Test
    fun `add должен вернуть true, если вставка успешна`() {
        val userAuth = UserAuth(
            userId = UUID.randomUUID(),
            email = "test@mail.com",
            passwordHash = "hash",
            salt = "salt",
            createdAt = LocalDateTime.now()
        )

        every {
            jdbc.update(any<String>(), any(), any(), any(), any())
        } returns 1

        val result = repository.add(userAuth)

        assertTrue(result)
        verify {
            jdbc.update(any<String>(),
                userAuth.userId,
                userAuth.email,
                userAuth.passwordHash,
                userAuth.salt
            )
        }
    }

    @Test
    fun `add должен вернуть false, если вставка не выполнена`() {
        val userAuth = UserAuth(
            userId = UUID.randomUUID(),
            email = "fail@mail.com",
            passwordHash = "hash",
            salt = "salt",
            createdAt = LocalDateTime.now()
        )

        every {
            jdbc.update(any<String>(), any(), any(), any(), any())
        } returns 0

        val result = repository.add(userAuth)

        assertFalse(result)
    }
}