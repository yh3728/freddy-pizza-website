package com.freddypizza.website.util

class DataInitializer {
//    // ! НЕ ЗАПУСКАТЬ ВМЕСТЕ С ТЕСТАМИ, ОБЯЗАТЕЛЬНО ЗАКОММЕНТИРОВАТЬ
//    // Создание пользователя админа
//    @Component
//    class AdminDataInitializer(
//        private val staffService: StaffService,
//    ) {
//        @PostConstruct
//        fun init() {
//            val adminUsername = "admin"
//            val adminPassword = "admin"
//            val adminRole = StaffRole.ADMIN
//
//            staffService.addStaff(StaffEntity(username = adminUsername, password = adminPassword, role = adminRole))
//            println("Admin account created successfully.")
//        }
//    }
//
//    // Создание заказов и продуктов
//    @Component
//    class TestDataGenerator(
//        private val productRepository: ProductRepository,
//        private val orderRepository: OrderRepository,
//        private val orderItemRepository: OrderItemRepository,
//    ) {
//        private val descriptions =
//            listOf(
//                "Просто вкусно.",
//                "Идеальное сочетание вкуса и текстуры.",
//                "Лёгкое и сытное блюдо для любого времени суток.",
//                "Пикантное, пряное, с насыщенным вкусом, подойдёт любителям острых ощущений.",
//                "Настоящий праздник вкуса в каждом кусочке! Попробуйте это блюдо и вы влюбитесь в него с первого укуса. Создано по оригинальному рецепту, передающему атмосферу настоящего итальянского ресторана.",
//                "Это блюдо подходит как для будней, так и для особенных вечеров. Оно объединило в себе классику и современность, свежесть и насыщенность.",
//            )
//
//        private val ingredientsSamples =
//            listOf(
//                "сыр, соус",
//                "томаты, моцарелла, базилик",
//                "бекон, курица, грибы, лук, перец",
//                "говядина, солёные огурцы, красный лук, кетчуп, горчица",
//                "рис, нори, лосось, сыр, огурец, авокадо, кунжут, васаби, соевый соус",
//                "сыр чеддер, зелень, соус ранч, халапеньо, курица терияки, чесночный соус",
//                "молочный шоколад, клубника, мята, сахарная пудра, сливки",
//                "минеральная вода, лёд, лимон, листья мяты, сироп",
//            )
//
//        private val comments =
//            listOf(
//                "Привезите без соуса.",
//                "Без лука и помидоров, пожалуйста.",
//                "Не забудьте вилки и салфетки!",
//                "Клиент просил доставить строго к 18:00.",
//                "Большая просьба: разделить роллы на два контейнера и положить в разные пакеты. Спасибо!",
//                "Оставьте заказ у двери, позвоните в звонок и уходите.",
//                "В прошлый раз привезли холодным, надеюсь в этот раз будет горячее :)",
//                "Отличное обслуживание, всегда заказываю у вас!",
//                "Очень важно — ребёнок аллергик. Без орехов и морепродуктов.",
//                "Это подарок, пожалуйста, оформите красиво, можно ленточку или открытку, если есть.",
//            )
//
//        private val customerNames =
//            listOf(
//                "Иван Иванов",
//                "Мария Петрова",
//                "Алексей Смирнов",
//                "Елена Кузнецова",
//                "Дмитрий Орлов",
//                "Светлана Фролова",
//                "Аркадий Зайцев",
//                "Юлия Виноградова",
//                "Михаил Егоров",
//                "Наталья Сидорова",
//            )
//
//        private val productNames =
//            listOf(
//                "Пепперони",
//                "Маргарита",
//                "Четыре сыра",
//                "Цезарь",
//                "Филадельфия",
//                "Бургер BBQ",
//                "Паста Болоньезе",
//                "Спринг роллы",
//                "Чизкейк Нью-Йорк",
//                "Лимонад с мятой",
//                "Шоколадный торт",
//                "Морс клюквенный",
//                "Брускетта с томатами",
//            )
//
//        private val categories = ProductCategory.entries
//        private val paymentTypes = PaymentType.entries
//        private val random = Random(System.currentTimeMillis())
//
//        @PostConstruct
//        fun generateTestData() {
//            val products = createVariedTestProducts()
//            createVariedTestOrders(products)
//        }
//
//        private fun createVariedTestProducts(): List<ProductEntity> {
//            val products = mutableListOf<ProductEntity>()
//            repeat(50) {
//                val name = productNames.random() + " ${random.nextInt(1, 100)}"
//                val price = BigDecimal(random.nextInt(100, 2500))
//                val category = categories.random()
//                val description = descriptions.random()
//                val quantity = random.nextInt(0, 100)
//                val weight = random.nextInt(50, 2000)
//                val ingredients = ingredientsSamples.random()
//
//                products +=
//                    ProductEntity(
//                        name = name,
//                        price = price,
//                        category = category,
//                        description = description,
//                        quantity = quantity,
//                        weight = weight,
//                        ingredients = ingredients,
//                    )
//            }
//            return productRepository.saveAll(products)
//        }
//
//        private fun createVariedTestOrders(products: List<ProductEntity>) {
//            val statuses = OrderStatus.entries.toTypedArray()
//            repeat(30) {
//                val customerName = customerNames.random()
//                val phone = "+7-${random.nextInt(900, 999)}-${random.nextInt(100, 999)}-${random.nextInt(1000, 9999)}"
//                val address = "г. Ярославль, ул. ${randomStreetName()}, д. ${random.nextInt(1, 100)}, кв. ${random.nextInt(1, 200)}"
//                val comment = comments.random()
//                val status = statuses.random()
//                val payment = paymentTypes.random()
//                val chars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"
//                val trackingCode = (1..6).map { chars.random() }.joinToString("")
//                val createdAt = LocalDateTime.now().minusDays(random.nextLong(0, 30))
//
//                val order =
//                    OrderEntity(
//                        customerName = customerName,
//                        phone = phone,
//                        address = address,
//                        totalPrice = BigDecimal.ZERO,
//                        status = status,
//                        comment = comment,
//                        createdAt = createdAt,
//                        items = mutableListOf(),
//                        trackingCode = trackingCode,
//                        payment = payment,
//                    )
//
//                val savedOrder = orderRepository.save(order)
//                val orderItems = generateOrderItems(products, savedOrder)
//                orderItemRepository.saveAll(orderItems)
//
//                val total =
//                    orderItems.fold(BigDecimal.ZERO) { acc, item ->
//                        acc + (item.product.price.multiply(BigDecimal(item.quantity)))
//                    }
//                savedOrder.totalPrice = total
//                orderRepository.save(savedOrder)
//            }
//        }
//
//        private fun generateOrderItems(
//            products: List<ProductEntity>,
//            order: OrderEntity,
//        ): List<OrderItemEntity> {
//            val items = mutableListOf<OrderItemEntity>()
//            repeat(random.nextInt(1, 10)) {
//                val product = products[random.nextInt(products.size)]
//                val quantity = random.nextInt(1, 10)
//
//                items +=
//                    OrderItemEntity(
//                        quantity = quantity,
//                        product = product,
//                        order = order,
//                    )
//            }
//            return items
//        }
//
//        private fun randomStreetName(): String {
//            val streetNames =
//                listOf(
//                    "Ленина",
//                    "Тверская",
//                    "Пушкина",
//                    "Гагарина",
//                    "Невский проспект",
//                    "Б. Хмельницкого",
//                    "Советская",
//                    "Железнодорожная",
//                    "Садовая",
//                    "Центральная",
//                )
//            return streetNames.random()
//        }
//    }
}
