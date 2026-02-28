package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.Product
import com.curlylab.curlylabback.service.ProductService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

class ProductControllerTest {

    private lateinit var productService: ProductService
    private lateinit var productController: ProductController

    @BeforeEach
    fun setUp() {
        productService = mockk()
        productController = ProductController(productService)
    }

    @Test
    fun `получение всех продуктов возвращает список с статусом OK`() {
        val expectedProducts = listOf(
            Product(UUID.randomUUID(), "Продукт 1", "Описание 1", listOf("тег1")),
            Product(UUID.randomUUID(), "Продукт 2", "Описание 2", listOf("тег2"))
        )

        every { productService.getAll() } returns expectedProducts

        val response = productController.getAllProducts()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedProducts, response.body)
        verify { productService.getAll() }
    }

    @Test
    fun `получение продукта по существующему id возвращает продукт с статусом OK`() {
        val id = UUID.randomUUID()
        val expectedProduct = Product(id, "Тестовый продукт", "Описание", listOf("тег1"))

        every { productService.getById(id) } returns expectedProduct

        val response = productController.getProduct(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedProduct, response.body)
        verify { productService.getById(id) }
    }

    @Test
    fun `получение продукта по несуществующему id возвращает статус NOT FOUND`() {
        val id = UUID.randomUUID()

        every { productService.getById(id) } returns null

        val response = productController.getProduct(id)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(null, response.body)
        verify { productService.getById(id) }
    }
}