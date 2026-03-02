package com.curlylab.curlylabback.controller

import com.amazonaws.services.kms.model.NotFoundException
import com.curlylab.curlylabback.model.Reviews
import com.curlylab.curlylabback.repository.ReviewEditTimeExpiredException
import com.curlylab.curlylabback.service.ReviewsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

class ReviewsControllerTest {

    private lateinit var reviewsService: ReviewsService
    private lateinit var reviewsController: ReviewsController

    @BeforeEach
    fun setUp() {
        reviewsService = mockk()
        reviewsController = ReviewsController(reviewsService)
    }

    @Test
    fun `получение всех отзывов возвращает список с статусом OK`() {
        val productId = UUID.randomUUID()
        val expectedReviews = listOf(
            Reviews(
                reviewId = UUID.randomUUID(),
                productId = productId,
                userId = UUID.randomUUID(),
                mark = 5,
                review = "Отлично"
            ),
            Reviews(
                reviewId = UUID.randomUUID(),
                productId = productId,
                userId = UUID.randomUUID(),
                mark = 4,
                review = "Хорошо"
            )
        )

        every { reviewsService.getAllForProduct(productId) } returns expectedReviews

        val response = reviewsController.getAllReviews(productId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedReviews, response.body)
        verify { reviewsService.getAllForProduct(productId) }
    }

    @Test
    fun `успешное создание отзыва возвращает OK с сообщением`() {
        val productId = UUID.randomUUID()
        val review = Reviews(
            reviewId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            productId = null,
            mark = 5,
            review = "Отличный продукт"
        )

        every { reviewsService.create(any()) } returns true

        val response = reviewsController.createReview(productId, review)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Review has created!", response.body)
        verify { reviewsService.create(any()) }
    }

    @Test
    fun `создание отзыва с уже установленным productId возвращает BAD REQUEST`() {
        val productId = UUID.randomUUID()
        val review = Reviews(
            reviewId = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            mark = 5,
            review = "Отличный продукт"
        )

        val response = reviewsController.createReview(productId, review)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Failed to create a review", response.body)
        verify(exactly = 0) { reviewsService.create(any()) }
    }

    @Test
    fun `ошибка при создании отзыва возвращает BAD REQUEST`() {
        val productId = UUID.randomUUID()
        val review = Reviews(
            reviewId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            productId = productId,
            mark = 5,
            review = "Отличный продукт"
        )

        every { reviewsService.create(any()) } returns false

        val response = reviewsController.createReview(productId, review)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Failed to create a review", response.body)
    }

    @Test
    fun `успешное обновление отзыва возвращает обновленный отзыв с статусом OK`() {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val updatedReview = Reviews(
            reviewId = reviewId,
            productId = productId,
            userId = UUID.randomUUID(),
            mark = 4,
            review = "Обновленный отзыв"
        )

        every { reviewsService.update(reviewId, any()) } returns updatedReview

        val response = reviewsController.updateReview(productId, reviewId, updatedReview)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedReview, response.body)
        verify { reviewsService.update(reviewId, any()) }
    }

    @Test
    fun `обновление несуществующего отзыва возвращает NOT FOUND`() {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val review = Reviews(
            reviewId = reviewId,
            productId = productId,
            userId = UUID.randomUUID(),
            mark = 4,
            review = "Отзыв"
        )

        every { reviewsService.update(reviewId, any()) } returns null

        val response = reviewsController.updateReview(productId, reviewId, review)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `обновление отзыва с истекшим временем редактирования возвращает FORBIDDEN с сообщением`() {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val review = Reviews(
            reviewId = reviewId,
            productId = productId,
            userId = UUID.randomUUID(),
            mark = 4,
            review = "Отзыв"
        )
        val errorMessage = "Время редактирования истекло"

        every { reviewsService.update(reviewId, any()) } throws ReviewEditTimeExpiredException(
            errorMessage
        )

        val response = reviewsController.updateReview(productId, reviewId, review)

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertEquals(errorMessage, response.body)
    }

    @Test
    fun `обновление отзыва с NotFoundException возвращает NOT FOUND`() {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val review = Reviews(
            reviewId = reviewId,
            productId = productId,
            userId = UUID.randomUUID(),
            mark = 4,
            review = "Отзыв"
        )

        every { reviewsService.update(reviewId, any()) } throws NotFoundException("Отзыв не найден")

        val response = reviewsController.updateReview(productId, reviewId, review)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `обновление отзыва с IllegalArgumentException возвращает BAD REQUEST с сообщением`() {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val review = Reviews(
            reviewId = reviewId,
            productId = productId,
            userId = UUID.randomUUID(),
            mark = 4,
            review = "Отзыв"
        )
        val errorMessage = "Некорректные данные"

        every { reviewsService.update(reviewId, any()) } throws IllegalArgumentException(
            errorMessage
        )

        val response = reviewsController.updateReview(productId, reviewId, review)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(errorMessage, response.body)
    }

    @Test
    fun `обновление отзыва с неизвестным исключением возвращает INTERNAL SERVER ERROR`() {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val review = Reviews(
            reviewId = reviewId,
            productId = productId,
            userId = UUID.randomUUID(),
            mark = 4,
            review = "Отзыв"
        )

        every {
            reviewsService.update(
                reviewId,
                any()
            )
        } throws RuntimeException("Неизвестная ошибка")

        val response = reviewsController.updateReview(productId, reviewId, review)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("Internal server error", response.body)
    }

    @Test
    fun `успешное удаление отзыва возвращает OK с сообщением`() {
        val reviewId = UUID.randomUUID()

        every { reviewsService.delete(reviewId) } returns true

        val response = reviewsController.deleteReview(reviewId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Review has deleted!", response.body)
        verify { reviewsService.delete(reviewId) }
    }

    @Test
    fun `удаление несуществующего отзыва возвращает NOT FOUND`() {
        val reviewId = UUID.randomUUID()

        every { reviewsService.delete(reviewId) } returns false

        val response = reviewsController.deleteReview(reviewId)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }
}