package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.Reviews
import com.curlylab.curlylabback.service.ReviewsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

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
    fun createReview(@PathVariable product_id: UUID, @RequestBody review: Reviews): ResponseEntity<String> {
        if (review.productId == null) {
            review.productId = product_id
        } else {
            return ResponseEntity.badRequest().body("Failed to create a review")
        }
        val created = reviewsService.create(review)
        return if (created) ResponseEntity.ok("Review has created!")
        else ResponseEntity.badRequest().body("Failed to create a review")
    }

    @PutMapping("/{product_id}/reviews/{user_id}")
    fun updateReview(@PathVariable product_id: UUID, @PathVariable user_id: UUID, @RequestBody review: Reviews): ResponseEntity<Reviews> {
        val id = reviewsService.getByProductAndUser(product_id, user_id)?.reviewId
        if (id == null) {
            return ResponseEntity.notFound().build()
        }
        val updated = reviewsService.update(id, review)
        return if (updated != null) ResponseEntity.ok(updated)
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{product_id}/reviews/{review_id}")
    fun deleteReview(@PathVariable review_id: UUID): ResponseEntity<String> {
        val deleted = reviewsService.delete(review_id)
        return if (deleted) ResponseEntity.ok("Review has deleted!")
        else ResponseEntity.notFound().build()
    }
}