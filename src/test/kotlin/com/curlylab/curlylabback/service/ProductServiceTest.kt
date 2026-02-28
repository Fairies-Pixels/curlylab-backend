package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.Product
import com.curlylab.curlylabback.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class ProductServiceTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var productService: ProductService

    @BeforeEach
    fun setUp() {
        productRepository = mockk()
        productService = ProductService(productRepository)
    }

    @Test
    fun `получение всех продуктов возвращает список из репозитория`() {
        val expectedProducts = listOf(
            Product(UUID.randomUUID(), "Продукт 1", "Описание 1", listOf("тег1")),
            Product(UUID.randomUUID(), "Продукт 2", "Описание 2", listOf("тег2"))
        )

        every { productRepository.getAll() } returns expectedProducts

        val result = productService.getAll()

        assertEquals(expectedProducts, result)
        verify { productRepository.getAll() }
    }

    @Test
    fun `получение продукта по существующему id возвращает продукт`() {
        val id = UUID.randomUUID()
        val expectedProduct = Product(id, "Тестовый продукт", "Описание", listOf("тег1"))

        every { productRepository.get(id) } returns expectedProduct

        val result = productService.getById(id)

        assertEquals(expectedProduct, result)
        verify { productRepository.get(id) }
    }

    @Test
    fun `получение продукта по несуществующему id возвращает null`() {
        val id = UUID.randomUUID()

        every { productRepository.get(id) } returns null

        val result = productService.getById(id)

        assertNull(result)
        verify { productRepository.get(id) }
    }

    @Test
    fun `удаление продукта вызывает соответствующий метод репозитория`() {
        val id = UUID.randomUUID()

        every { productRepository.delete(id) } returns true

        val result = productService.delete(id)

        assertTrue(result)
        verify { productRepository.delete(id) }
    }
}