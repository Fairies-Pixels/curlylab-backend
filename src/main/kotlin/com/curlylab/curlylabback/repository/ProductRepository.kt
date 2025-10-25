package com.curlylab.curlylabback.repository

import com.curlylab.curlylabback.model.Product
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

interface ProductRepository : BaseInterfaceRepository<Product> {
    fun getAll(): List<Product>
}

@Repository
class ProductRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : ProductRepository {
    private fun parseTags(raw: String?): List<String> {
        return if (raw.isNullOrBlank()) {
            emptyList()
        } else {
            raw
                .removePrefix("{")
                .removeSuffix("}")
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }
    }

    private val rowMapper = RowMapper<Product> { rs: ResultSet, _: Int ->
        Product(
            id = UUID.fromString(rs.getString("id")),
            name = rs.getString("name"),
            description = rs.getString("description"),
            tags = parseTags(rs.getString("tags")),
            imageUrl = rs.getString("image_url")
        )
    }

    override fun get(id: UUID): Product? {
        val sql = "SELECT id, name, description, tags, image_url FROM products WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    override fun getAll(): List<Product> {
        val sql = "SELECT id, name, description, tags, image_url FROM products"
        return jdbcTemplate.query(sql, rowMapper)
    }

    override fun add(entity: Product): Boolean {
        val sql = """
            INSERT INTO products (id, name, description, tags, image_url)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        val tagsString = entity.tags.joinToString(",")

        return jdbcTemplate.update(
            sql,
            entity.id,
            entity.name,
            entity.description,
            tagsString,
            entity.imageUrl
        ) > 0
    }

    override fun delete(id: UUID): Boolean {
        val sql = "DELETE FROM products WHERE id = ?"
        return jdbcTemplate.update(sql, id) > 0
    }

    override fun edit(id: UUID, entity: Product): Product? {
        val sql = """
            UPDATE products
            SET name = ?, description = ?, tags = ?, image_url = ?
            WHERE id = ?
        """.trimIndent()

        val tagsString = entity.tags.joinToString(",")

        val updated = jdbcTemplate.update(
            sql,
            entity.name,
            entity.description,
            tagsString,
            entity.imageUrl,
            id
        ) > 0

        return if (updated) get(id) else null
    }
}