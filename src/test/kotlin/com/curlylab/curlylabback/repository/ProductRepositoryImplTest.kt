package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.Product
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.util.UUID

class ProductRepositoryImplTest {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var repository: ProductRepositoryImpl

    @BeforeEach
    fun setUp() {
        jdbcTemplate = mockk()
        repository = ProductRepositoryImpl(jdbcTemplate)
    }

    @Test
    fun `получение всех продуктов возвращает список`() {
        val expectedProducts = listOf(
            Product(UUID.randomUUID(), "Продукт 1", "Описание 1", listOf("тег1")),
            Product(UUID.randomUUID(), "Продукт 2", "Описание 2", listOf("тег2", "тег3"))
        )

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<Product>>()
            )
        } returns expectedProducts

        val result = repository.getAll()

        assertEquals(expectedProducts.size, result.size)
        assertEquals(expectedProducts[0].name, result[0].name)
        verify {
            jdbcTemplate.query(
                "SELECT id, name, description, tags, image_url FROM products",
                any<RowMapper<Product>>()
            )
        }
    }

    @Test
    fun `получение продукта по существующему id возвращает продукт`() {
        val id = UUID.randomUUID()
        val expectedProduct = Product(id, "Продукт 1", "Описание 1", listOf("тег1"))

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<Product>>(),
                any<UUID>()
            )
        } returns listOf(expectedProduct)

        val result = repository.get(id)

        assertNotNull(result)
        assertEquals(expectedProduct.name, result?.name)
        verify {
            jdbcTemplate.query(
                "SELECT id, name, description, tags, image_url FROM products WHERE id = ?",
                any<RowMapper<Product>>(),
                id
            )
        }
    }

    @Test
    fun `получение продукта по несуществующему id возвращает null`() {
        val id = UUID.randomUUID()

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<Product>>(),
                any<UUID>()
            )
        } returns emptyList()

        val result = repository.get(id)

        assertNull(result)
    }

    @Test
    fun `добавление продукта возвращает true при успешной вставке`() {
        val product =
            Product(UUID.randomUUID(), "Новый продукт", "Описание", listOf("тег1", "тег2"))

        every { jdbcTemplate.update(any<String>(), any(), any(), any(), any(), any()) } returns 1

        val result = repository.add(product)

        assertTrue(result)
        verify {
            jdbcTemplate.update(
                any<String>(),
                product.id,
                product.name,
                product.description,
                "тег1,тег2",
                product.imageUrl
            )
        }
    }

    @Test
    fun `добавление продукта возвращает false при неудачной вставке`() {
        val product = Product(UUID.randomUUID(), "Новый продукт", "Описание", listOf("тег1"))

        every { jdbcTemplate.update(any<String>(), any(), any(), any(), any(), any()) } returns 0

        val result = repository.add(product)

        assertFalse(result)
    }

    @Test
    fun `удаление продукта возвращает true при успешном удалении`() {
        val id = UUID.randomUUID()

        every { jdbcTemplate.update(any<String>(), any<UUID>()) } returns 1

        val result = repository.delete(id)

        assertTrue(result)
        verify { jdbcTemplate.update("DELETE FROM products WHERE id = ?", id) }
    }

    @Test
    fun `удаление продукта возвращает false при неудачном удалении`() {
        val id = UUID.randomUUID()

        every { jdbcTemplate.update(any<String>(), any<UUID>()) } returns 0

        val result = repository.delete(id)

        assertFalse(result)
    }

    @Test
    fun `редактирование продукта возвращает обновленный продукт при успехе`() {
        val id = UUID.randomUUID()
        val updatedProduct =
            Product(id, "Обновленное имя", "Обновленное описание", listOf("тег1", "тег2"))

        every {
            jdbcTemplate.update(
                any<String>(),
                any(),
                any(),
                any(),
                any(),
                any<UUID>()
            )
        } returns 1
        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<Product>>(),
                any<UUID>()
            )
        } returns listOf(updatedProduct)

        val result = repository.edit(id, updatedProduct)

        assertNotNull(result)
        assertEquals(updatedProduct.name, result?.name)
        verify {
            jdbcTemplate.update(
                any<String>(),
                updatedProduct.name,
                updatedProduct.description,
                "тег1,тег2",
                updatedProduct.imageUrl,
                id
            )
        }
    }

    @Test
    fun `редактирование продукта возвращает null при неудачном обновлении`() {
        val id = UUID.randomUUID()
        val updatedProduct = Product(id, "Обновленное имя", "Обновленное описание", listOf("тег1"))

        every {
            jdbcTemplate.update(
                any<String>(),
                any(),
                any(),
                any(),
                any(),
                any<UUID>()
            )
        } returns 0

        val result = repository.edit(id, updatedProduct)

        assertNull(result)
    }
}