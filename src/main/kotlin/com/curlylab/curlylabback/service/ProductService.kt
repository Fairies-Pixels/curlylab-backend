package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.Product
import com.curlylab.curlylabback.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository
) : BaseServiceImpl<Product>(productRepository) {

    fun getAll(): List<Product> {
        return productRepository.getAll()
    }
}