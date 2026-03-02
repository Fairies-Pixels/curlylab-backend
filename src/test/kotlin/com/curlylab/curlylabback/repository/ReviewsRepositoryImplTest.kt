package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.MarkAndReview
import com.curlylab.curlylabback.model.Reviews
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.time.LocalDateTime
import java.util.UUID

class ReviewsRepositoryImplTest {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var repository: ReviewsRepositoryImpl

    @BeforeEach
    fun setUp() {
        jdbcTemplate = mockk()
        repository = ReviewsRepositoryImpl(jdbcTemplate)
    }

    @Test
    fun `получение всех отзывов для продукта возвращает список`() {
        val productId = UUID.randomUUID()
        val reviews = listOf(
            Reviews(UUID.randomUUID(), UUID.randomUUID(), productId, LocalDateTime.now(), 5, "Отлично"),
            Reviews(UUID.randomUUID(), UUID.randomUUID(), productId, LocalDateTime.now(), 4, "Хорошо")
        )

        every {
            jdbcTemplate.query(any<String>(), any<RowMapper<Reviews>>(), productId)
        } returns reviews

        val result = repository.getAllForProduct(productId)

        assertEquals(2, result.size)
        verify {
            jdbcTemplate.query(
                match { it.contains("FROM reviews") },
                any<RowMapper<Reviews>>(),
                productId
            )
        }
    }

    @Test
    fun `получение отзыва по id возвращает отзыв`() {
        val id = UUID.randomUUID()
        val review = Reviews(id, UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), 5, "Текст")

        every {
            jdbcTemplate.query(any<String>(), any<RowMapper<Reviews>>(), id)
        } returns listOf(review)

        val result = repository.get(id)

        assertNotNull(result)
        assertEquals(5, result?.mark)
    }

    @Test
    fun `получение несуществующего отзыва возвращает null`() {
        val id = UUID.randomUUID()

        every {
            jdbcTemplate.query(any<String>(), any<RowMapper<Reviews>>(), id)
        } returns emptyList()

        val result = repository.get(id)

        assertNull(result)
    }

    @Test
    fun `добавление отзыва возвращает true при корректной оценке`() {
        val review = Reviews(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now(),
            4,
            "Нормально"
        )

        every { jdbcTemplate.update(any<String>(), any(), any(), any(), any(), any(), any()) } returns 1

        val result = repository.add(review)

        assertTrue(result)
    }

    @Test
    fun `добавление отзыва возвращает false если оценка null`() {
        val review = Reviews(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now(),
            null,
            "Без оценки"
        )

        val result = repository.add(review)

        assertFalse(result)
    }

    @Test
    fun `добавление отзыва возвращает false если оценка вне диапазона`() {
        val review = Reviews(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now(),
            10,
            "Плохо"
        )

        val result = repository.add(review)

        assertFalse(result)
    }

    @Test
    fun `удаление возвращает true при успешном удалении`() {
        val id = UUID.randomUUID()

        every { jdbcTemplate.update(any<String>(), id) } returns 1

        val result = repository.delete(id)

        assertTrue(result)
        verify { jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", id) }
    }

    @Test
    fun `удаление возвращает false при неудаче`() {
        val id = UUID.randomUUID()

        every { jdbcTemplate.update(any<String>(), id) } returns 0

        val result = repository.delete(id)

        assertFalse(result)
    }

    @Test
    fun `редактирование отзыва успешно в пределах 24 часов`() {
        val id = UUID.randomUUID()
        val oldReview = Reviews(
            id,
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now().minusHours(1),
            3,
            "Старый текст"
        )

        val updatedReview = oldReview.copy(mark = 5, review = "Новый текст")

        every { jdbcTemplate.query(any<String>(), any<RowMapper<Reviews>>(), id) } returns listOf(oldReview) andThen listOf(updatedReview)
        every { jdbcTemplate.update(any<String>(), any(), any(), id) } returns 1

        val result = repository.edit(id, updatedReview)

        assertNotNull(result)
        assertEquals(5, result?.mark)
        assertEquals("Новый текст", result?.review)
    }

    @Test
    fun `редактирование вызывает исключение если прошло более 24 часов`() {
        val id = UUID.randomUUID()
        val oldReview = Reviews(
            id,
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now().minusDays(2),
            3,
            "Старый текст"
        )

        every { jdbcTemplate.query(any<String>(), any<RowMapper<Reviews>>(), id) } returns listOf(oldReview)

        assertThrows(ReviewEditTimeExpiredException::class.java) {
            repository.editMarkAndReview(id, MarkAndReview(5, "Новый"))
        }
    }

    @Test
    fun `редактирование вызывает исключение если оценка вне диапазона`() {
        val id = UUID.randomUUID()
        val oldReview = Reviews(
            id,
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now().minusHours(1),
            3,
            "Старый текст"
        )

        every { jdbcTemplate.query(any<String>(), any<RowMapper<Reviews>>(), id) } returns listOf(oldReview)

        assertThrows(IllegalArgumentException::class.java) {
            repository.editMarkAndReview(id, MarkAndReview(10, "Новый"))
        }
    }
}