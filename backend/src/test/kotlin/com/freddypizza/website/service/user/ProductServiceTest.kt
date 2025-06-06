package com.freddypizza.website.service.user

import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.enums.ProductCategory
import com.freddypizza.website.repository.ProductRepository
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
@Transactional
class ProductServiceTest
    @Autowired
    constructor(
        private val underTest: ProductService,
        private val productRepository: ProductRepository,
    ) {
        private lateinit var product1: ProductEntity
        private lateinit var product2: ProductEntity

        @BeforeEach
        fun setUp() {
            product1 =
                ProductEntity(
                    name = "Cheesy pizza",
                    description = "Delicious cheesy pizza",
                    price = BigDecimal(15.0),
                    quantity = 3,
                    category = ProductCategory.PIZZA,
                )
            product2 =
                ProductEntity(
                    name = "Burger",
                    description = "Juicy beef burger",
                    price = BigDecimal(10.0),
                    quantity = 5,
                    category = ProductCategory.SNACK,
                )
        }

        /**
         * Тест проверяет, что метод getProductById возвращает продукт по id, если он есть в базе данных.
         * Ожидается, что вернется правильный продукт.
         */
        @Test
        fun `should return product by id when product exists`() {
            val savedProduct = productRepository.save(product1)
            val foundProduct = underTest.getProductById(savedProduct.id)
            assertThat(foundProduct).isEqualTo(savedProduct)
        }

        /**
         * Тест проверяет, что метод getProductById возвращает null, если продукт с таким id не найден.
         * Ожидается, что вернется null.
         */
        @Test
        fun `should return null when product does not exist by id`() {
            val foundProduct = underTest.getProductById(999L)

            assertThat(foundProduct).isNull()
        }

        /**
         * Тест проверяет, что метод getAllProducts возвращает пустой список, если продуктов нет.
         */
        @Test
        fun `test that getAllProducts returns empty list when no products in database`() {
            assertThat(underTest.getAllProducts()).isEmpty()
        }

        /**
         * Тест проверяет, что метод getAllProducts возвращает список продуктов, если они есть в базе данных.
         */
        @Test
        fun `test that getAllProducts returns list of products when products exist`() {
            val savedProducts = listOf(productRepository.save(product1), productRepository.save(product2))

            assertThat(underTest.getAllProducts()).isEqualTo(savedProducts)
        }
    }
