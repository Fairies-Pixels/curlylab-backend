package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.repository.UserRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

class UserServiceTests {

    private lateinit var userRepo: UserRepository
    private lateinit var s3Service: S3Service

    private lateinit var service: UserService

    @BeforeEach
    fun setup() {
        userRepo = mockk()
        s3Service = mockk()

        service = UserService(userRepo, s3Service)
    }

    @Test
    fun `если пользователь не найден при загрузке аватара, должна выбрасываться ошибка`() {
        val userId = UUID.randomUUID()
        val file = mockk<MultipartFile>()

        every { userRepo.get(userId) } returns null

        assertThrows<IllegalArgumentException> {
            service.uploadUserAvatar(userId, file)
        }
    }

    @Test
    fun `при загрузке нового аватара старый должен удаляться`() {
        val userId = UUID.randomUUID()
        val file = mockk<MultipartFile>()

        val user = User(
            id = userId,
            username = "vika",
            createdAt = LocalDateTime.now(),
            imageUrl = "http://old-url"
        )

        every { userRepo.get(userId) } returns user
        every { s3Service.extractFileKeyFromUrl("http://old-url") } returns "old-key"
        every { s3Service.deleteFile("old-key") } returns Unit
        every { s3Service.uploadFile(userId, file) } returns "new-key"
        every { s3Service.getFileUrl("new-key") } returns "http://new-url"
        every { userRepo.edit(eq(userId), any()) } returns user.copy(imageUrl = "http://new-url")

        val result = service.uploadUserAvatar(userId, file)

        assertEquals("http://new-url", result)

        verify { s3Service.deleteFile("old-key") }
        verify {
            userRepo.edit(eq(userId), match {
                it.imageUrl == "http://new-url"
            })
        }
    }

    @Test
    fun `если старого аватара нет, должен просто загрузиться новый`() {
        val userId = UUID.randomUUID()
        val file = mockk<MultipartFile>()

        val user = User(
            id = userId,
            username = "vika",
            createdAt = LocalDateTime.now(),
            imageUrl = null
        )

        every { userRepo.get(userId) } returns user
        every { s3Service.uploadFile(userId, file) } returns "new-key"
        every { s3Service.getFileUrl("new-key") } returns "http://new-url"
        every { userRepo.edit(eq(userId), any()) } returns user.copy(imageUrl = "http://new-url")

        val result = service.uploadUserAvatar(userId, file)

        assertEquals("http://new-url", result)

        verify(exactly = 0) { s3Service.deleteFile(any()) }
        verify { userRepo.edit(eq(userId), match { it.imageUrl == "http://new-url" }) }
    }

    @Test
    fun `если пользователь не найден при удалении аватара, должна выбрасываться ошибка`() {
        val userId = UUID.randomUUID()

        every { userRepo.get(userId) } returns null

        assertThrows<IllegalArgumentException> {
            service.deleteUserAvatar(userId)
        }
    }

    @Test
    fun `если у пользователя нет аватара, должна выбрасываться ошибка`() {
        val userId = UUID.randomUUID()

        val user = User(
            id = userId,
            username = "vika",
            createdAt = LocalDateTime.now(),
            imageUrl = null
        )

        every { userRepo.get(userId) } returns user

        assertThrows<IllegalArgumentException> {
            service.deleteUserAvatar(userId)
        }
    }

    @Test
    fun `при удалении аватара файл должен удалиться и imageUrl стать null`() {
        val userId = UUID.randomUUID()

        val user = User(
            id = userId,
            username = "vika",
            createdAt = LocalDateTime.now(),
            imageUrl = "http://image-url"
        )

        every { userRepo.get(userId) } returns user
        every { s3Service.extractFileKeyFromUrl("http://image-url") } returns "file-key"
        every { s3Service.deleteFile("file-key") } returns Unit
        every { userRepo.edit(eq(userId), any()) } returns user.copy(imageUrl = null)

        service.deleteUserAvatar(userId)

        verify { s3Service.deleteFile("file-key") }
        verify {
            userRepo.edit(eq(userId), match { it.imageUrl == null })
        }
    }
}