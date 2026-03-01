package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.HairType
import com.curlylab.curlylabback.model.Porosity
import com.curlylab.curlylabback.model.Thickness
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.util.*
import kotlin.test.*

class HairTypeRepositoryImplTests {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var repository: HairTypeRepositoryImpl

    @BeforeEach
    fun setUp() {
        jdbcTemplate = mockk()
        repository = HairTypeRepositoryImpl(jdbcTemplate)
    }

    @Test
    fun `get должен вернуть сущность, если запись найдена`() {
        val id = UUID.randomUUID()
        val hairType = HairType(id, Porosity.HIGH, true, Thickness.THICK)

        every {
            jdbcTemplate.query(any<String>(), any<RowMapper<HairType>>(), id)
        } returns listOf(hairType)

        val result = repository.get(id)

        assertEquals(hairType, result)
        verify { jdbcTemplate.query(any<String>(), any<RowMapper<HairType>>(), id) }
    }

    @Test
    fun `get должен вернуть null, если запись не найдена`() {
        val id = UUID.randomUUID()

        every {
            jdbcTemplate.query(any<String>(), any<RowMapper<HairType>>(), id)
        } returns emptyList()

        val result = repository.get(id)

        assertNull(result)
    }

    @Test
    fun `add должен вернуть true, если вставка успешна`() {
        val entity = HairType(
            UUID.randomUUID(),
            Porosity.LOW,
            false,
            Thickness.THIN
        )

        every {
            jdbcTemplate.update(any<String>(), any(), any(), any(), any())
        } returns 1

        val result = repository.add(entity)

        assertTrue(result)
    }

    @Test
    fun `add должен вернуть false, если вставка не выполнена`() {
        val entity = HairType(
            UUID.randomUUID(),
            Porosity.MEDIUM,
            true,
            Thickness.MEDIUM
        )

        every {
            jdbcTemplate.update(any<String>(), any(), any(), any(), any())
        } returns 0

        val result = repository.add(entity)

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
    fun `edit должен выполнить upsert и вернуть обновленную сущность`() {
        val id = UUID.randomUUID()
        val entity = HairType(id, Porosity.HIGH, true, Thickness.THICK)

        every {
            jdbcTemplate.update(any<String>(), any(), any(), any(), any())
        } returns 1

        every {
            jdbcTemplate.query(any<String>(), any<RowMapper<HairType>>(), id)
        } returns listOf(entity)

        val result = repository.edit(id, entity)

        assertEquals(entity, result)
        verify { jdbcTemplate.update(any<String>(), any(), any(), any(), any()) }
        verify { jdbcTemplate.query(any<String>(), any<RowMapper<HairType>>(), id) }
    }
}
