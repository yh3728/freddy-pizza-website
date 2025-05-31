package com.freddypizza.website

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

// // Создание пользователя админа
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
// // Создание заказов и продуктов
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
//            val chars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"
//            val trackingCode = (1..3).map { chars.random() }.joinToString("")
//            val order =
//                OrderEntity(
//                    customerName = customerName,
//                    phone = phone,
//                    address = address,
//                    status = status,
//                    totalPrice = totalPrice,
//                    comment = comment,
//                    createdAt = LocalDateTime.now(),
//                    items = mutableListOf(),
//                    trackingCode = trackingCode,
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
//                ProductEntity(
//                    name = "Pizza",
//                    price = BigDecimal(400),
//                    category = ProductCategory.PIZZA,
//                    quantity = 100,
//                    weight = 100,
//                    ingredients = "tomato cheese",
//                ),
//                ProductEntity(
//                    name = "Burger",
//                    price = BigDecimal(150),
//                    category = ProductCategory.SNACK,
//                    quantity = 0,
//                    weight = 200,
//                    ingredients = "cucmber straw",
//                ),
//                ProductEntity(
//                    name = "Sushi",
//                    price = BigDecimal(600),
//                    category = ProductCategory.ROLLS,
//                    quantity = 10,
//                    weight = 50,
//                    ingredients = "skdfjsdf sdsdfg",
//                ),
//                ProductEntity(
//                    name = "Pasta",
//                    price = BigDecimal(300),
//                    category = ProductCategory.SNACK,
//                    quantity = 1,
//                    weight = 1000,
//                    ingredients = "dfsg dfg",
//                ),
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
