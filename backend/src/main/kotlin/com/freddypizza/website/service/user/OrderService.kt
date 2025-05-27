package com.freddypizza.website.service.user

import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.entity.OrderItemEntity
import com.freddypizza.website.exception.EmptyOrderException
import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.repository.OrderRepository
import com.freddypizza.website.repository.ProductRepository
import com.freddypizza.website.request.user.CreateOrderRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class OrderService (
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
){
    fun getAllOrders(): List<OrderEntity> = orderRepository.findAll()
    fun getOrderById(orderId: Long): OrderEntity? = orderRepository.findByIdOrNull(orderId)
    fun createOrder(request: CreateOrderRequest): OrderEntity {
        if (request.items.isEmpty()) throw EmptyOrderException()
        val orderEntity = OrderEntity(
            customerName = request.customerName,
            phone = request.phone,
            address = request.address,
            comment = request.comment
        )
        val orderItems = request.items.map { item ->
            val product = productRepository.findByIdOrNull(item.productId)
                ?: throw ProductNotFoundException()
            OrderItemEntity(
                quantity = item.quantity,
                order = orderEntity,
                product = product
            )
        }
        val totalPrice = orderItems.sumOf { it.product.price * it.quantity.toBigDecimal() }
        orderEntity.items = orderItems.toMutableList()
        orderEntity.totalPrice = totalPrice
        return orderRepository.save(orderEntity)
    }

}
