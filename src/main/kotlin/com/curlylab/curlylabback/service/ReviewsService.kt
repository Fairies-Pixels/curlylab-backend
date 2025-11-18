package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.Reviews
import com.curlylab.curlylabback.repository.ReviewsRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReviewsService(
    val reviewsRepository: ReviewsRepository
) : BaseServiceImpl<Reviews>(reviewsRepository) {

    fun getAllForProduct(product_id: UUID): List<Reviews> {
        return reviewsRepository.getAllForProduct(product_id)
    }
}