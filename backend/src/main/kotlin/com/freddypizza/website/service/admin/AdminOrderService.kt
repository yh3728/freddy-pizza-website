package com.freddypizza.website.service.admin

import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.exception.InvalidOrderStatusException
import com.freddypizza.website.exception.OrderNotFoundException
import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.repository.OrderRepository
import com.freddypizza.website.request.admin.AdminOrderStatusRequest
import com.freddypizza.website.request.admin.AdminProductQuantityRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AdminOrderService(
    private val orderRepository: OrderRepository,
    private val staffService: StaffService,
    private val productService: AdminProductService,
) {
    fun getAllOrders(): List<OrderEntity> = orderRepository.findAll()

    fun getOrderById(id: Long): OrderEntity? = orderRepository.findByIdOrNull(id)

    fun getOrdersByRole(
        status: OrderStatus?,
        currentUser: CustomStaffUserDetails,
    ): List<OrderEntity> {
        val allowedStatuses =
            when (currentUser.getRole()) {
                StaffRole.COOK -> listOf(OrderStatus.NEW, OrderStatus.IN_PROGRESS)
                StaffRole.DELIVERY -> listOf(OrderStatus.READY_FOR_DELIVERY, OrderStatus.OUT_FOR_DELIVERY)
                StaffRole.ADMIN -> OrderStatus.entries
            }

        if (status != null && status !in allowedStatuses) {
            throw InvalidOrderStatusException("Вы не можете просматривать заказы со статусом $status")
        }

        val orders =
            if (status != null) {
                getOrdersByStatus(status)
            } else {
                getOrdersByStatusIn(allowedStatuses)
            }

        return when (currentUser.getRole()) {
            StaffRole.DELIVERY -> {
                orders.filter {
                    when (it.status) {
                        OrderStatus.READY_FOR_DELIVERY -> true
                        OrderStatus.OUT_FOR_DELIVERY ->
                            it.assignedDelivery?.id == currentUser.id
                        else -> false
                    }
                }
            }
            else -> orders
        }
    }

    fun updateOrderStatusByRole(
        id: Long,
        orderStatusRequest: AdminOrderStatusRequest,
        currentUser: CustomStaffUserDetails,
    ): OrderEntity {
        val existingOrder = orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException()

        return when (currentUser.getRole()) {
            StaffRole.COOK -> {
                if (orderStatusRequest.status == OrderStatus.IN_PROGRESS || orderStatusRequest.status == OrderStatus.READY_FOR_DELIVERY) {
                    updateOrderStatus(existingOrder, orderStatusRequest.status)
                } else {
                    throw InvalidOrderStatusException(
                        "Нельзя установить статус ${orderStatusRequest.status} для роли ${currentUser.getRole().displayName}",
                    )
                }
            }

            StaffRole.DELIVERY -> {
                if (orderStatusRequest.status == OrderStatus.OUT_FOR_DELIVERY || orderStatusRequest.status == OrderStatus.DELIVERED) {
                    val updatedOrder =
                        existingOrder.copy(
                            status = orderStatusRequest.status,
                            assignedDelivery =
                                if (orderStatusRequest.status == OrderStatus.OUT_FOR_DELIVERY) {
                                    staffService.getStaffById(currentUser.id)
                                } else {
                                    existingOrder.assignedDelivery
                                },
                        )
                    orderRepository.save(updatedOrder)
                } else {
                    throw InvalidOrderStatusException(
                        "Нельзя установить статус ${orderStatusRequest.status} для роли ${currentUser.getRole().displayName}",
                    )
                }
            }

            StaffRole.ADMIN -> {
                if (orderStatusRequest.status == OrderStatus.CANCELLED) {
                    cancelOrder(existingOrder)
                } else {
                    updateOrderStatus(existingOrder, orderStatusRequest.status)
                }
            }
        }
    }

    private fun cancelOrder(order: OrderEntity): OrderEntity {
        order.items.forEach { item ->
            val product = productService.getProductById(item.id) ?: throw ProductNotFoundException()
            val newQuantity = product.quantity + item.quantity
            productService.updateQuantity(item.product.id, AdminProductQuantityRequest(newQuantity))
        }
        val cancelledOrder = order.copy(status = OrderStatus.CANCELLED)
        return orderRepository.save(cancelledOrder)
    }

    private fun updateOrderStatus(
        order: OrderEntity,
        newStatus: OrderStatus,
    ): OrderEntity = orderRepository.save(order.copy(status = newStatus))

    private fun getOrdersByStatus(status: OrderStatus): List<OrderEntity> = orderRepository.findByStatus(status)

    private fun getOrdersByStatusIn(statusList: List<OrderStatus>): List<OrderEntity> = orderRepository.findByStatusIn(statusList)
}
