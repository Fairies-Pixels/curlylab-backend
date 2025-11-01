package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.Reviews
import com.curlylab.curlylabback.model.ReviewMarks
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

interface ReviewsRepository : BaseInterfaceRepository<Reviews>

@Repository
class ReviewsRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : ReviewsRepository {

    override fun get(id: UUID): Reviews? {
        return Reviews(
            productId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            mark = ReviewMarks.ZERO_STARS,
            review = ""
        )
    }

    override fun add(entity: Reviews): Boolean {
        return false
    }

    override fun delete(id: UUID): Boolean {
        return false
    }

    override fun edit(id: UUID, entity: Reviews): Reviews? {
        return Reviews(
            productId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            mark = ReviewMarks.ZERO_STARS,
            review = ""
        )
    }
}
