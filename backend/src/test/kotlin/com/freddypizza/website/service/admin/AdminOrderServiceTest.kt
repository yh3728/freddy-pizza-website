package com.freddypizza.website.service.admin

import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.PaymentType
import com.freddypizza.website.enums.ProductCategory
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.exception.InvalidOrderStatusException
import com.freddypizza.website.exception.OrderNotFoundException
import com.freddypizza.website.repository.OrderRepository
import com.freddypizza.website.repository.ProductRepository
import com.freddypizza.website.repository.StaffRepository
import com.freddypizza.website.request.admin.AdminOrderStatusRequest
import com.freddypizza.website.request.user.CreateOrderRequest
import com.freddypizza.website.request.user.OrderItemDTO
import com.freddypizza.website.service.user.OrderService
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
        private val staffRepository: StaffRepository,
        private val productRepository: ProductRepository,
        private val orderService: OrderService,
    ) {
        private lateinit var order1: OrderEntity
        private lateinit var order2: OrderEntity
        private lateinit var order3: OrderEntity

        private lateinit var savedAdmin: StaffEntity
        private lateinit var savedCook: StaffEntity
        private lateinit var savedDelivery: StaffEntity

        private lateinit var adminStaff: CustomStaffUserDetails
        private lateinit var cookStaff: CustomStaffUserDetails
        private lateinit var deliveryStaff: CustomStaffUserDetails

        @BeforeEach
        fun setUp() {
            savedAdmin = staffRepository.save(StaffEntity(0, "admin", "password", StaffRole.ADMIN))
            savedCook = staffRepository.save(StaffEntity(0, "cook", "password", StaffRole.COOK))
            savedDelivery = staffRepository.save(StaffEntity(0, "delivery", "password", StaffRole.DELIVERY))

            adminStaff = CustomStaffUserDetails(savedAdmin)
            cookStaff = CustomStaffUserDetails(savedCook)
            deliveryStaff = CustomStaffUserDetails(savedDelivery)

            order1 = createOrder(OrderStatus.NEW)
            order2 = createOrder(OrderStatus.READY_FOR_DELIVERY)
            order3 = createOrder(OrderStatus.OUT_FOR_DELIVERY, assignedDelivery = savedDelivery)
        }

        private fun randomCode() =
            (1..6)
                .map { "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".random() }
                .joinToString("")

        private fun createOrder(
            status: OrderStatus,
            assignedDelivery: StaffEntity? = null,
        ): OrderEntity =
            orderRepository.save(
                OrderEntity(
                    customerName = "Name",
                    phone = "23423423",
                    address = "Address 10",
                    totalPrice = BigDecimal.valueOf(25.0),
                    status = status,
                    createdAt = LocalDateTime.now(),
                    trackingCode = randomCode(),
                    payment = PaymentType.CASH,
                    assignedDelivery = assignedDelivery,
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
            val result = underTest.getOrdersByRole(OrderStatus.NEW, cookStaff)
            assertThat(result).hasSize(1).allMatch { it.status == OrderStatus.NEW }
        }

        /**
         * Тест проверяет, что COOK не может получить заказы с недопустимым статусом.
         */
        @Test
        fun `COOK should throw when requesting invalid status`() {
            assertThrows<InvalidOrderStatusException> {
                underTest.getOrdersByRole(OrderStatus.READY_FOR_DELIVERY, cookStaff)
            }
        }

        /**
         * Тест проверяет, что COOK без указания статуса получает заказы NEW и IN_PROGRESS.
         */
        @Test
        fun `COOK gets allowed orders when status is null`() {
            val result = underTest.getOrdersByRole(null, cookStaff)
            assertThat(result).hasSize(1)
            assertThat(result.map { it.status }).containsExactly(OrderStatus.NEW)
        }

        /**
         * Тест проверяет, что DELIVERY может получить заказы со статусом READY_FOR_DELIVERY и только свои OUT_FOR_DELIVERY.
         */
        @Test
        fun `DELIVERY sees only READY_FOR_DELIVERY and own OUT_FOR_DELIVERY when status is null`() {
            val result = underTest.getOrdersByRole(null, deliveryStaff)

            assertThat(result).hasSize(2)
            assertThat(result.map { it.status }).containsExactlyInAnyOrder(
                OrderStatus.READY_FOR_DELIVERY,
                OrderStatus.OUT_FOR_DELIVERY,
            )
        }

        /**
         * Тест проверяет, что DELIVERY не может получить заказы с недопустимым статусом.
         */
        @Test
        fun `DELIVERY should throw when requesting invalid status`() {
            assertThrows<InvalidOrderStatusException> {
                underTest.getOrdersByRole(OrderStatus.NEW, deliveryStaff)
            }
        }

        /**
         * Тест проверяет, что ADMIN получает все заказы независимо от статуса.
         */
        @Test
        fun `ADMIN gets all orders regardless of status`() {
            val result = underTest.getOrdersByRole(null, adminStaff)
            assertThat(result).hasSize(3)
        }

        /**
         * Тест проверяет, что COOK может обновить статус заказа на IN_PROGRESS.
         */
        @Test
        fun `COOK updates order to IN_PROGRESS`() {
            val request = AdminOrderStatusRequest(OrderStatus.IN_PROGRESS)
            val result =
                underTest.updateOrderStatusByRole(order1.id, request, cookStaff)

            assertThat(result.status).isEqualTo(OrderStatus.IN_PROGRESS)
        }

        /**
         * Тест проверяет, что COOK может обновить статус заказа на READY_FOR_DELIVERY.
         */
        @Test
        fun `COOK updates order to READY_FOR_DELIVERY`() {
            val request = AdminOrderStatusRequest(OrderStatus.READY_FOR_DELIVERY)
            val result =
                underTest.updateOrderStatusByRole(order1.id, request, cookStaff)

            assertThat(result.status).isEqualTo(OrderStatus.READY_FOR_DELIVERY)
        }

        /**
         * Тест проверяет, что статус заказа в базе обновился на IN_PROGRESS.
         */
        @Test
        fun `COOK update reflects in repository`() {
            val request = AdminOrderStatusRequest(OrderStatus.IN_PROGRESS)
            underTest.updateOrderStatusByRole(order1.id, request, cookStaff)

            assertThat(orderRepository.findByIdOrNull(order1.id)?.status).isEqualTo(OrderStatus.IN_PROGRESS)
        }

        /**
         * Тест проверяет, что COOK не может установить недопустимый статус.
         */
        @Test
        fun `COOK throws when setting invalid status`() {
            val request = AdminOrderStatusRequest(OrderStatus.DELIVERED)

            assertThrows<InvalidOrderStatusException> {
                underTest.updateOrderStatusByRole(order1.id, request, cookStaff)
            }
        }

        /**
         * Тест проверяет, что DELIVERY может обновить статус на OUT_FOR_DELIVERY.
         */
        @Test
        fun `DELIVERY updates order to OUT_FOR_DELIVERY`() {
            val request = AdminOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY)
            val result =
                underTest.updateOrderStatusByRole(order2.id, request, deliveryStaff)

            assertThat(result.status).isEqualTo(OrderStatus.OUT_FOR_DELIVERY)
        }

        /**
         * Тест проверяет, что DELIVERY может обновить статус на DELIVERED.
         */
        @Test
        fun `DELIVERY updates order to DELIVERED`() {
            val request = AdminOrderStatusRequest(OrderStatus.DELIVERED)
            val result =
                underTest.updateOrderStatusByRole(order2.id, request, deliveryStaff)

            assertThat(result.status).isEqualTo(OrderStatus.DELIVERED)
        }

        /**
         * Тест проверяет, что DELIVERY не может установить недопустимый статус.
         */
        @Test
        fun `DELIVERY throws when setting invalid status`() {
            val request = AdminOrderStatusRequest(OrderStatus.IN_PROGRESS)
            assertThrows<InvalidOrderStatusException> {
                underTest.updateOrderStatusByRole(order1.id, request, deliveryStaff)
            }
        }

        /**
         * Тест проверяет, что при попытке обновить несуществующий заказ выбрасывается исключение.
         */
        @Test
        fun `ADMIN update throws if order not found`() {
            val request = AdminOrderStatusRequest(OrderStatus.DELIVERED)

            assertThrows<OrderNotFoundException> {
                underTest.updateOrderStatusByRole(999L, request, adminStaff)
            }
        }

        /**
         * Тест проверяет, что ADMIN может установить любой статус.
         */
        @Test
        fun `ADMIN can update order to any status`() {
            val request = AdminOrderStatusRequest(OrderStatus.CANCELLED)
            val result =
                underTest.updateOrderStatusByRole(order1.id, request, adminStaff)

            assertThat(result.status).isEqualTo(OrderStatus.CANCELLED)
        }

        /**
         * Тест проверяет, что DELIVERY видит только свои OUT_FOR_DELIVERY заказы когда статус OUT_FOR_DELIVERY.
         */
        @Test
        fun `DELIVERY sees only own OUT_FOR_DELIVERY orders when status is OUT_FOR_DELIVERY`() {
            val result = underTest.getOrdersByRole(OrderStatus.OUT_FOR_DELIVERY, deliveryStaff)

            assertThat(result).hasSize(1)
            assertThat(result.first().status).isEqualTo(OrderStatus.OUT_FOR_DELIVERY)
            assertThat(result.first().assignedDelivery?.id).isEqualTo(deliveryStaff.id)
        }

        /**
         * Тест проверяет, что DELIVERY не видит чужие заказы со статусом OUT_FOR_DELIVERY.
         */
        @Test
        fun `DELIVERY does not see other delivery's OUT_FOR_DELIVERY orders`() {
            val otherDelivery = staffRepository.save(StaffEntity(0, "other", "pwd", StaffRole.DELIVERY))
            val foreignOrder = createOrder(OrderStatus.OUT_FOR_DELIVERY, otherDelivery)
            val result = underTest.getOrdersByRole(null, deliveryStaff)

            assertThat(result).noneMatch { it.id == foreignOrder.id }
        }

        /**
         * Тест проверяет, что DELIVERY становится ответственным за заказ при выставлении статуса OUT_FOR_DELIVERY.
         */
        @Test
        fun `DELIVERY sets assignedDelivery when updating to OUT_FOR_DELIVERY`() {
            val request = AdminOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY)

            val result = underTest.updateOrderStatusByRole(order2.id, request, deliveryStaff)

            assertThat(result.status).isEqualTo(OrderStatus.OUT_FOR_DELIVERY)
            assertThat(result.assignedDelivery?.id).isEqualTo(deliveryStaff.id)
        }

        /**
         * Тест проверяет, что ADMIN видит все заказы, независимо от assignedDelivery.
         */
        @Test
        fun `ADMIN sees all orders regardless of assignedDelivery`() {
            val otherDelivery = staffRepository.save(StaffEntity(0, "other", "pwd", StaffRole.DELIVERY))
            val foreignOrder = createOrder(OrderStatus.OUT_FOR_DELIVERY, assignedDelivery = otherDelivery)
            val result = underTest.getOrdersByRole(null, adminStaff)

            assertThat(result.map { it.id }).contains(foreignOrder.id)
        }

        /**
         * Тест проверяет, что при установке заказу статуса CANCELLED, товар возвращается в наличие.
         */
        @Test
        fun `ADMIN cancels order status and product quantity is restored`() {
            val initialQuantity = 10
            val product =
                productRepository.save(
                    ProductEntity(
                        name = "Pizza",
                        quantity = 10,
                        category = ProductCategory.PIZZA,
                        price = BigDecimal.TEN,
                        description = "example description",
                        ingredients = "example ingredients",
                        weight = 100,
                    ),
                )

            val orderRequest =
                CreateOrderRequest(
                    customerName = "Customer",
                    phone = "123456789",
                    address = "Address",
                    payment = PaymentType.CASH,
                    items = mutableListOf(OrderItemDTO(product.id, 3)),
                )

            val order = orderService.createOrder(orderRequest)

            val productAfterCreation = productRepository.findByIdOrNull(product.id)
            assertThat(productAfterCreation?.quantity).isEqualTo(initialQuantity - 3)

            val request = AdminOrderStatusRequest(OrderStatus.CANCELLED)
            val updatedOrder = underTest.updateOrderStatusByRole(order.id, request, adminStaff)

            assertThat(updatedOrder.status).isEqualTo(OrderStatus.CANCELLED)

            val productAfterCancel = productRepository.findByIdOrNull(product.id)
            assertThat(productAfterCancel?.quantity).isEqualTo(initialQuantity)
        }
    }
