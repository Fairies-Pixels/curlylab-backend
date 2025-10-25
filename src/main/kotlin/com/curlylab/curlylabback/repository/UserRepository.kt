package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

interface UserRepository : BaseInterfaceRepository<User>

@Repository
class UserRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : UserRepository {

    private val rowMapper = RowMapper<User> { rs: ResultSet, _: Int ->
        User(
            id = UUID.fromString(rs.getString("id")),
            username = rs.getString("username"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            imageUrl = rs.getString("image_url")
        )
    }

    override fun get(id: UUID): User? {
        val sql = "SELECT id, username, created_at, image_url FROM users WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    override fun add(entity: User): Boolean {
        val sql = """
            INSERT INTO users (id, username, created_at, image_url)
            VALUES (?, ?, ?, ?)
        """.trimIndent()

        return jdbcTemplate.update(
            sql,
            entity.id,
            entity.username,
            entity.createdAt,
            entity.imageUrl
        ) > 0
    }

    override fun delete(id: UUID): Boolean {
        val sql = "DELETE FROM users WHERE id = ?"
        return jdbcTemplate.update(sql, id) > 0
    }

    override fun edit(id: UUID, entity: User): User? {
        val sql = """
            UPDATE users
            SET username = ?, created_at = ?, image_url = ?
            WHERE id = ?
        """.trimIndent()

        val updated = jdbcTemplate.update(
            sql,
            entity.username,
            entity.createdAt,
            entity.imageUrl,
            id
        ) > 0

        return if (updated) get(id) else null
    }
}