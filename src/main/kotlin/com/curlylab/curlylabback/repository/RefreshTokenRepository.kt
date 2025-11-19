package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.UserRefreshToken
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.util.*

interface RefreshTokenRepository {
    fun save(token: UserRefreshToken): Boolean
    fun findValid(token: String): UserRefreshToken?
    fun revoke(tokenId: UUID): Boolean
}

@Repository
class RefreshTokenRepositoryImpl(
    private val jdbc: JdbcTemplate
) : RefreshTokenRepository {

    private val mapper = RowMapper<UserRefreshToken> { rs, _ ->
        UserRefreshToken(
            id = UUID.fromString(rs.getString("id")),
            userId = UUID.fromString(rs.getString("user_id")),
            token = rs.getString("token"),
            expiresAt = rs.getTimestamp("expires_at").toLocalDateTime(),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            revoked = rs.getBoolean("revoked")
        )
    }

    override fun save(token: UserRefreshToken): Boolean {
        val sql = """
            INSERT INTO user_refresh_tokens (id, user_id, token, expires_at, created_at, revoked)
            VALUES (?, ?, ?, ?, ?, ?)
        """
        return jdbc.update(
            sql,
            token.id, token.userId, token.token, token.expiresAt, token.createdAt, token.revoked
        ) > 0
    }

    override fun findValid(token: String): UserRefreshToken? {
        val sql = """
            SELECT * FROM user_refresh_tokens 
            WHERE token = ? AND revoked = false AND expires_at > now()
        """
        return jdbc.query(sql, mapper, token).firstOrNull()
    }

    override fun revoke(tokenId: UUID): Boolean {
        val sql = """UPDATE user_refresh_tokens SET revoked = true WHERE id = ?"""
        return jdbc.update(sql, tokenId) > 0
    }
}
