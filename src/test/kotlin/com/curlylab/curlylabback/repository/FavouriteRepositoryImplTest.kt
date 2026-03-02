package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.Favourite
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.util.*

class FavouriteRepositoryImplTest {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var repository: FavouriteRepositoryImpl

    @BeforeEach
    fun setUp() {
        jdbcTemplate = mockk()
        repository = FavouriteRepositoryImpl(jdbcTemplate)
    }

    @Test
    fun `успешное получение всех избранных продуктов пользователя`() {
        val userId = UUID.randomUUID()
        val expectedFavourites = listOf(
            Favourite(userId = userId, productId = UUID.randomUUID()),
            Favourite(userId = userId, productId = UUID.randomUUID())
        )

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<Favourite>>(),
                any<UUID>()
            )
        } returns expectedFavourites

        val result = repository.getALLForUser(userId)

        assertEquals(expectedFavourites.size, result?.size)
        assertEquals(expectedFavourites[0].productId, result?.get(0)?.productId)
        verify {
            jdbcTemplate.query(
                "SELECT user_id, product_id FROM favourites WHERE user_id = ?",
                any<RowMapper<Favourite>>(),
                userId
            )
        }
    }

    @Test
    fun `получение пустого списка избранного для пользователя`() {
        val userId = UUID.randomUUID()

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<Favourite>>(),
                any<UUID>()
            )
        } returns emptyList()

        val result = repository.getALLForUser(userId)

        assertNotNull(result)
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun `успешное получение записи избранного по ID пользователя и продукта`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val expectedFavourite = Favourite(userId = userId, productId = productId)

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<Favourite>>(),
                any<UUID>(),
                any<UUID>()
            )
        } returns listOf(expectedFavourite)

        val result = repository.getByUserAndProduct(userId, productId)

        assertNotNull(result)
        assertEquals(expectedFavourite.productId, result?.productId)
        verify {
            jdbcTemplate.query(
                "SELECT user_id, product_id FROM favourites WHERE user_id = ? AND product_id = ?",
                any<RowMapper<Favourite>>(),
                userId,
                productId
            )
        }
    }

    @Test
    fun `попытка получения несуществующей записи`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<Favourite>>(),
                any<UUID>(),
                any<UUID>()
            )
        } returns emptyList()

        val result = repository.getByUserAndProduct(userId, productId)

        assertNull(result)
    }

    @Test
    fun `добавление записи в избранное возвращает false при неудаче`() {
        val favourite = Favourite(
            userId = UUID.randomUUID(),
            productId = UUID.randomUUID()
        )

        every {
            jdbcTemplate.update(
                any<String>(),
                any<UUID>(),
                any<UUID>()
            )
        } returns 0

        val result = repository.add(favourite)

        assertFalse(result)
    }

    @Test
    fun `успешное удаление записи из избранного`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        every {
            jdbcTemplate.update(
                any<String>(),
                any<UUID>(),
                any<UUID>()
            )
        } returns 1

        val result = repository.deleteByUserAndProduct(userId, productId)

        assertTrue(result)
        verify {
            jdbcTemplate.update(
                "DELETE FROM favourites WHERE user_id = ? AND product_id = ?",
                userId,
                productId
            )
        }
    }

    @Test
    fun `попытка удаления несуществующей записи`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        every {
            jdbcTemplate.update(
                any<String>(),
                any<UUID>(),
                any<UUID>()
            )
        } returns 0

        val result = repository.deleteByUserAndProduct(userId, productId)

        assertFalse(result)
    }

    @Test
    fun `успешное удаление всех записей избранного по ID продукта`() {
        val productId = UUID.randomUUID()

        every {
            jdbcTemplate.update(
                any<String>(),
                any<UUID>()
            )
        } returns 2

        val result = repository.delete(productId)

        assertTrue(result)
        verify {
            jdbcTemplate.update(
                "DELETE FROM favourites WHERE product_id = ?",
                productId
            )
        }
    }

    @Test
    fun `удаление записей по ID продукта возвращает false если ничего не удалено`() {
        val productId = UUID.randomUUID()

        every {
            jdbcTemplate.update(
                any<String>(),
                any<UUID>()
            )
        } returns 0

        val result = repository.delete(productId)

        assertFalse(result)
    }

    @Test
    fun `получение одной записи по ID возвращает первую запись`() {
        val userId = UUID.randomUUID()
        val expectedFavourite = Favourite(userId = userId, productId = UUID.randomUUID())

        every {
            jdbcTemplate.query(
                any<String>(),
                any<RowMapper<Favourite>>(),
                any<UUID>()
            )
        } returns listOf(expectedFavourite)

        val result = repository.get(userId)

        assertNotNull(result)
        assertEquals(expectedFavourite.productId, result?.productId)
        verify {
            jdbcTemplate.query(
                "SELECT user_id, product_id FROM favourites WHERE user_id = ?",
                any<RowMapper<Favourite>>(),
                userId
            )
        }
    }

    @Test
    fun `редактирование записи возвращает null (не реализовано)`() {
        val id = UUID.randomUUID()
        val entity = Favourite(userId = UUID.randomUUID(), productId = UUID.randomUUID())

        val result = repository.edit(id, entity)

        assertNull(result)
    }
}