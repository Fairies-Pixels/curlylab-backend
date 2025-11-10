package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.MarkAndReview
import com.curlylab.curlylabback.model.Reviews
import com.curlylab.curlylabback.model.ReviewMarks
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.*

interface ReviewsRepository : BaseInterfaceRepository<Reviews> {
    fun getAll(): List<Reviews>
    fun editMarkAndReview(id: UUID, entity: MarkAndReview): Reviews?
}

@Repository
class ReviewsRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : ReviewsRepository {

    private val rowMapper = RowMapper<Reviews> { rs: ResultSet, _: Int ->
        Reviews(
            reviewId = UUID.fromString(rs.getString("review_id")),
            userId = UUID.fromString(rs.getString("user_id")),
            productId = UUID.fromString(rs.getString("product_id")),
            date = rs.getTimestamp("date").toLocalDateTime(),
            mark =  ReviewMarks.valueOf(rs.getString("mark").uppercase()),
            review = rs.getString("review")
        )
    }

    override fun getAll(): List<Reviews> {
        val sql = "SELECT review_id, user_id, product_id, date, mark, review FROM reviews"
        return jdbcTemplate.query(sql, rowMapper)
    }

    override fun get(id: UUID): Reviews? {
        val sql = """SELECT review_id, user_id, product_id, date, mark, review
                FROM reviews
                WHERE review_id = ?"""
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    override fun add(entity: Reviews): Boolean {
        val sql = """
            INSERT INTO reviews (review_id, user_id, product_id, date, mark, review)
            VALUES (?, ?, ?, ?, ?::marks_enum, ?)
        """.trimIndent()

        return jdbcTemplate.update(
            sql,
            entity.reviewId,
            entity.userId,
            entity.productId,
            entity.date,
            entity.mark.name.lowercase(),
            entity.review
        ) > 0
    }

    override fun delete(id: UUID): Boolean {
        val sql = "DELETE FROM reviews WHERE review_id = ?"
        return jdbcTemplate.update(sql, id) > 0
    }

    override fun edit(id: UUID, entity: Reviews): Reviews? {
        return null
    }

    override fun editMarkAndReview(id: UUID, entity: MarkAndReview): Reviews? {
        val oldReviews = get(id) ?: return null

        val creationDate = oldReviews.date ?: return null
        if (!creationDate.isAfter(LocalDateTime.now().minusHours(24))) {
           return null
        }

        val newMark = entity.mark ?: oldReviews.mark
        val newReview = entity.review ?: oldReviews.review

        val sql = """
            UPDATE reviews
            SET mark = ?::marks_enum, review = ?
            WHERE review_id = ?
        """.trimIndent()

        val updated =  jdbcTemplate.update(
            sql,
            newMark.name.lowercase(),
            newReview,
            id
        ) > 0

        return if (updated) get(id) else null
    }
}
