package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.Favourite
import com.curlylab.curlylabback.model.Reviews
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

interface FavouriteRepository : BaseInterfaceRepository<Favourite> {
    fun getALLForUser(user_id: UUID): List<Favourite>?
    fun deleteByUserAndProduct(user_id: UUID, product_id: UUID): Boolean
    fun getByUserAndProduct(user_id: UUID, product_id: UUID): Favourite?
}

@Repository
class FavouriteRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : FavouriteRepository {

    private val rowMapper = RowMapper<Favourite> { rs: ResultSet, _: Int ->
        Favourite(
            userId = UUID.fromString(rs.getString("user_id")),
            productId = UUID.fromString(rs.getString("product_id")),
        )
    }

    override fun get(user_id: UUID): Favourite? {
        val sql = "SELECT user_id, product_id FROM favourites WHERE user_id = ?"
        return jdbcTemplate.query(sql, rowMapper, user_id).firstOrNull()
    }

    override fun getByUserAndProduct(user_id: UUID, product_id: UUID): Favourite? {
        val sql = "SELECT user_id, product_id FROM favourites WHERE user_id = ? AND product_id = ?"
        return jdbcTemplate.query(sql, rowMapper, user_id, product_id).firstOrNull()
    }

    override fun getALLForUser(user_id: UUID): List<Favourite>? {
        val sql = "SELECT user_id, product_id FROM favourites WHERE user_id = ?"
        return jdbcTemplate.query(sql, rowMapper, user_id)
    }

    override fun add(entity: Favourite): Boolean {
        val sql = """
            INSERT INTO favourites (user_id, product_id)
            VALUES (?, ?)
        """.trimIndent()

        return jdbcTemplate.update(
            sql,
            entity.userId,
            entity.productId,
        ) > 0
    }

    override fun delete(product_id: UUID): Boolean {
        val sql = "DELETE FROM favourites WHERE product_id = ?"
        return jdbcTemplate.update(sql, product_id) > 0
    }

    override fun deleteByUserAndProduct(user_id: UUID, product_id: UUID): Boolean {
        val sql = "DELETE FROM favourites WHERE user_id = ? AND product_id = ?"
        return jdbcTemplate.update(sql, user_id, product_id) > 0
    }

    override fun edit(id: UUID, entity: Favourite): Favourite? {
        return null
    }
}
