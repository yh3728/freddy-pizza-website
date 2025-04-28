package com.freddypizza.website.controller.admin

import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.request.AdminProductAvailabilityRequest
import com.freddypizza.website.request.AdminProductRequest
import com.freddypizza.website.request.AdminProductUpdateRequest
import com.freddypizza.website.response.AdminProductResponse
import com.freddypizza.website.service.admin.AdminProductService
import com.freddypizza.website.util.toAdminProductResponseDTO
import com.freddypizza.website.util.toProductEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/menu/items")
class AdminMenuController(
    private val productService: AdminProductService,
) {
    @PostMapping
    fun addItem(
        @RequestBody productRequestDTO: AdminProductRequest,
    ): ResponseEntity<AdminProductResponse> {
        val productEntity = productRequestDTO.toProductEntity()
        val productResponse = productService.addProduct(productEntity).toAdminProductResponseDTO()
        return ResponseEntity(productResponse, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllItems(): ResponseEntity<List<AdminProductResponse>> =
        ResponseEntity.ok(
            productService.getAllProducts().map {
                it.toAdminProductResponseDTO()
            },
        )

    @GetMapping("/{id}")
    fun getItemById(
        @PathVariable id: Long,
    ): ResponseEntity<AdminProductResponse> {
        val item = productService.getProductById(id) ?: throw ProductNotFoundException()
        return ResponseEntity.ok(item.toAdminProductResponseDTO())
    }

    @DeleteMapping("/{id}")
    fun deleteItem(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    fun updateItem(
        @PathVariable id: Long,
        @RequestBody productUpdateRequest: AdminProductUpdateRequest,
    ): ResponseEntity<AdminProductResponse> {
        val product = productService.updateProduct(id, productUpdateRequest)
        return ResponseEntity.ok(product.toAdminProductResponseDTO())
    }

    @PatchMapping("/{id}/availability")
    fun updateAvailability(
        @PathVariable id: Long,
        @RequestBody availabilityRequest: AdminProductAvailabilityRequest,
    ): ResponseEntity<AdminProductResponse> {
        val product = productService.updateAvailability(id, availabilityRequest)
        return ResponseEntity.ok(product.toAdminProductResponseDTO())
    }
}
