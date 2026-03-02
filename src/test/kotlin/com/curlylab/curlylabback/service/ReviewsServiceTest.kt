package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.Reviews
import com.curlylab.curlylabback.repository.ReviewsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class ReviewsServiceTest {

    private lateinit var reviewsRepository: ReviewsRepository
    private lateinit var reviewsService: ReviewsService

    @BeforeEach
    fun setUp() {
        reviewsRepository = mockk()
        reviewsService = ReviewsService(reviewsRepository)
    }

    @Test
    fun `получение всех отзывов по продукту возвращает список из репозитория`() {
        val productId = UUID.randomUUID()
        val expectedReviews = listOf(
            Reviews(
                reviewId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                productId = productId,
                date = LocalDateTime.now(),
                mark = 5,
                review = "Отлично"
            ),
            Reviews(
                reviewId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                productId = productId,
                date = LocalDateTime.now(),
                mark = 4,
                review = "Хорошо"
            )
        )

        every { reviewsRepository.getAllForProduct(productId) } returns expectedReviews

        val result = reviewsService.getAllForProduct(productId)

        assertEquals(2, result.size)
        assertEquals(expectedReviews[0].reviewId, result[0].reviewId)
        assertEquals(expectedReviews[1].reviewId, result[1].reviewId)
        assertEquals(expectedReviews[0].mark, result[0].mark)
        assertEquals(expectedReviews[1].mark, result[1].mark)
        verify(exactly = 1) { reviewsRepository.getAllForProduct(productId) }
    }

    @Test
    fun `получение отзывов по продукту возвращает пустой список, если отзывов нет`() {
        val productId = UUID.randomUUID()

        every { reviewsRepository.getAllForProduct(productId) } returns emptyList()

        val result = reviewsService.getAllForProduct(productId)

        assertEquals(0, result.size)
        assert(result.isEmpty())
        verify(exactly = 1) { reviewsRepository.getAllForProduct(productId) }
    }

    @Test
    fun `получение отзыва по id возвращает отзыв из репозитория`() {
        val reviewId = UUID.randomUUID()
        val expectedReview = Reviews(
            reviewId = reviewId,
            userId = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            date = LocalDateTime.now(),
            mark = 5,
            review = "Отлично"
        )

        every { reviewsRepository.get(reviewId) } returns expectedReview

        val result = reviewsService.getById(reviewId)

        assertNotNull(result)
        assertEquals(reviewId, result?.reviewId)
        assertEquals(5, result?.mark)
        verify(exactly = 1) { reviewsRepository.get(reviewId) }
    }

    @Test
    fun `получение отзыва по несуществующему id возвращает null`() {
        val reviewId = UUID.randomUUID()

        every { reviewsRepository.get(reviewId) } returns null

        val result = reviewsService.getById(reviewId)

        assertEquals(null, result)
        verify(exactly = 1) { reviewsRepository.get(reviewId) }
    }
}