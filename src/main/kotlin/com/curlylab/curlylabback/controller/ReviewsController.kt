package com.curlylab.curlylabback.controller

import com.amazonaws.services.kms.model.NotFoundException
import com.curlylab.curlylabback.model.Reviews
import com.curlylab.curlylabback.repository.ReviewEditTimeExpiredException
import com.curlylab.curlylabback.service.ReviewsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/products")
class ReviewsController(
    private val reviewsService: ReviewsService
) {
    @GetMapping("/{product_id}/reviews")
    fun getAllReviews(@PathVariable product_id: UUID): ResponseEntity<List<Reviews>> {
        val products = reviewsService.getAllForProduct(product_id)
        return ResponseEntity.ok(products)
    }

    @PostMapping("/{product_id}/reviews")
    fun createReview(
        @PathVariable product_id: UUID,
        @RequestBody review: Reviews
    ): ResponseEntity<String> {
        if (review.productId == null) {
            review.productId = product_id
        } else {
            return ResponseEntity.badRequest().body("Failed to create a review")
        }
        val created = reviewsService.create(review)
        return if (created) ResponseEntity.ok("Review has created!")
        else ResponseEntity.badRequest().body("Failed to create a review")
    }

    @PutMapping("/{product_id}/reviews/{review_id}")
    fun updateReview(
        @PathVariable product_id: UUID,
        @PathVariable review_id: UUID,
        @RequestBody review: Reviews
    ): ResponseEntity<Any> {
        return try {
            val updated = reviewsService.update(review_id, review)
            if (updated != null) {
                ResponseEntity.ok(updated)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (ex: ReviewEditTimeExpiredException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.message)
        } catch (ex: NotFoundException) {
            ResponseEntity.notFound().build()
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ex.message)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error")
        }
    }

    @DeleteMapping("/{product_id}/reviews/{review_id}")
    fun deleteReview(@PathVariable review_id: UUID): ResponseEntity<String> {
        val deleted = reviewsService.delete(review_id)
        return if (deleted) ResponseEntity.ok("Review has deleted!")
        else ResponseEntity.notFound().build()
    }
}