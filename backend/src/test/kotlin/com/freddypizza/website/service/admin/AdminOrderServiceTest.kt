package com.freddypizza.website.service.admin

import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.exception.InvalidOrderStatusException
import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.repository.OrderRepository
import com.freddypizza.website.request.admin.AdminOrderStatusRequest
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.time.LocalDateTime

@SpringBootTest
@Transactional
class AdminOrderServiceTest
    @Autowired
    constructor(
        private val underTest: AdminOrderService,
        private val orderRepository: OrderRepository,
    ) {
        private lateinit var order1: OrderEntity
        private lateinit var order2: OrderEntity
        private lateinit var order3: OrderEntity

        @BeforeEach
        fun setUp() {
            order1 = createOrder(OrderStatus.NEW)
            order2 = createOrder(OrderStatus.READY_FOR_DELIVERY)
            order3 = createOrder(OrderStatus.DELIVERED)
        }

        private fun createOrder(status: OrderStatus): OrderEntity =
            orderRepository.save(
                OrderEntity(
                    customerName = "Name",
                    phone = "23423423",
                    address = "Address 10",
                    totalPrice = BigDecimal.valueOf(25.0),
                    status = status,
                    createdAt = LocalDateTime.now(),
                ),
            )

        /**
         * Тест проверяет, что метод getAllOrders возвращает все существующие заказы, если они есть в базе данных.
         */
        @Test
        fun `getAllOrders should return all orders`() {
            val result = underTest.getAllOrders()
            assertThat(result).hasSize(3)
        }

        /**
         * Тест проверяет успешное получение заказа по существующему ID.
         */
        @Test
        fun `getOrderById should return order when exists`() {
            val result = underTest.getOrderById(order1.id)!!
            assertThat(result.id).isEqualTo(order1.id)
        }

        /**
         * Тест проверяет, что полученный заказ имеет корректный статус.
         */
        @Test
        fun `getOrderById should return order with correct status`() {
            val result = underTest.getOrderById(order1.id)!!
            assertThat(result.status).isEqualTo(OrderStatus.NEW)
        }

        /**
         * Тест проверяет, что при запросе несуществующего ID возвращается null.
         */
        @Test
        fun `getOrderById should return null when not exists`() {
            val result = underTest.getOrderById(999L)
            assertThat(result).isNull()
        }

        /**
         * Тест проверяет, что COOK может получить заказы со статусом NEW.
         */
        @Test
        fun `COOK can get orders with status NEW`() {
            val result = underTest.getOrdersByRole(StaffRole.COOK, OrderStatus.NEW)
            assertThat(result).hasSize(1).allMatch { it.status == OrderStatus.NEW }
        }

        /**
         * Тест проверяет, что COOK не может получить заказы с недопустимым статусом.
         */
        @Test
        fun `COOK should throw when requesting invalid status`() {
            assertThrows<InvalidOrderStatusException> {
                underTest.getOrdersByRole(StaffRole.COOK, OrderStatus.READY_FOR_DELIVERY)
            }
        }

        /**
         * Тест проверяет, что COOK без указания статуса получает заказы NEW и IN_PROGRESS.
         */
        @Test
        fun `COOK gets allowed orders when status is null`() {
            val result = underTest.getOrdersByRole(StaffRole.COOK, null)
            assertThat(result).hasSize(1)
            assertThat(result.map { it.status }).containsExactly(OrderStatus.NEW)
        }

        /**
         * Тест проверяет, что DELIVERY может получить заказы со статусом READY_FOR_DELIVERY.
         */
        @Test
        fun `DELIVERY can get orders with valid status`() {
            val result = underTest.getOrdersByRole(StaffRole.DELIVERY, OrderStatus.READY_FOR_DELIVERY)
            assertThat(result).hasSize(1)
        }

        /**
         * Тест проверяет, что DELIVERY получает заказы только со статусом READY_FOR_DELIVERY и OUT_FOR_DELIVERY при status=null.
         */
        @Test
        fun `DELIVERY gets allowed orders when status is null`() {
            val result = underTest.getOrdersByRole(StaffRole.DELIVERY, null)
            assertThat(result).hasSize(1)
            assertThat(result.first().status).isEqualTo(OrderStatus.READY_FOR_DELIVERY)
        }

        /**
         * Тест проверяет, что DELIVERY не может получить заказы с недопустимым статусом.
         */
        @Test
        fun `DELIVERY should throw when requesting invalid status`() {
            assertThrows<InvalidOrderStatusException> {
                underTest.getOrdersByRole(StaffRole.DELIVERY, OrderStatus.NEW)
            }
        }

        /**
         * Тест проверяет, что ADMIN получает все заказы независимо от статуса.
         */
        @Test
        fun `ADMIN gets all orders regardless of status`() {
            val result = underTest.getOrdersByRole(StaffRole.ADMIN, null)
            assertThat(result).hasSize(3)
        }

        /**
         * Тест проверяет, что COOK может обновить статус заказа на IN_PROGRESS.
         */
        @Test
        fun `COOK updates order to IN_PROGRESS`() {
            val request = AdminOrderStatusRequest(OrderStatus.IN_PROGRESS)
            val result = underTest.updateOrderStatusByRole(order1.id, StaffRole.COOK, request)

            assertThat(result.status).isEqualTo(OrderStatus.IN_PROGRESS)
        }

        /**
         * Тест проверяет, что статус заказа в базе обновился на IN_PROGRESS.
         */
        @Test
        fun `COOK update reflects in repository`() {
            val request = AdminOrderStatusRequest(OrderStatus.IN_PROGRESS)
            underTest.updateOrderStatusByRole(order1.id, StaffRole.COOK, request)

            assertThat(orderRepository.findByIdOrNull(order1.id)?.status).isEqualTo(OrderStatus.IN_PROGRESS)
        }

        /**
         * Тест проверяет, что COOK не может установить недопустимый статус.
         */
        @Test
        fun `COOK throws when setting invalid status`() {
            val request = AdminOrderStatusRequest(OrderStatus.DELIVERED)

            assertThrows<InvalidOrderStatusException> {
                underTest.updateOrderStatusByRole(order1.id, StaffRole.COOK, request)
            }
        }

        /**
         * Тест проверяет, что DELIVERY может обновить статус на OUT_FOR_DELIVERY.
         */
        @Test
        fun `DELIVERY updates order to OUT_FOR_DELIVERY`() {
            val request = AdminOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY)
            val result = underTest.updateOrderStatusByRole(order2.id, StaffRole.DELIVERY, request)

            assertThat(result.status).isEqualTo(OrderStatus.OUT_FOR_DELIVERY)
        }

        /**
         * Тест проверяет, что DELIVERY не может установить недопустимый статус.
         */
        @Test
        fun `DELIVERY throws when setting invalid status`() {
            val request = AdminOrderStatusRequest(OrderStatus.IN_PROGRESS)
            assertThrows<InvalidOrderStatusException> {
                underTest.updateOrderStatusByRole(order1.id, StaffRole.DELIVERY, request)
            }
        }

        /**
         * Тест проверяет, что при попытке обновить несуществующий заказ выбрасывается исключение.
         */
        @Test
        fun `ADMIN update throws if order not found`() {
            val request = AdminOrderStatusRequest(OrderStatus.DELIVERED)

            assertThrows<ProductNotFoundException> {
                underTest.updateOrderStatusByRole(999L, StaffRole.ADMIN, request)
            }
        }

        /**
         * Тест проверяет, что ADMIN может установить любой статус.
         */
        @Test
        fun `ADMIN can update order to any status`() {
            val request = AdminOrderStatusRequest(OrderStatus.CANCELLED)
            val result = underTest.updateOrderStatusByRole(order1.id, StaffRole.ADMIN, request)

            assertThat(result.status).isEqualTo(OrderStatus.CANCELLED)
        }
    }
