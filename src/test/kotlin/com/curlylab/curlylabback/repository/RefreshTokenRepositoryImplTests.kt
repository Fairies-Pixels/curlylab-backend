package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.UserRefreshToken
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.time.LocalDateTime
import java.util.UUID

class RefreshTokenRepositoryImplTest {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var repository: RefreshTokenRepositoryImpl

    @BeforeEach
    fun setUp() {
        jdbcTemplate = mockk()
        repository = RefreshTokenRepositoryImpl(jdbcTemplate)
    }

    @Test
    fun `сохранение токена возвращает true при успешной вставке`() {
        val token = UserRefreshToken(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            token = "refresh-token",
            expiresAt = LocalDateTime.now().plusDays(1),
            createdAt = LocalDateTime.now(),
            revoked = false
        )

        every {
            jdbcTemplate.update(any<String>(), any(), any(), any(), any(), any(), any())
        } returns 1

        val result = repository.save(token)

        assertTrue(result)

        verify {
            jdbcTemplate.update(
                any<String>(),
                token.id,
                token.userId,
                token.token,
                token.expiresAt,
                token.createdAt,
                token.revoked
            )
        }
    }

    @Test
    fun `сохранение токена возвращает false при неудачной вставке`() {
        val token = UserRefreshToken(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            token = "refresh-token",
            expiresAt = LocalDateTime.now().plusDays(1),
            createdAt = LocalDateTime.now(),
            revoked = false
        )

        every {
            jdbcTemplate.update(any<String>(), any(), any(), any(), any(), any(), any())
        } returns 0

        val result = repository.save(token)

        assertFalse(result)
    }

    @Test
    fun `findValid возвращает токен если найден`() {
        val tokenString = "valid-token"

        val expected = UserRefreshToken(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            token = tokenString,
            expiresAt = LocalDateTime.now().plusDays(1),
            createdAt = LocalDateTime.now(),
            revoked = false
        )

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<UserRefreshToken>>(),
                tokenString
            )
        } returns listOf(expected)

        val result = repository.findValid(tokenString)

        assertNotNull(result)
        assertEquals(expected.id, result?.id)

        verify {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<UserRefreshToken>>(),
                tokenString
            )
        }
    }

    @Test
    fun `findValid возвращает null если токен не найден`() {
        val tokenString = "missing-token"

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<UserRefreshToken>>(),
                tokenString
            )
        } returns emptyList()

        val result = repository.findValid(tokenString)

        assertNull(result)
    }

    @Test
    fun `revoke возвращает true при успешном обновлении`() {
        val tokenId = UUID.randomUUID()

        every {
            jdbcTemplate.update(any<String>(), tokenId)
        } returns 1

        val result = repository.revoke(tokenId)

        assertTrue(result)

        verify {
            jdbcTemplate.update(
                "UPDATE user_refresh_tokens SET revoked = true WHERE id = ?",
                tokenId
            )
        }
    }

    @Test
    fun `revoke возвращает false при неудачном обновлении`() {
        val tokenId = UUID.randomUUID()

        every {
            jdbcTemplate.update(any<String>(), tokenId)
        } returns 0

        val result = repository.revoke(tokenId)

        assertFalse(result)
    }
}