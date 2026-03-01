package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.UserProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.time.LocalDateTime
import java.util.*

class UserProviderRepositoryImplTests {

    private lateinit var jdbc: JdbcTemplate
    private lateinit var repository: UserProviderRepositoryImpl

    @BeforeEach
    fun setUp() {
        jdbc = mockk()
        repository = UserProviderRepositoryImpl(jdbc)
    }

    @Test
    fun `find возвращает null если сущность не найдена`() {
        every {
            jdbc.query(any<String>(), any<RowMapper<UserProvider>>(), any<String>(), any<String>())
        } returns emptyList()

        val result = repository.find("google", "nonexistent")

        assertNull(result)
    }

    @Test
    fun `add возвращает true при успешной вставке`() {
        val provider = UserProvider(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            provider = "google",
            providerUserId = "12345",
            email = "test@mail.com",
            createdAt = LocalDateTime.now()
        )

        every { jdbc.update(any<String>(), any(), any(), any(), any(), any()) } returns 1

        val result = repository.add(provider)

        assertTrue(result)

        verify {
            jdbc.update(
                match { it.contains("INSERT INTO user_providers") },
                provider.id,
                provider.userId,
                provider.provider,
                provider.providerUserId,
                provider.email
            )
        }
    }

    @Test
    fun `find возвращает сущность если она существует`() {
        val provider = UserProvider(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            provider = "google",
            providerUserId = "12345",
            email = "test@mail.com",
            createdAt = LocalDateTime.now()
        )

        every { jdbc.query(any<String>(), any<RowMapper<UserProvider>>(), any<String>(), any<String>()) } returns listOf(provider)

        val result = repository.find("google", "12345")

        assertNotNull(result)
        assertEquals(provider, result)

        verify {
            jdbc.query(
                match { it.contains("SELECT * FROM user_providers") },
                any<RowMapper<UserProvider>>(),
                "google",
                "12345"
            )
        }
    }

    @Test
    fun `add возвращает false при неудачной вставке`() {
        val provider = UserProvider(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            provider = "google",
            providerUserId = "12345",
            email = "test@mail.com",
            createdAt = LocalDateTime.now()
        )

        every {
            jdbc.update(any<String>(), any(), any(), any(), any(), any())
        } returns 0

        val result = repository.add(provider)

        assertFalse(result)
    }
}