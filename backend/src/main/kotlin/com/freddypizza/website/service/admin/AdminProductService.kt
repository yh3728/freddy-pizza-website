package com.freddypizza.website.service.admin

import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.repository.ProductRepository
import com.freddypizza.website.request.admin.AdminProductImageRequest
import com.freddypizza.website.request.admin.AdminProductQuantityRequest
import com.freddypizza.website.request.admin.AdminProductUpdateRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class AdminProductService(
    private val productRepository: ProductRepository,
    var uploadDir: Path = Paths.get("uploads/products"),
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
                weight = productUpdateRequest.weight ?: existingProduct.weight,
                ingredients = productUpdateRequest.ingredients ?: existingProduct.ingredients,
                price = productUpdateRequest.price ?: existingProduct.price,
                quantity = productUpdateRequest.quantity ?: existingProduct.quantity,
                category = productUpdateRequest.category ?: existingProduct.category,
                imagePath = productUpdateRequest.imagePath ?: existingProduct.imagePath,
            )
        return productRepository.save(updatedProduct)
    }

    fun updateQuantity(
        id: Long,
        quantityRequest: AdminProductQuantityRequest,
    ): ProductEntity {
        val existingProduct = productRepository.findByIdOrNull(id) ?: throw ProductNotFoundException()
        return productRepository.save(existingProduct.copy(quantity = quantityRequest.quantity))
    }

    fun updateImagePath(
        id: Long,
        imageRequest: AdminProductImageRequest,
    ): ProductEntity {
        val product = getProductById(id) ?: throw ProductNotFoundException()
        val image = imageRequest.image
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }

        val extension = image.originalFilename?.substringAfterLast('.', "") ?: "jpg"
        val filename = "product_${UUID.randomUUID()}.$extension"
        val filePath = uploadDir.resolve(filename)

        Files.copy(image.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

        val updatedProduct = product.copy(imagePath = "/uploads/products/$filename")
        return productRepository.save(updatedProduct)
    }
}
