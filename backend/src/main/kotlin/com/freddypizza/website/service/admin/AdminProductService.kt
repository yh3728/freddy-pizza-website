package com.freddypizza.website.service.admin

import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.repository.ProductRepository
import com.freddypizza.website.request.admin.AdminProductAvailabilityRequest
import com.freddypizza.website.request.admin.AdminProductUpdateRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AdminProductService(
    private val productRepository: ProductRepository,
) {
    fun getProductById(id: Long): ProductEntity? = productRepository.findByIdOrNull(id)

    fun getAllProducts(): List<ProductEntity> = productRepository.findAll()

    fun addProduct(productEntity: ProductEntity): ProductEntity = productRepository.save(productEntity)

    fun deleteProduct(id: Long) {
        productRepository.findById(id).orElseThrow { ProductNotFoundException() }
        productRepository.deleteById(id)
    }

    fun updateProduct(
        id: Long,
        productUpdateRequest: AdminProductUpdateRequest,
    ): ProductEntity {
        val existingProduct = productRepository.findByIdOrNull(id) ?: throw ProductNotFoundException()
        val updatedProduct =
            existingProduct.copy(
                name = productUpdateRequest.name ?: existingProduct.name,
                description = productUpdateRequest.description ?: existingProduct.description,
                price = productUpdateRequest.price ?: existingProduct.price,
                isAvailable = productUpdateRequest.isAvailable ?: existingProduct.isAvailable,
                category = productUpdateRequest.category ?: existingProduct.category,
            )
        return productRepository.save(updatedProduct)
    }

    fun updateAvailability(
        id: Long,
        availabilityRequest: AdminProductAvailabilityRequest,
    ): ProductEntity {
        val existingProduct = productRepository.findByIdOrNull(id) ?: throw ProductNotFoundException()
        return productRepository.save(existingProduct.copy(isAvailable = availabilityRequest.isAvailable))
    }
}
