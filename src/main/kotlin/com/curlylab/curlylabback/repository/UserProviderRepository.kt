package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.UserProvider
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.util.*

interface UserProviderRepository {
    fun find(provider: String, providerUserId: String): UserProvider?
    fun add(entity: UserProvider): Boolean
}

@Repository
class UserProviderRepositoryImpl(
    private val jdbc: JdbcTemplate
) : UserProviderRepository {

    private val mapper = RowMapper<UserProvider> { rs, _ ->
        UserProvider(
            id = UUID.fromString(rs.getString("id")),
            userId = UUID.fromString(rs.getString("user_id")),
            provider = rs.getString("provider"),
            providerUserId = rs.getString("provider_user_id"),
            email = rs.getString("email"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime()
        )
    }

    override fun find(provider: String, providerUserId: String): UserProvider? {
        val sql = """
           SELECT * FROM user_providers
           WHERE provider = ? AND provider_user_id = ?
        """
        return jdbc.query(sql, mapper, provider, providerUserId).firstOrNull()
    }

    override fun add(entity: UserProvider): Boolean {
        val sql = """
            INSERT INTO user_providers (id, user_id, provider, provider_user_id, email)
            VALUES (?, ?, ?, ?, ?)
        """
        return jdbc.update(
            sql,
            entity.id, entity.userId, entity.provider, entity.providerUserId, entity.email
        ) > 0
    }
}
