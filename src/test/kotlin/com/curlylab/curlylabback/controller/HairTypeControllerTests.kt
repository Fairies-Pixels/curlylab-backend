package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.*
import com.curlylab.curlylabback.service.HairTypeService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HairTypeControllerTest {

    private lateinit var hairTypeService: HairTypeService
    private lateinit var hairTypeController: HairTypeController

    @BeforeEach
    fun setUp() {
        hairTypeService = mockk()
        hairTypeController = HairTypeController(hairTypeService)
    }

    @Test
    fun `получение типа волос по существующему id возвращает статус OK и запись`() {
        val id = UUID.randomUUID()
        val expected = HairType(id, Porosity.HIGH, true, Thickness.THICK)

        every { hairTypeService.getById(id) } returns expected

        val response = hairTypeController.getHairType(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
        verify { hairTypeService.getById(id) }
    }

    @Test
    fun `создание типа волос при успешном сохранении возвращает статус OK`() {
        val hairType = HairType(UUID.randomUUID(), Porosity.LOW, false, Thickness.THIN)

        every { hairTypeService.create(hairType) } returns true

        val response = hairTypeController.createHairType(hairType)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("HairType created", response.body)
        verify { hairTypeService.create(hairType) }
    }

    @Test
    fun `создание типа волос при ошибке возвращает статус BAD REQUEST`() {
        val hairType = HairType(UUID.randomUUID(), Porosity.MEDIUM, true, Thickness.MEDIUM)

        every { hairTypeService.create(hairType) } returns false

        val response = hairTypeController.createHairType(hairType)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Failed to create HairType", response.body)
        verify { hairTypeService.create(hairType) }
    }

    @Test
    fun `обновление существующего типа волос возвращает статус OK и обновленную сущность`() {
        val id = UUID.randomUUID()
        val updated = HairType(id, Porosity.HIGH, false, Thickness.THICK)

        every { hairTypeService.update(id, updated) } returns updated

        val response = hairTypeController.updateHairType(id, updated)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated, response.body)
        verify { hairTypeService.update(id, updated) }
    }

    @Test
    fun `обновление несуществующего типа волос возвращает статус NOT FOUND`() {
        val id = UUID.randomUUID()
        val hairType = HairType(id, Porosity.LOW, false, Thickness.THIN)

        every { hairTypeService.update(id, hairType) } returns null

        val response = hairTypeController.updateHairType(id, hairType)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        verify { hairTypeService.update(id, hairType) }
    }

    @Test
    fun `удаление существующего типа волос возвращает статус OK`() {
        val id = UUID.randomUUID()

        every { hairTypeService.delete(id) } returns true

        val response = hairTypeController.deleteHairType(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("HairType deleted", response.body)
        verify { hairTypeService.delete(id) }
    }

    @Test
    fun `удаление несуществующего типа волос возвращает статус NOT FOUND`() {
        val id = UUID.randomUUID()

        every { hairTypeService.delete(id) } returns false

        val response = hairTypeController.deleteHairType(id)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        verify { hairTypeService.delete(id) }
    }
}