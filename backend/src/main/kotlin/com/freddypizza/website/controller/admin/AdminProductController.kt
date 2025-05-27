package com.freddypizza.website.controller.admin

import com.freddypizza.website.exception.ErrorResponse
import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.request.admin.AdminProductImageRequest
import com.freddypizza.website.request.admin.AdminProductQuantityRequest
import com.freddypizza.website.request.admin.AdminProductRequest
import com.freddypizza.website.request.admin.AdminProductUpdateRequest
import com.freddypizza.website.response.admin.AdminProductResponse
import com.freddypizza.website.service.admin.AdminProductService
import com.freddypizza.website.util.toAdminProductResponseDTO
import com.freddypizza.website.util.toProductEntity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/menu")
class AdminProductController(
    private val productService: AdminProductService,
) {
    @PostMapping
    @Operation(summary = "Добавить новый продукт в меню")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Продукт успешно добавлен"),
            ApiResponse(
                responseCode = "409",
                description = "Продукт с таким именем уже существует",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun addItem(
        @RequestBody productRequestDTO: AdminProductRequest,
    ): ResponseEntity<AdminProductResponse> {
        val productEntity = productRequestDTO.toProductEntity()
        val productResponse = productService.addProduct(productEntity).toAdminProductResponseDTO()
        return ResponseEntity(productResponse, HttpStatus.CREATED)
    }

    @GetMapping
    @Operation(summary = "Получить список всех продуктов в меню")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список продуктов получен"),
        ],
    )
    fun getAllItems(): ResponseEntity<List<AdminProductResponse>> =
        ResponseEntity.ok(
            productService.getAllProducts().map {
                it.toAdminProductResponseDTO()
            },
        )

    @GetMapping("/{id}")
    @Operation(summary = "Получить продукт по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Продукт найден"),
            ApiResponse(
                responseCode = "404",
                description = "Продукт не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun getItemById(
        @PathVariable id: Long,
    ): ResponseEntity<AdminProductResponse> {
        val item = productService.getProductById(id) ?: throw ProductNotFoundException()
        return ResponseEntity.ok(item.toAdminProductResponseDTO())
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить продукт по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Продукт удалён"),
            ApiResponse(
                responseCode = "404",
                description = "Продукт не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun deleteItem(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить продукт по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Продукт обновлён"),
            ApiResponse(
                responseCode = "404",
                description = "Продукт не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun updateItem(
        @PathVariable id: Long,
        @RequestBody productUpdateRequest: AdminProductUpdateRequest,
    ): ResponseEntity<AdminProductResponse> {
        val product = productService.updateProduct(id, productUpdateRequest)
        return ResponseEntity.ok(product.toAdminProductResponseDTO())
    }

    @PatchMapping("/{id}/quantity")
    @Operation(summary = "Изменить количество продукта по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Количество обновлено"),
            ApiResponse(
                responseCode = "404",
                description = "Продукт не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun updateQuantity(
        @PathVariable id: Long,
        @RequestBody availabilityRequest: AdminProductQuantityRequest,
    ): ResponseEntity<AdminProductResponse> {
        val product = productService.updateQuantity(id, availabilityRequest)
        return ResponseEntity.ok(product.toAdminProductResponseDTO())
    }

    @PostMapping("/{id}/image")
    @Operation(summary = "Загрузить фото продукта")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Фото обновлено"),
            ApiResponse(
                responseCode = "404",
                description = "Продукт не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun uploadProductPhoto(
        @PathVariable id: Long,
        @ModelAttribute request: AdminProductImageRequest,
    ): ResponseEntity<AdminProductResponse> {
        val product = productService.updateImagePath(id, request)
        return ResponseEntity.ok(product.toAdminProductResponseDTO())
    }
}
