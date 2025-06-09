package com.freddypizza.website.controller.admin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.entity.OrderItemEntity
import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.PaymentType
import com.freddypizza.website.enums.ProductCategory
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.exception.InvalidOrderStatusException
import com.freddypizza.website.exception.OrderNotFoundException
import com.freddypizza.website.request.admin.AdminOrderStatusRequest
import com.freddypizza.website.service.admin.AdminOrderService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
class AdminOrderControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        @MockkBean private val orderService: AdminOrderService,
    ) {
        private val mapper = jacksonObjectMapper()

        private val testProduct =
            ProductEntity(
                id = 1L,
                name = "Test Pizza",
                price = BigDecimal("10.99"),
                category = ProductCategory.PIZZA,
            )

        private var testOrder =
            OrderEntity(
                id = 1L,
                status = OrderStatus.NEW,
                totalPrice = BigDecimal("25.98"),
                items = mutableListOf(),
                comment = "Test comment",
                customerName = "John Doe",
                phone = "+1234567890",
                address = "Test Address 123",
                payment = PaymentType.CARD,
                trackingCode = "ABCDEF",
            )
        private val testOrderItem =
            OrderItemEntity(
                id = 1L,
                product = testProduct,
                quantity = 2,
                order = testOrder,
            )

        init {
            testOrder =
                testOrder.copy(
                    items = mutableListOf(testOrderItem),
                )
        }

        val adminStaff =
            CustomStaffUserDetails(
                StaffEntity(1, "admin", "password", StaffRole.ADMIN),
            )
        val cookStaff =
            CustomStaffUserDetails(
                StaffEntity(1, "cook", "password", StaffRole.COOK),
            )
        val deliveryStaff = CustomStaffUserDetails(StaffEntity(1, "delivery", "password", StaffRole.DELIVERY))

        @BeforeEach
        fun setup() {
            every { orderService.getOrderById(1L) } returns testOrder
            every { orderService.getOrderById(999L) } throws OrderNotFoundException()

            every {
                orderService.updateOrderStatusByRole(any(), any(), any())
            } answers {
                val request = thirdArg<AdminOrderStatusRequest>()
                testOrder.copy(status = request.status)
            }
        }

        /**
         * Тест для получения списка заказов пользователем с ролью ADMIN.
         * Ожидается статус 200 (OK), а также что в ответе будет хотя бы один заказ с правильными полями:
         * id, status и название продукта в элементах заказа.
         */
        @Test
        fun `getOrders should return all orders for ADMIN role`() {
            every { orderService.getOrdersByRole(StaffRole.ADMIN, null) } returns listOf(testOrder)
            mockMvc
                .get("/admin/orders") {
                    with(user(adminStaff))
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$[0].id") { value(1) }
                    jsonPath("$[0].status") { value("NEW") }
                    jsonPath("$[0].items[0].productName") { value("Test Pizza") }
                }
            verify { orderService.getOrdersByRole(StaffRole.ADMIN, null) }
        }

        /**
         * Тест для получения списка новых заказов пользователем с ролью COOK.
         * Ожидается статус 200 (OK) и что статус возвращённого заказа равен "NEW".
         */
        @Test
        fun `getOrders should return NEW orders for COOK role`() {
            every { orderService.getOrdersByRole(StaffRole.COOK, null) } returns listOf(testOrder)

            mockMvc
                .get("/admin/orders") {
                    with(user(cookStaff))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$[0].status") { value("NEW") }
                }

            verify { orderService.getOrdersByRole(StaffRole.COOK, null) }
        }

        /**
         * Тест обработки некорректного запроса статуса для COOK.
         * Пользователь COOK запрашивает заказы со статусом READY_FOR_DELIVERY, что недопустимо,
         * в результате ожидаем статус 400 (Bad Request) и сообщение об ошибке.
         */
        @Test
        fun `getOrders should 400 for COOK requesting READY_FOR_DELIVERY`() {
            every { orderService.getOrdersByRole(StaffRole.COOK, OrderStatus.READY_FOR_DELIVERY) } throws InvalidOrderStatusException()

            mockMvc
                .get("/admin/orders?status=READY_FOR_DELIVERY") {
                    with(user(cookStaff))
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("BAD_REQUEST") }
                    jsonPath("$.message") { value("Некорректный статус") }
                }
        }

        /**
         * Тест для получения готовых к доставке заказов пользователем с ролью DELIVERY.
         * Ожидается статус 200 (OK) и что статус заказа в ответе равен "READY_FOR_DELIVERY".
         */
        @Test
        fun `getOrders should return READY_FOR_DELIVERY for DELIVERY role`() {
            val readyOrder = testOrder.copy(status = OrderStatus.READY_FOR_DELIVERY)
            every { orderService.getOrdersByRole(StaffRole.DELIVERY, null) } returns listOf(readyOrder)

            mockMvc
                .get("/admin/orders") {
                    with(user(deliveryStaff))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$[0].status") { value("READY_FOR_DELIVERY") }
                }

            verify { orderService.getOrdersByRole(StaffRole.DELIVERY, null) }
        }

        /**
         * Тест получения заказа по ID пользователем с ролью ADMIN.
         * Ожидается статус 200 (OK) и в ответе должны присутствовать поля id, customerName и количество в элементах заказа.
         */
        @Test
        fun `getOrderById should return full response for ADMIN`() {
            every { orderService.getOrderById(1L) } returns testOrder

            mockMvc
                .get("/admin/orders/1") {
                    with(user(adminStaff))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.id") { value(1) }
                    jsonPath("$.customerName") { value("John Doe") }
                    jsonPath("$.items[0].quantity") { value(2) }
                }

            verify { orderService.getOrderById(1L) }
        }

        /**
         * Тест, когда попытка получить несуществующий заказ по ID заканчивается 404 (Not Found).
         * Ожидается, что поле "error" в ответе будет равно "NOT_FOUND".
         */
        @Test
        fun `getOrderById should 404 when not found`() {
            every { orderService.getOrderById(99L) } returns null

            mockMvc
                .get("/admin/orders/99") {
                    with(user(adminStaff))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.error") { value("NOT_FOUND") }
                }
        }

        /**
         * Тест обновления статуса заказа с роли COOK на IN_PROGRESS.
         * Ожидается статус 200 (OK) и что в ответе статус заказа равен "IN_PROGRESS".
         */
        @Test
        fun `updateOrderStatus should allow COOK to IN_PROGRESS`() {
            val req = AdminOrderStatusRequest(OrderStatus.IN_PROGRESS)
            val updated = testOrder.copy(status = OrderStatus.IN_PROGRESS)
            every { orderService.updateOrderStatusByRole(1L, StaffRole.COOK, req) } returns updated

            mockMvc
                .patch("/admin/orders/1/status") {
                    with(user(cookStaff))
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(req)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.status") { value("IN_PROGRESS") }
                }

            verify { orderService.updateOrderStatusByRole(1L, StaffRole.COOK, req) }
        }

        /**
         * Тест обработки некорректного запроса обновления статуса для DELIVERY (установка NEW недопустима).
         * Ожидается статус 400 (Bad Request) и поле "error" со значением "BAD_REQUEST".
         */
        @Test
        fun `updateOrderStatus should 400 when DELIVERY sets invalid status`() {
            val req = AdminOrderStatusRequest(OrderStatus.NEW)
            every { orderService.updateOrderStatusByRole(1L, StaffRole.DELIVERY, req) } throws InvalidOrderStatusException()

            mockMvc
                .patch("/admin/orders/1/status") {
                    with(user(deliveryStaff))
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(req)
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("BAD_REQUEST") }
                }
        }

        /**
         * Тест для изменения статуса заказа пользователем с ролью DELIVERY на DELIVERED.
         * Ожидается статус 200 (OK) и что в ответе статус равен "DELIVERED".
         */
        @Test
        fun `updateOrderStatus should allow DELIVERY to DELIVERED`() {
            val req = AdminOrderStatusRequest(OrderStatus.DELIVERED)
            val updated = testOrder.copy(status = OrderStatus.DELIVERED)
            every { orderService.updateOrderStatusByRole(1L, StaffRole.DELIVERY, req) } returns updated

            mockMvc
                .patch("/admin/orders/1/status") {
                    with(user(deliveryStaff))
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(req)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.status") { value("DELIVERED") }
                }

            verify { orderService.updateOrderStatusByRole(1L, StaffRole.DELIVERY, req) }
        }

        /**
         * Тест для отмены заказа пользователем с ролью ADMIN (статус CANCELLED).
         * Ожидается статус 200 (OK) и что статус в ответе будет "CANCELLED".
         */
        @Test
        fun `updateOrderStatus should allow ADMIN to CANCELLED`() {
            val req = AdminOrderStatusRequest(OrderStatus.CANCELLED)
            val updated = testOrder.copy(status = OrderStatus.CANCELLED)
            every { orderService.updateOrderStatusByRole(1L, StaffRole.ADMIN, req) } returns updated

            mockMvc
                .patch("/admin/orders/1/status") {
                    with(user(adminStaff))
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(req)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.status") { value("CANCELLED") }
                }

            verify { orderService.updateOrderStatusByRole(1L, StaffRole.ADMIN, req) }
        }

        /**
         * Тест, когда попытка обновить статус несуществующего заказа завершается 404 (Not Found).
         * Ожидается, что поле "error" в ответе будет равно "NOT_FOUND".
         */
        @Test
        fun `updateOrderStatus should 404 when order not found`() {
            val req = AdminOrderStatusRequest(OrderStatus.DELIVERED)
            every { orderService.updateOrderStatusByRole(99L, StaffRole.ADMIN, req) } throws OrderNotFoundException()

            mockMvc
                .patch("/admin/orders/99/status") {
                    with(user(adminStaff))
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(req)
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.error") { value("NOT_FOUND") }
                }
        }

        /**
         * Тест для получения всех доступных статусов заказа пользователем с ролью ADMIN.
         * Ожидается статус 200 (OK), а в ответе — массив строк с возможными значениями статусов (например, "NEW", "CANCELLED").
         */
        @Test
        fun `getStatusOptions should return all statuses for ADMIN`() {
            mockMvc
                .get("/admin/orders/status-options") {
                    with(user(adminStaff))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$") { isArray() }
                    jsonPath("$[?(@ == 'NEW')]") { exists() }
                    jsonPath("$[?(@ == 'CANCELLED')]") { exists() }
                }
        }

        /**
         * Тест для получения статусов доступных для COOK.
         * Ожидается статус 200 (OK) и что первым в массиве будет "IN_PROGRESS", вторым — "READY_FOR_DELIVERY".
         */
        @Test
        fun `getStatusOptions should return cook statuses for COOK`() {
            mockMvc
                .get("/admin/orders/status-options") {
                    with(user(cookStaff))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$[0]") { value("IN_PROGRESS") }
                    jsonPath("$[1]") { value("READY_FOR_DELIVERY") }
                }
        }

        /**
         * Тест для получения статусов доступных для DELIVERY.
         * Ожидается статус 200 (OK) и что первым будет "OUT_FOR_DELIVERY", вторым — "DELIVERED".
         */
        @Test
        fun `getStatusOptions should return delivery statuses for DELIVERY`() {
            mockMvc
                .get("/admin/orders/status-options") {
                    with(user(deliveryStaff))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$[0]") { value("OUT_FOR_DELIVERY") }
                    jsonPath("$[1]") { value("DELIVERED") }
                }
        }

        /**
         * Тест проверки доступа без авторизации.
         * Ожидается статус 401 (Unauthorized) при попытке получить список заказов без авторизации.
         */
        @Test
        fun `getOrders should return 401 for unauthorized access`() {
            mockMvc
                .get("/admin/orders")
                .andExpect { status { isUnauthorized() } }
        }

        /**
         * Тест, когда нет ни одного заказа: ожидается пустой список и статус 200 (OK).
         */
        @Test
        fun `should return empty list when no orders`() {
            every { orderService.getOrdersByRole(any(), any()) } returns emptyList()

            mockMvc
                .get("/admin/orders") {
                    with(user(adminStaff))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.length()") { value(0) }
                }
        }
    }
