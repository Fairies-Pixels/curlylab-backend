package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.Reviews
import com.curlylab.curlylabback.repository.ReviewsRepository
import org.springframework.stereotype.Service

@Service
class ReviewsService(
    val reviewsRepository: ReviewsRepository
) : BaseServiceImpl<Reviews>(reviewsRepository) {

    fun getAll(): List<Reviews> {
        return reviewsRepository.getAll()
    }
}