package com.freddypizza.website.service.user

import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.enums.PaymentType
import com.freddypizza.website.enums.ProductCategory
import com.freddypizza.website.exception.EmptyOrderException
import com.freddypizza.website.exception.NotEnoughStockException
import com.freddypizza.website.exception.ProductNotFoundException
import com.freddypizza.website.repository.ProductRepository
import com.freddypizza.website.request.user.CreateOrderRequest
import com.freddypizza.website.request.user.OrderItemDTO
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

@SpringBootTest
@Transactional
class OrderServiceTest
    @Autowired
    constructor(
        private val underTest: OrderService,
        private val productRepository: ProductRepository,
    ) {
        private lateinit var product: ProductEntity
        private lateinit var order1: OrderEntity
        private lateinit var order2: OrderEntity

        @BeforeEach
        fun setUp() {
            product =
                productRepository.save(
                    ProductEntity(
                        name = "Test Pizza",
                        description = "Tasty",
                        price = BigDecimal.valueOf(10.0),
                        quantity = 10,
                        category = ProductCategory.SNACK,
                        imagePath = null,
                    ),
                )

            val req1 =
                CreateOrderRequest(
                    customerName = "Alice",
                    phone = "111",
                    address = "Addr 1",
                    comment = null,
                    items =
                        listOf(
                            OrderItemDTO(productId = product.id, quantity = 2),
                        ),
                    payment = PaymentType.CARD,
                )
            order1 = underTest.createOrder(req1)

            val req2 =
                CreateOrderRequest(
                    customerName = "Bob",
                    phone = "222",
                    address = "Addr 2",
                    comment = "No onions",
                    items =
                        listOf(
                            OrderItemDTO(productId = product.id, quantity = 3),
                        ),
                    payment = PaymentType.CARD,
                )
            order2 = underTest.createOrder(req2)
        }

        /**
         * Тест проверяет, что getOrderByCode возвращает заказ по его trackingCode.
         */
        @Test
        fun `getOrderByCode should return order when exists`() {
            val result = underTest.getOrderByCode(order2.trackingCode)!!
            assertThat(result.trackingCode).isEqualTo(order2.trackingCode)
        }

        /**
         * Тест проверяет, что getOrderByCode возвращает null для несуществующего кода.
         */
        @Test
        fun `getOrderByCode should return null when not exists`() {
            val result = underTest.getOrderByCode("UNKNOWN")
            assertThat(result).isNull()
        }

        /**
         * Тест проверяет, что при попытке создать пустой заказ выбрасывается EmptyOrderException.
         */
        @Test
        fun `createOrder should throw EmptyOrderException when items list is empty`() {
            val req =
                CreateOrderRequest(
                    customerName = "X",
                    phone = "000",
                    address = "Y",
                    comment = null,
                    items = mutableListOf(),
                    payment = PaymentType.CARD,
                )
            assertThrows<EmptyOrderException> {
                underTest.createOrder(req)
            }
        }

        /**
         * Тест проверяет, что при отсутствии продукта выбрасывается ProductNotFoundException.
         */
        @Test
        fun `createOrder should throw ProductNotFoundException when product not found`() {
            val req =
                CreateOrderRequest(
                    customerName = "X",
                    phone = "000",
                    address = "Y",
                    comment = null,
                    items = listOf(OrderItemDTO(productId = 999L, quantity = 1)),
                    payment = PaymentType.CARD,
                )
            assertThrows<ProductNotFoundException> {
                underTest.createOrder(req)
            }
        }

        /**
         * Тест проверяет успешное создание заказа:
         * - корректно вычисляется totalPrice,
         * - уменьшается количество продукта,
         * - сохраняется список items,
         * - генерируется trackingCode.
         */
        @Test
        fun `createOrder should save order with correct details`() {
            val updatedProduct = productRepository.findByIdOrNull(product.id)!!
            assertThat(updatedProduct.quantity).isEqualTo(5)

            assertThat(order1.customerName).isEqualTo("Alice")
            assertThat(order1.items).hasSize(1)
            assertThat(order1.totalPrice).isEqualTo(BigDecimal.valueOf(20.0))
            assertThat(order1.trackingCode).isNotNull()
            assertThat(order1.payment).isEqualTo(PaymentType.CARD)
        }

        /**
         * Тест проверяет, что при нехватке товара выбрасывается NotEnoughStockException.
         */
        @Test
        fun `createOrder should throw NotEnoughStockException when not enough quantity`() {
            val req =
                CreateOrderRequest(
                    customerName = "Charlie",
                    phone = "333",
                    address = "Addr 3",
                    comment = null,
                    items = listOf(OrderItemDTO(productId = product.id, quantity = 6)),
                    payment = PaymentType.CARD,
                )

            val exception =
                assertThrows<NotEnoughStockException> {
                    underTest.createOrder(req)
                }

            assertThat(exception.errors).hasSize(1)
            assertThat(exception.errors[0].productId).isEqualTo(product.id)
            assertThat(exception.errors[0].requestedQuantity).isEqualTo(6)
            assertThat(exception.errors[0].availableQuantity).isEqualTo(5)
        }
    }
