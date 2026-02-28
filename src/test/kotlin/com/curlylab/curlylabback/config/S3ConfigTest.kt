package com.curlylab.curlylabback.config

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import kotlin.test.assertTrue

class S3ConfigTest {

    @Test
    fun `создание s3 клиента с корректными настройками`() {
        val config = S3Config()

        val testCases = listOf(
            Triple("test-access-key", "test-secret-key", "https://test.endpoint"),
            Triple("a".repeat(20), "b".repeat(40), "https://s3.amazonaws.com"),
            Triple("valid-key", "valid-secret", "http://localhost:9000"),

            Triple(
                "key-with-hyphens", "secret_with_underscores", "https://storage.yandexcloud.net"
            ),
            Triple("", "secret", "https://test.endpoint"),
            Triple("key", "", "https://test.endpoint"),
        )

        testCases.forEachIndexed { index, (accessKey, secretKey, endpoint) ->
            ReflectionTestUtils.setField(config, "accessKey", accessKey)
            ReflectionTestUtils.setField(config, "secretKey", secretKey)
            ReflectionTestUtils.setField(config, "endpoint", endpoint)
            ReflectionTestUtils.setField(config, "region", "test-region")
            ReflectionTestUtils.setField(config, "bucket", "test-bucket")

            try {
                val s3Client = config.s3Client()
                assertNotNull(s3Client, "Клиент не должен быть null для кейса $index")
            } catch (e: Exception) {
                if (accessKey.isEmpty() || secretKey.isEmpty()) {
                    assertTrue(e is IllegalArgumentException || e is RuntimeException)
                } else {
                    throw e
                }
            }
        }
    }

    @Test
    fun `получение имени бакета`() {
        val config = S3Config()
        val expectedBucket = "test-bucket"

        ReflectionTestUtils.setField(config, "bucket", expectedBucket)

        val result = config.bucketName()

        assertNotNull(result)
        assert(result == expectedBucket)
    }
}