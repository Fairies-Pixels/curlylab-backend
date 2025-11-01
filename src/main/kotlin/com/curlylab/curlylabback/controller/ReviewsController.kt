package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.service.ReviewsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reviews")
class ReviewsController(
    private val reviewsService: ReviewsService
) {
}