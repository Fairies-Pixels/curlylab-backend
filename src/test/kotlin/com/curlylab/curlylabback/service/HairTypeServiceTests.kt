package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.HairType
import com.curlylab.curlylabback.model.Porosity
import com.curlylab.curlylabback.model.Thickness
import com.curlylab.curlylabback.repository.HairTypeRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNull

class HairTypeServiceTests {

    private lateinit var repository: HairTypeRepository
    private lateinit var service: HairTypeService

    @BeforeEach
    fun setUp() {
        repository = mockk()
        service = HairTypeService(repository)
    }

    @Test
    fun `getById должен вернуть запись с таким id, если такая запись существует`() {
        val id = UUID.randomUUID()
        val hairType = HairType(id, Porosity.LOW, null, Thickness.MEDIUM)

        every { repository.get(id) } returns hairType

        val result = service.getById(id)

        assertEquals(hairType, result)
        verify(exactly = 1) { repository.get(id) }
    }

    @Test
    fun `getById должен вернуть null, если такой записи нет`() {
        val id = UUID.randomUUID()

        every { repository.get(id) } returns null

        val result = service.getById(id)

        assertNull(result)
        verify { repository.get(id) }
    }

    @Test
    fun `create должен вернуть true при добавлении записи`() {
        val hairType = HairType(UUID.randomUUID(), Porosity.HIGH, true, Thickness.THICK)

        every { repository.add(hairType) } returns true

        val result = service.create(hairType)

        assertTrue(result)
        verify { repository.add(hairType) }
    }

    @Test
    fun `create должен вернуть false, если такая запись уже есть`() {
        val hairType = HairType(UUID.randomUUID(), Porosity.MEDIUM, false, null)

        every { repository.add(hairType) } returns false

        val result = service.create(hairType)

        assertFalse(result)
        verify { repository.add(hairType) }
    }

    @Test
    fun `update должен вернуть обновленную запись`() {
        val id = UUID.randomUUID()
        val updated = HairType(id, null, false, Thickness.MEDIUM)

        every { repository.edit(id, updated) } returns updated

        val result = service.update(id, updated)

        assertEquals(updated, result)
        verify { repository.edit(id, updated) }
    }

    @Test
    fun `update должен вернуть null если запись не существует`() {
        val id = UUID.randomUUID()
        val entity = HairType(id, Porosity.LOW, null, Thickness.MEDIUM)

        every { repository.edit(id, entity) } returns null

        val result = service.update(id, entity)

        assertNull(result)
        verify { repository.edit(id, entity) }
    }

    @Test
    fun `delete должен вернуть true при успешном удалении`() {
        val id = UUID.randomUUID()

        every { repository.delete(id) } returns true

        val result = service.delete(id)

        assertTrue(result)
        verify { repository.delete(id) }
    }

    @Test
    fun `delete должен вернуть false при неуспешном удалении`() {
        val id = UUID.randomUUID()

        every { repository.delete(id) } returns false

        val result = service.delete(id)

        assertFalse(result)
        verify { repository.delete(id) }
    }
}