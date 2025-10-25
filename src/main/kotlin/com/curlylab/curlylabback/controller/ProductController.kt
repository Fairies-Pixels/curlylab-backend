package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.Product
import com.curlylab.curlylabback.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<Product>> {
        val products = productService.getAll()
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: UUID): ResponseEntity<Product> {
        val product = productService.getById(id)
        return if (product != null) ResponseEntity.ok(product)
        else ResponseEntity.notFound().build()
    }
}