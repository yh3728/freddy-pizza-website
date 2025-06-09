package com.freddypizza.website.controller.user

import com.freddypizza.website.exception.ErrorResponse
import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.response.user.CardItemResponse
import com.freddypizza.website.response.user.ProductResponse
import com.freddypizza.website.service.user.ProductService
import com.freddypizza.website.util.toCardItemDTO
import com.freddypizza.website.util.toProductResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/menu")
class ProductController(
    private val productService: ProductService,
) {
    @Operation(summary = "Получить все продукты в меню (для главной страницы меню)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Продукты получены"),
        ],
    )
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> =
        ResponseEntity.ok(
            productService.getAllProducts().map {
                it.toProductResponse()
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
    fun getProductById(
        @PathVariable id: Long,
    ): ResponseEntity<CardItemResponse> {
        val product = productService.getProductById(id) ?: throw ProductNotFoundException()
        return ResponseEntity.ok(product.toCardItemDTO())
    }
}
