package com.freddypizza.website.service.user

import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.entity.OrderItemEntity
import com.freddypizza.website.exception.EmptyOrderException
import com.freddypizza.website.exception.FailedGenerateCodeException
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
    fun getOrderByCode(code: String): OrderEntity? = orderRepository.findByTrackingCode(code)
    fun createOrder(request: CreateOrderRequest): OrderEntity {
        val uniqCode = generateUniqueTrackingCode()
        if (request.items.isEmpty()) throw EmptyOrderException()
        val orderEntity = OrderEntity(
            customerName = request.customerName,
            phone = request.phone,
            address = request.address,
            comment = request.comment,
            trackingCode = uniqCode
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

    private fun generateUniqueTrackingCode(): String {
        val chars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var code: String
        var attempts = 0
        code = (1..6).map { chars.random() }.joinToString("")
        while (orderRepository.findByTrackingCode(code) != null) {
            if (attempts > 30) throw FailedGenerateCodeException("Ошибка в генерации кода заказа")
            code = (1..6).map { chars.random() }.joinToString("")
            attempts++
        }
        return code
    }

}
