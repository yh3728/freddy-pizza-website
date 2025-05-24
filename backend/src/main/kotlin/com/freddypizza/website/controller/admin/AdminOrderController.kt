package com.freddypizza.website.controller.admin

import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.exception.ErrorResponse
import com.freddypizza.website.exception.OrderNotFoundException
import com.freddypizza.website.request.admin.AdminOrderStatusRequest
import com.freddypizza.website.response.admin.*
import com.freddypizza.website.service.admin.AdminOrderService
import com.freddypizza.website.util.toAdminOrderFullResponse
import com.freddypizza.website.util.toAdminOrderShortResponse
import com.freddypizza.website.util.toCookOrderShortResponse
import com.freddypizza.website.util.toDeliveryOrderResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/orders")
class AdminOrderController(
    private val orderService: AdminOrderService,
) {
    @GetMapping
    @Operation(
        summary =
        "Получить список всех заказов + фильтровать по нужному статусу по необходимости" +
                "(ADMIN - все заказы," +
                " COOK - NEW и IN_PROGRESS," +
                " DELIVERY - READY_FOR_DELIVERY и OUT_FOR_DELIVERY",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список заказов получен (Один из возможных форматов заказа в зависимости от роли)",
                content = [
                    Content(
                        array =
                        ArraySchema(
                            schema =
                            Schema(
                                oneOf = [
                                    CookOrderShortResponse::class,
                                    DeliveryOrderResponse::class,
                                    AdminOrderShortResponse::class,
                                ],
                            ),
                        ),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Текущая роль не может просматривать заказы с данным статусом",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun getOrders(
        @RequestParam status: OrderStatus?,
        @AuthenticationPrincipal user: CustomStaffUserDetails,
    ): ResponseEntity<List<BaseOrderResponse>> {
        val role = user.getRole()
        val orders = orderService.getOrdersByRole(role, status)
        println(status)
        val response =
            orders.map { order ->
                when (user.getRole()) {
                    StaffRole.COOK -> order.toCookOrderShortResponse()
                    StaffRole.DELIVERY -> order.toDeliveryOrderResponse()
                    StaffRole.ADMIN -> order.toAdminOrderShortResponse()
                }
            }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить продукт по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Заказ найден"),
            ApiResponse(
                responseCode = "404",
                description = "Заказ не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun getOrderById(
        @PathVariable id: Long,
    ): ResponseEntity<AdminOrderFullResponse> {
        val order = orderService.getOrderById(id) ?: throw OrderNotFoundException()
        return ResponseEntity.ok(order.toAdminOrderFullResponse())
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Изменить статус заказа по ID")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Статус обновлен (Один из возможных форматов заказа в зависимости от роли)",
                content = [
                    Content(
                        array =
                            ArraySchema(
                                schema =
                                    Schema(
                                        oneOf = [
                                            CookOrderShortResponse::class,
                                            DeliveryOrderResponse::class,
                                            AdminOrderShortResponse::class,
                                        ],
                                    ),
                            ),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Заказ не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Нельзя установить данный статус для текущей роли",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestBody orderStatusRequest: AdminOrderStatusRequest,
        @AuthenticationPrincipal user: CustomStaffUserDetails,
    ): ResponseEntity<BaseOrderResponse> {
        println(orderStatusRequest)
        val order = orderService.updateOrderStatusByRole(id, user.getRole(), orderStatusRequest)
        val response =
            when (user.getRole()) {
                StaffRole.COOK -> order.toCookOrderShortResponse()
                StaffRole.DELIVERY -> order.toDeliveryOrderResponse()
                StaffRole.ADMIN -> order.toAdminOrderShortResponse()
            }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/status-options")
    @Operation(summary = "Получить все возможные статусы (для фильтрации)")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Статусы получены")],
    )
    fun getStatusOptions(
        @AuthenticationPrincipal user: CustomStaffUserDetails,
    ): ResponseEntity<List<OrderStatus>> {
        val availableStatuses =
            when (user.getRole()) {
                StaffRole.COOK -> listOf(OrderStatus.IN_PROGRESS, OrderStatus.READY_FOR_DELIVERY)
                StaffRole.DELIVERY -> listOf(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED)
                StaffRole.ADMIN -> OrderStatus.entries.toList()
            }
        return ResponseEntity.ok(availableStatuses)
    }
}
