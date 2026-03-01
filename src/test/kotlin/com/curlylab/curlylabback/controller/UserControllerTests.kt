package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.service.UserService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import java.time.LocalDateTime
import java.util.*
import kotlin.test.*

class UserControllerTests {

    private lateinit var userService: UserService
    private lateinit var controller: UserController

    @BeforeEach
    fun setUp() {
        userService = mockk()
        controller = UserController(userService)
    }

    @Test
    fun `получение пользователя по существующему id возвращает OK`() {
        val id = UUID.randomUUID()
        val user = User(id, "vika", LocalDateTime.now(), "img.png")

        every { userService.getById(id) } returns user

        val response = controller.getUser(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(user, response.body)
    }

    @Test
    fun `получение пользователя по несуществующему id возвращает NOT FOUND`() {
        val id = UUID.randomUUID()

        every { userService.getById(id) } returns null

        val response = controller.getUser(id)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `создание пользователя успешно возвращает OK`() {
        val user = User(UUID.randomUUID(), "vika", LocalDateTime.now(), null)

        every { userService.create(user) } returns true

        val response = controller.createUser(user)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("User created", response.body)
    }

    @Test
    fun `создание пользователя с ошибкой возвращает BAD REQUEST`() {
        val user = User(UUID.randomUUID(), "vika", LocalDateTime.now(), null)

        every { userService.create(user) } returns false

        val response = controller.createUser(user)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Failed to create user", response.body)
    }

    @Test
    fun `обновление пользователя успешно возвращает OK`() {
        val id = UUID.randomUUID()
        val user = User(id, "anonymous", LocalDateTime.now(), null)

        every { userService.update(id, user) } returns user

        val response = controller.updateUser(id, user)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(user, response.body)
    }

    @Test
    fun `обновление несуществующего пользователя возвращает NOT FOUND`() {
        val id = UUID.randomUUID()
        val user = User(id, "vika", LocalDateTime.now(), null)

        every { userService.update(id, user) } returns null

        val response = controller.updateUser(id, user)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `удаление пользователя успешно возвращает OK`() {
        val id = UUID.randomUUID()

        every { userService.delete(id) } returns true

        val response = controller.deleteUser(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("User deleted", response.body)
    }

    @Test
    fun `удаление несуществующего пользователя возвращает NOT FOUND`() {
        val id = UUID.randomUUID()

        every { userService.delete(id) } returns false

        val response = controller.deleteUser(id)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `загрузка аватара успешно возвращает OK и url`() {
        val id = UUID.randomUUID()
        val file = MockMultipartFile("file", "avatar.png", "image/png", byteArrayOf(1,2))
        val url = "http://image.url/avatar.png"

        every { userService.uploadUserAvatar(id, file) } returns url

        val response = controller.uploadUserAvatar(id, file)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mapOf("imageUrl" to url), response.body)
    }

    @Test
    fun `загрузка аватара с превышением размера возвращает PAYLOAD TOO LARGE`() {
        val id = UUID.randomUUID()
        val file = MockMultipartFile("file", "big.png", "image/png", byteArrayOf())

        every {
            userService.uploadUserAvatar(id, file)
        } throws IllegalArgumentException("File size exceeds 5MB")

        val response = controller.uploadUserAvatar(id, file)

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.statusCode)
        assertEquals(
            mapOf("error" to "File size exceeds 5MB"),
            response.body
        )
    }

    @Test
    fun `загрузка аватара с другой ошибкой возвращает BAD REQUEST`() {
        val id = UUID.randomUUID()
        val file = MockMultipartFile("file", "bad.png", "image/png", byteArrayOf())

        every {
            userService.uploadUserAvatar(id, file)
        } throws IllegalArgumentException("Invalid file")

        val response = controller.uploadUserAvatar(id, file)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(mapOf("error" to "Invalid file"), response.body)
    }

    @Test
    fun `загрузка аватара с неожиданной ошибкой возвращает BAD REQUEST`() {
        val id = UUID.randomUUID()
        val file = MockMultipartFile("file", "bad.png", "image/png", byteArrayOf())

        every {
            userService.uploadUserAvatar(id, file)
        } throws RuntimeException("storage error")

        val response = controller.uploadUserAvatar(id, file)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertTrue(response.body!!["error"]!!.contains("storage error"))
    }

    @Test
    fun `удаление аватара успешно возвращает OK`() {
        val id = UUID.randomUUID()

        every { userService.deleteUserAvatar(id) } just Runs

        val response = controller.deleteUserAvatar(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(
            mapOf("message" to "Avatar deleted successfully"),
            response.body
        )
    }

    @Test
    fun `удаление аватара с ошибкой возвращает BAD REQUEST`() {
        val id = UUID.randomUUID()

        every { userService.deleteUserAvatar(id) } throws IllegalArgumentException("no avatar")

        val response = controller.deleteUserAvatar(id)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(
            mapOf("error" to "no avatar"),
            response.body
        )
    }
}