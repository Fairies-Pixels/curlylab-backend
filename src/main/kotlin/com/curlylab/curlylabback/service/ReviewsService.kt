package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.MarkAndReview
import com.curlylab.curlylabback.repository.ReviewsRepository
import com.curlylab.curlylabback.model.Reviews
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReviewsService(
    val reviewsRepository: ReviewsRepository
) : BaseServiceImpl<Reviews>(reviewsRepository) {

    fun getAll(): List<Reviews> {
        return reviewsRepository.getAll()
    }

    fun editMarkAndReview(id: UUID, entity: MarkAndReview): Reviews? {
        return reviewsRepository.editMarkAndReview(id, entity)
    }
}