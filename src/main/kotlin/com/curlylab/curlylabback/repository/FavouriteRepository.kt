package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.Favourite
import com.curlylab.curlylabback.model.Reviews
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

interface FavouriteRepository : BaseInterfaceRepository<Favourite> {
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

    override fun get(id: UUID): Favourite? {
        return null
    }

    override fun add(entity: Favourite): Boolean {
        return false
    }

    override fun delete(id: UUID): Boolean {
        return false
    }

    override fun edit(id: UUID, entity: Favourite): Favourite? {
        return null
    }
}
