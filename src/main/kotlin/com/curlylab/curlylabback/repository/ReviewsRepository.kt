package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.MarkAndReview
import com.curlylab.curlylabback.model.Reviews
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.*

interface ReviewsRepository : BaseInterfaceRepository<Reviews> {
    fun getAllForProduct(product_id: UUID): List<Reviews>
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
            mark =  rs.getInt("mark"),
            review = rs.getString("review")
        )
    }

    override fun getAllForProduct(product_id: UUID): List<Reviews> {
        val sql = """
            SELECT review_id, user_id, product_id, mark, date, review
            FROM reviews
            WHERE product_id = ?
            """.trimIndent()
        return jdbcTemplate.query(sql, rowMapper, product_id)
    }

    override fun get(id: UUID): Reviews? {
        val sql = """SELECT review_id, user_id, product_id, date, mark, review
                FROM reviews
                WHERE review_id = ?"""
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    override fun add(entity: Reviews): Boolean {
        entity.mark ?: return false
        if (entity.mark < 1 || entity.mark > 5) {
            return false
        }

        val sql = """
            INSERT INTO reviews (review_id, user_id, product_id, date, mark, review)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        return jdbcTemplate.update(
            sql,
            entity.reviewId,
            entity.userId,
            entity.productId,
            entity.date,
            entity.mark,
            entity.review
        ) > 0
    }

    override fun delete(id: UUID): Boolean {
        val sql = "DELETE FROM reviews WHERE review_id = ?"
        return jdbcTemplate.update(sql, id) > 0
    }

    override fun edit(id: UUID, entity: Reviews): Reviews? {
        val editableEntity = MarkAndReview(entity.mark, entity.review)
        return editMarkAndReview(id, editableEntity)
    }

    fun editMarkAndReview(id: UUID, entity: MarkAndReview): Reviews? {
        val oldReviews = get(id) ?: return null

        val creationDate = oldReviews.date ?: return null
        if (!creationDate.isAfter(LocalDateTime.now().minusHours(24))) {
           return null
        }

        val newMark = (entity.mark ?: oldReviews.mark) ?: return null
        val newReview = entity.review ?: oldReviews.review

        if (newMark < 1 || newMark > 5) {
            return null
        }

        val sql = """
            UPDATE reviews
            SET mark = ?, review = ?
            WHERE review_id = ?
        """.trimIndent()

        val updated =  jdbcTemplate.update(
            sql,
            newMark,
            newReview,
            id
        ) > 0

        return if (updated) get(id) else null
    }
}
