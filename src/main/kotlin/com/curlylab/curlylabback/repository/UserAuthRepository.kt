package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.UserAuth
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.util.*

interface UserAuthRepository {
    fun findByEmail(email: String): UserAuth?
    fun add(entity: UserAuth): Boolean
}

@Repository
class UserAuthRepositoryImpl(
    private val jdbc: JdbcTemplate
) : UserAuthRepository {

    private val mapper = RowMapper<UserAuth> { rs, _ ->
        UserAuth(
            userId = UUID.fromString(rs.getString("user_id")),
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            salt = rs.getString("salt"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime()
        )
    }

    override fun findByEmail(email: String): UserAuth? {
        val sql = """SELECT * FROM user_auth WHERE email = ?"""
        return jdbc.query(sql, mapper, email).firstOrNull()
    }

    override fun add(entity: UserAuth): Boolean {
        val sql = """
            INSERT INTO user_auth (user_id, email, password_hash, salt)
            VALUES (?, ?, ?, ?)
        """
        return jdbc.update(sql, entity.userId, entity.email, entity.passwordHash, entity.salt) > 0
    }
}
