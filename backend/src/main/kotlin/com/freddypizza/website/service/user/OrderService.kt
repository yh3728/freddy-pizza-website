package com.freddypizza.website.service.user

import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.entity.OrderItemEntity
import com.freddypizza.website.exception.*
import com.freddypizza.website.repository.OrderRepository
import com.freddypizza.website.repository.ProductRepository
import com.freddypizza.website.request.admin.AdminProductQuantityRequest
import com.freddypizza.website.request.user.CreateOrderRequest
import com.freddypizza.website.service.admin.AdminProductService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val productService: AdminProductService,
) {
    fun getOrderByCode(code: String): OrderEntity? = orderRepository.findByTrackingCode(code)

    fun createOrder(request: CreateOrderRequest): OrderEntity {
        val uniqCode = generateUniqueTrackingCode()
        if (request.items.isEmpty()) throw EmptyOrderException()
        val productsById = productRepository.findAllById(request.items.map { it.productId }).associateBy { it.id }

        val stockErrors =
            request.items.mapNotNull { item ->
                val product =
                    productsById[item.productId]
                        ?: throw ProductNotFoundException()

                if (product.quantity < item.quantity) {
                    StockError(
                        productId = product.id,
                        requestedQuantity = item.quantity,
                        availableQuantity = product.quantity,
                    )
                } else {
                    null
                }
            }

        if (stockErrors.isNotEmpty()) throw NotEnoughStockException(errors = stockErrors)
        val orderEntity =
            OrderEntity(
                customerName = request.customerName,
                phone = request.phone,
                address = request.address,
                comment = request.comment,
                trackingCode = uniqCode,
            )

        val orderItems =
            request.items.map { item ->
                val product =
                    productRepository.findByIdOrNull(item.productId)
                        ?: throw ProductNotFoundException()
                productService.updateQuantity(item.productId, AdminProductQuantityRequest(product.quantity - item.quantity))
                OrderItemEntity(
                    quantity = item.quantity,
                    order = orderEntity,
                    product = product,
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
