package com.freddypizza.website

import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.entity.OrderItemEntity
import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.ProductCategory
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.repository.OrderItemRepository
import com.freddypizza.website.repository.OrderRepository
import com.freddypizza.website.repository.ProductRepository
import com.freddypizza.website.repository.StaffRepository
import com.freddypizza.website.service.admin.StaffService
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
//
//// Создание пользователя админа
// @Component
// class AdminDataInitializer(
//    private val staffRepository: StaffRepository,
//    private val staffService: StaffService,
// ) {
//    @PostConstruct
//    fun init() {
//        val adminUsername = "admin"
//        val adminPassword = "admin"
//        val adminRole = StaffRole.ADMIN
//
//        staffService.addStaff(StaffEntity(username = adminUsername, password = adminPassword, role = adminRole))
//        println("Admin account created successfully.")
//    }
// }
//
//// Создание заказов и продуктов
// @Component
// class TestOrderGenerator(
//    @Autowired private val orderRepository: OrderRepository,
//    @Autowired private val productRepository: ProductRepository,
//    @Autowired private val orderItemRepository: OrderItemRepository,
// ) {
//    @PostConstruct
//    fun generateTestOrders() {
//        val products = ensureProductsExist()
//        val statuses = OrderStatus.entries.toTypedArray()
//        val random = Random(System.currentTimeMillis())
//
//        repeat(10) {
//            val status = statuses[random.nextInt(statuses.size)]
//            val customerName = "Customer${random.nextInt(1000, 9999)}"
//            val phone = "123-456-${random.nextInt(1000, 9999)}"
//            val address = "Address ${random.nextInt(1, 100)}"
//            val totalPrice = BigDecimal(random.nextInt(100, 1000))
//            val comment = "Комментарий для заказа ${it + 1}"
//
//            val order =
//                OrderEntity(
//                    customerName = customerName,
//                    phone = phone,
//                    address = address,
//                    status = status,
//                    totalPrice = totalPrice,
//                    comment = comment,
//                    createdAt = LocalDateTime.now(),
//                    items = emptyList()
//                )
//
//            val savedOrder = orderRepository.save(order)
//
//            val orderItems = generateRandomOrderItems(products, savedOrder)
//
//            orderItemRepository.saveAll(orderItems)
//        }
//    }
//
//    private fun ensureProductsExist(): List<ProductEntity> {
//        var products = productRepository.findAll()
//        if (products.isEmpty()) {
//            products = createTestProducts()
//        }
//        return products
//    }
//
//    private fun createTestProducts(): List<ProductEntity> {
//        val products =
//            listOf(
//                ProductEntity(name = "Pizza", price = BigDecimal(400), category = ProductCategory.PIZZA),
//                ProductEntity(name = "Burger", price = BigDecimal(150), category = ProductCategory.SNACK),
//                ProductEntity(name = "Sushi", price = BigDecimal(600), category = ProductCategory.ROLLS),
//                ProductEntity(name = "Pasta", price = BigDecimal(300), category = ProductCategory.SNACK),
//            )
//        return productRepository.saveAll(products)
//    }
//
//    private fun generateRandomOrderItems(
//        products: List<ProductEntity>,
//        order: OrderEntity,
//    ): List<OrderItemEntity> {
//        val random = Random(System.currentTimeMillis())
//        val items = mutableListOf<OrderItemEntity>()
//
//        repeat(random.nextInt(1, 4)) {
//            val product = products[random.nextInt(products.size)]
//            val quantity = random.nextInt(1, 5)
//
//            val orderItem =
//                OrderItemEntity(
//                    quantity = quantity,
//                    product = product,
//                    order = order,
//                )
//
//            items.add(orderItem)
//        }
//
//        return items
//    }
// }
