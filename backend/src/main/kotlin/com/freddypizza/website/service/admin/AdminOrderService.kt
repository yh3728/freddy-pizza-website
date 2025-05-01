package com.freddypizza.website.service.admin

import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.exception.InvalidOrderStatusException
import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.repository.OrderRepository
import com.freddypizza.website.request.admin.AdminOrderStatusRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AdminOrderService(
    private val orderRepository: OrderRepository,
) {
    fun getAllOrders(): List<OrderEntity> = orderRepository.findAll()

    fun getOrderById(id: Long): OrderEntity? = orderRepository.findByIdOrNull(id)

    fun getOrdersByRole(
        staffRole: StaffRole,
        status: OrderStatus?,
    ): List<OrderEntity> {
        val allowedStatuses =
            when (staffRole) {
                StaffRole.COOK -> listOf(OrderStatus.NEW, OrderStatus.IN_PROGRESS)
                StaffRole.DELIVERY -> listOf(OrderStatus.READY_FOR_DELIVERY, OrderStatus.OUT_FOR_DELIVERY)
                StaffRole.ADMIN -> OrderStatus.entries
            }

        return if (status != null) {
            if (status in allowedStatuses) {
                getOrdersByStatus(status)
            } else {
                throw InvalidOrderStatusException("Вы не можете просматривать заказы со статусом $status")
            }
        } else {
            getOrdersByStatusIn(allowedStatuses)
        }
    }

    fun updateOrderStatusByRole(
        id: Long,
        staffRole: StaffRole,
        orderStatusRequest: AdminOrderStatusRequest,
    ): OrderEntity =
        when (staffRole) {
            StaffRole.COOK -> {
                if (orderStatusRequest.status == OrderStatus.IN_PROGRESS || orderStatusRequest.status == OrderStatus.READY_FOR_DELIVERY) {
                    updateOrderStatus(id, orderStatusRequest)
                } else {
                    throw InvalidOrderStatusException(
                        "Нельзя установить статус ${orderStatusRequest.status} для роли ${staffRole.displayName}",
                    )
                }
            }

            StaffRole.DELIVERY -> {
                if (orderStatusRequest.status == OrderStatus.OUT_FOR_DELIVERY || orderStatusRequest.status == OrderStatus.DELIVERED) {
                    updateOrderStatus(id, orderStatusRequest)
                } else {
                    throw InvalidOrderStatusException(
                        "Нельзя установить статус ${orderStatusRequest.status} для роли ${staffRole.displayName}",
                    )
                }
            }

            StaffRole.ADMIN -> {
                updateOrderStatus(id, orderStatusRequest)
            }
        }

    private fun updateOrderStatus(
        id: Long,
        orderStatusRequest: AdminOrderStatusRequest,
    ): OrderEntity {
        val existingOrder = orderRepository.findByIdOrNull(id) ?: throw ProductNotFoundException()
        return orderRepository.save(existingOrder.copy(status = orderStatusRequest.status))
    }

    private fun getOrdersByStatus(status: OrderStatus): List<OrderEntity> = orderRepository.findByStatus(status)

    private fun getOrdersByStatusIn(statusList: List<OrderStatus>): List<OrderEntity> = orderRepository.findByStatusIn(statusList)
}
