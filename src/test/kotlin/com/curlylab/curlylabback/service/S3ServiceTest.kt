package com.curlylab.curlylabback.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.PutObjectResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockMultipartFile
import java.io.File
import java.util.UUID

class S3ServiceTest {

    private lateinit var s3Client: AmazonS3
    private lateinit var s3Service: S3Service

    private val bucketName = "test-bucket"

    @BeforeEach
    fun setUp() {
        s3Client = mockk()
        s3Service = spyk(S3Service(s3Client, bucketName), recordPrivateCalls = true)
    }

    @Test
    fun `загрузка файла успешно возвращает имя файла`() {
        val userId = UUID.randomUUID()
        val fileContent = "image content".toByteArray()
        val multipartFile = MockMultipartFile("file", "avatar.jpg", "image/jpeg", fileContent)

        every { s3Service["validateFile"](multipartFile) } returns Unit

        val tempFile = File.createTempFile("temp", null)
        tempFile.writeBytes(fileContent)
        every { s3Service["convertMultiPartToFile"](multipartFile) } returns tempFile

        val mockResult = mockk<PutObjectResult>()
        every { s3Client.putObject(any<PutObjectRequest>()) } returns mockResult

        val expectedFileName = "avatars/$userId/$userId.jpg"

        val result = s3Service.uploadFile(userId, multipartFile)

        assertEquals(expectedFileName, result)
        verify { s3Client.putObject(match { it.bucketName == bucketName && it.key == expectedFileName && it.file == tempFile }) }
        assertFalse(tempFile.exists())
    }

    @Test
    fun `загрузка пустого файла выбрасывает исключение`() {
        val userId = UUID.randomUUID()
        val emptyFile = MockMultipartFile("file", "empty.jpg", "image/jpeg", ByteArray(0))

        every { s3Service["validateFile"](emptyFile) } throws IllegalArgumentException("File is empty")

        val ex = assertThrows<IllegalArgumentException> {
            s3Service.uploadFile(userId, emptyFile)
        }
        assertEquals("File is empty", ex.message)
    }

    @Test
    fun `загрузка файла с недопустимым размером выбрасывает исключение`() {
        val userId = UUID.randomUUID()
        val largeContent = ByteArray(6 * 1024 * 1024)
        val largeFile = MockMultipartFile("file", "large.jpg", "image/jpeg", largeContent)

        every { s3Service["validateFile"](largeFile) } throws IllegalArgumentException("File size exceeds 5MB")

        val ex = assertThrows<IllegalArgumentException> {
            s3Service.uploadFile(userId, largeFile)
        }
        assertEquals("File size exceeds 5MB", ex.message)
    }

    @Test
    fun `загрузка файла с недопустимым типом выбрасывает исключение`() {
        val userId = UUID.randomUUID()
        val fileContent = "image content".toByteArray()
        val invalidFile = MockMultipartFile("file", "avatar.gif", "image/gif", fileContent)

        every { s3Service["validateFile"](invalidFile) } throws IllegalArgumentException("Only JPEG and PNG images are allowed")

        val ex = assertThrows<IllegalArgumentException> {
            s3Service.uploadFile(userId, invalidFile)
        }
        assertEquals("Only JPEG and PNG images are allowed", ex.message)
    }

    @Test
    fun `загрузка png файла сохраняет png расширение`() {
        val userId = UUID.randomUUID()
        val fileContent = "image content".toByteArray()
        val multipartFile = MockMultipartFile("file", "avatar.png", "image/png", fileContent)

        every { s3Service["validateFile"](multipartFile) } returns Unit

        val tempFile = File.createTempFile("temp", null)
        tempFile.writeBytes(fileContent)
        every { s3Service["convertMultiPartToFile"](multipartFile) } returns tempFile

        val mockResult = mockk<PutObjectResult>()
        every { s3Client.putObject(any<PutObjectRequest>()) } returns mockResult

        val result = s3Service.uploadFile(userId, multipartFile)

        assertTrue(result.endsWith(".png"))
    }

    @Test
    fun `загрузка файла без расширения использует jpg по умолчанию`() {
        val userId = UUID.randomUUID()
        val fileContent = "image content".toByteArray()
        val multipartFile = MockMultipartFile("file", "avatar", "image/jpeg", fileContent)

        every { s3Service["validateFile"](multipartFile) } returns Unit

        val tempFile = File.createTempFile("temp", null)
        tempFile.writeBytes(fileContent)
        every { s3Service["convertMultiPartToFile"](multipartFile) } returns tempFile

        val mockResult = mockk<PutObjectResult>()
        every { s3Client.putObject(any<PutObjectRequest>()) } returns mockResult

        val result = s3Service.uploadFile(userId, multipartFile)

        assertTrue(result.endsWith(".jpg"))
    }

    @Test
    fun `удаление файла вызывает s3 клиент`() {
        val fileKey = "avatars/test/test.jpg"

        every { s3Client.deleteObject(any<DeleteObjectRequest>()) } returns Unit

        s3Service.deleteFile(fileKey)

        verify { s3Client.deleteObject(match { it.bucketName == bucketName && it.key == fileKey }) }
    }

    @Test
    fun `получение url файла возвращает корректный url`() {
        val fileKey = "avatars/test/test.jpg"
        val expectedUrl = "http://localhost/$bucketName/$fileKey"

        every { s3Client.getUrl(bucketName, fileKey) } returns java.net.URL(expectedUrl)

        val url = s3Service.getFileUrl(fileKey)

        assertEquals(expectedUrl, url)
    }

    @Test
    fun `извлечение ключа из url возвращает корректный ключ`() {
        val fileKey = "avatars/test/test.jpg"
        val imageUrl = "https://storage.yandexcloud.net/$bucketName/$fileKey"

        val extractedKey = s3Service.extractFileKeyFromUrl(imageUrl)

        assertEquals(fileKey, extractedKey)
    }

    @Test
    fun `извлечение ключа из null или пустого url возвращает null`() {
        assertNull(s3Service.extractFileKeyFromUrl(null))
        assertNull(s3Service.extractFileKeyFromUrl(""))
    }
}