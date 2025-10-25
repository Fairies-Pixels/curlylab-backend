package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.HairType
import com.curlylab.curlylabback.model.Porosity
import com.curlylab.curlylabback.model.Thickness
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

interface HairTypeRepository : BaseInterfaceRepository<HairType>

@Repository
class HairTypeRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : HairTypeRepository {

    private val rowMapper = RowMapper<HairType> { rs: ResultSet, _: Int ->
        HairType(
            userId = UUID.fromString(rs.getString("user_id")),
            porosity = Porosity.valueOf(rs.getString("porosity").uppercase()),
            isColored = rs.getBoolean("is_colored"),
            thickness = Thickness.valueOf(rs.getString("thickness").uppercase())
        )
    }

    override fun get(id: UUID): HairType? {
        val sql = """
            SELECT user_id, porosity, is_colored, thickness
            FROM user_hairtypes
            WHERE user_id = ?
        """.trimIndent()

        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    override fun add(entity: HairType): Boolean {
        val sql = """
            INSERT INTO user_hairtypes (user_id, porosity, is_colored, thickness)
            VALUES (?, ?::porosity_enum, ?, ?::thickness_enum)
        """.trimIndent()

        return jdbcTemplate.update(
            sql,
            entity.userId,
            entity.porosity.name.lowercase(),
            entity.isColored,
            entity.thickness.name.lowercase()
        ) > 0
    }

    override fun delete(id: UUID): Boolean {
        val sql = "DELETE FROM user_hairtypes WHERE user_id = ?"
        return jdbcTemplate.update(sql, id) > 0
    }

    override fun edit(id: UUID, entity: HairType): HairType? {
        val sql = """
            UPDATE user_hairtypes
            SET porosity = ?::porosity_enum,
                is_colored = ?,
                thickness = ?::thickness_enum
            WHERE user_id = ?
        """.trimIndent()

        val updated = jdbcTemplate.update(
            sql,
            entity.porosity.name.lowercase(),
            entity.isColored,
            entity.thickness.name.lowercase(),
            id
        ) > 0

        return if (updated) get(id) else null
    }
}