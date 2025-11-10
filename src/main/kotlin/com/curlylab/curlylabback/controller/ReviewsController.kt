package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.MarkAndReview
import com.curlylab.curlylabback.model.Reviews
import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.service.ReviewsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/reviews")
class ReviewsController(
    private val reviewsService: ReviewsService
) {
    @GetMapping
    fun getAllReviews(): ResponseEntity<List<Reviews>> {
        val products = reviewsService.getAll()
        return ResponseEntity.ok(products)
    }

    @PostMapping
    fun createReview(@RequestBody review: Reviews): ResponseEntity<String> {
        val created = reviewsService.create(review)
        return if (created) ResponseEntity.ok("Review has created!")
        else ResponseEntity.badRequest().body("Failed to create a review")
    }

    @PutMapping("/{id}")
    fun updateReview(@PathVariable id: UUID, @RequestBody review: MarkAndReview): ResponseEntity<Reviews> {
        val updated = reviewsService.editMarkAndReview(id, review)
        return if (updated != null) ResponseEntity.ok(updated)
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteRewview(@PathVariable id: UUID): ResponseEntity<String> {
        val deleted = reviewsService.delete(id)
        return if (deleted) ResponseEntity.ok("Review has deleted!")
        else ResponseEntity.notFound().build()
    }
}