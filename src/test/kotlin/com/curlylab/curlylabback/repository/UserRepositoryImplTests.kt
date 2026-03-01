package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.User
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.time.LocalDateTime
import java.util.*
import kotlin.test.*

class UserRepositoryImplTests {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var repository: UserRepositoryImpl

    @BeforeEach
    fun setUp() {
        jdbcTemplate = mockk()
        repository = UserRepositoryImpl(jdbcTemplate)
    }

    @Test
    fun `get должен вернуть пользователя, если запись найдена`() {
        val id = UUID.randomUUID()
        val user = User(id, "вика", LocalDateTime.now(), "image.png")

        every {
            jdbcTemplate.query(any<String>(), any<RowMapper<User>>(), id)
        } returns listOf(user)

        val result = repository.get(id)

        assertEquals(user, result)
        verify { jdbcTemplate.query(any<String>(), any<RowMapper<User>>(), id) }
    }

    @Test
    fun `get должен вернуть null, если запись не найдена`() {
        val id = UUID.randomUUID()

        every {
            jdbcTemplate.query(any<String>(), any<RowMapper<User>>(), id)
        } returns emptyList()

        val result = repository.get(id)

        assertNull(result)
    }

    @Test
    fun `add должен вернуть true, если вставка успешна`() {
        val user = User(UUID.randomUUID(), "вика", LocalDateTime.now(), "image.png")

        every {
            jdbcTemplate.update(any<String>(), any(), any(), any(), any())
        } returns 1

        val result = repository.add(user)

        assertTrue(result)
    }

    @Test
    fun `add должен вернуть false, если вставка не выполнена`() {
        val user = User(UUID.randomUUID(), "вика", LocalDateTime.now(), "image.png")

        every {
            jdbcTemplate.update(any<String>(), any(), any(), any(), any())
        } returns 0

        val result = repository.add(user)

        assertFalse(result)
    }

    @Test
    fun `delete должен вернуть true, если удаление успешно`() {
        val id = UUID.randomUUID()

        every {
            jdbcTemplate.update(any<String>(), id)
        } returns 1

        val result = repository.delete(id)

        assertTrue(result)
    }

    @Test
    fun `delete должен вернуть false, если запись не удалена`() {
        val id = UUID.randomUUID()

        every {
            jdbcTemplate.update(any<String>(), id)
        } returns 0

        val result = repository.delete(id)

        assertFalse(result)
    }

    @Test
    fun `edit должен вернуть обновленного пользователя, если обновление успешно`() {
        val id = UUID.randomUUID()
        val updatedUser = User(id, "anonymous2007", LocalDateTime.now(), "new.png")

        every {
            jdbcTemplate.update(any<String>(), any(), any(), any(), id)
        } returns 1

        every {
            jdbcTemplate.query(any<String>(), any<RowMapper<User>>(), id)
        } returns listOf(updatedUser)

        val result = repository.edit(id, updatedUser)

        assertEquals(updatedUser, result)
        verify { jdbcTemplate.update(any<String>(), any(), any(), any(), id) }
        verify { jdbcTemplate.query(any<String>(), any<RowMapper<User>>(), id) }
    }

    @Test
    fun `edit должен вернуть null, если обновление не произошло`() {
        val id = UUID.randomUUID()
        val user = User(id, "вика", LocalDateTime.now(), "image.png")

        every {
            jdbcTemplate.update(any<String>(), any(), any(), any(), id)
        } returns 0

        val result = repository.edit(id, user)

        assertNull(result)
        verify { jdbcTemplate.update(any<String>(), any(), any(), any(), id) }
        verify(exactly = 0) {
            jdbcTemplate.query(any<String>(), any<RowMapper<User>>(), id)
        }
    }
}