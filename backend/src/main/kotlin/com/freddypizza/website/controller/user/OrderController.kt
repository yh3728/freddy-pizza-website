package com.freddypizza.website.controller.user

import com.freddypizza.website.exception.ErrorResponse
import com.freddypizza.website.exception.OrderNotFoundException
import com.freddypizza.website.request.user.CreateOrderRequest
import com.freddypizza.website.response.user.OrderResponse
import com.freddypizza.website.service.user.OrderService
import com.freddypizza.website.util.toOrderDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @Operation(summary = "Создать новый заказ")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Заказ успешно создан"),
        ],
    )
    @PostMapping
    fun createOrder(
        @RequestBody createOrderRequest: CreateOrderRequest,
    ): ResponseEntity<OrderResponse> {
        val orderResponse = orderService.createOrder(createOrderRequest).toOrderDTO()
        return ResponseEntity(orderResponse, HttpStatus.CREATED)
    }

    @Operation(summary = "Получить заказ по трек коду")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Заказ успешно найден"),
            ApiResponse(
                responseCode = "404",
                description = "Заказ не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/{code}")
    fun getOrderByCode(
        @PathVariable code: String,
    ): ResponseEntity<OrderResponse> {
        val item = orderService.getOrderByCode(code) ?: throw OrderNotFoundException()
        return ResponseEntity.ok(item.toOrderDTO())
    }
}
