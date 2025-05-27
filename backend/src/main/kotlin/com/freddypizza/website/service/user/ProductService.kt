package com.freddypizza.website.service.user

import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService (
    private val productRepository: ProductRepository
){

    fun getProductById(id: Long): ProductEntity? = productRepository.findByIdOrNull(id)
    fun getAllProducts(): List<ProductEntity> = productRepository.findAll()

}