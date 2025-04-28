package com.freddypizza.website.service.admin

import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.enums.ProductCategory
import com.freddypizza.website.repository.ProductRepository
import com.freddypizza.website.request.AdminProductAvailabilityRequest
import com.freddypizza.website.request.AdminProductUpdateRequest
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
class AdminProductServiceTest
    @Autowired
    constructor(
        private val underTest: AdminProductService,
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
                    isAvailable = true,
                    category = ProductCategory.PIZZA,
                )
            product2 =
                ProductEntity(
                    name = "Burger",
                    description = "Juicy beef burger",
                    price = BigDecimal(10.0),
                    isAvailable = true,
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

        /**
         * Тест проверяет, что метод addProduct добавляет новый продукт в базу данных.
         * Ожидается, что продукт будет сохранен и возвращен.
         */
        @Test
        fun `should add product successfully`() {
            val savedProduct = underTest.addProduct(product1)
            assertThat(savedProduct).isNotNull
            assertThat(savedProduct.id).isNotNull()
            assertThat(savedProduct.name).isEqualTo(product1.name)
            val recalledProduct = productRepository.findByIdOrNull(savedProduct.id)
            assertThat(recalledProduct).isNotNull
            assertThat(recalledProduct).isEqualTo(savedProduct)
        }

        /**
         * Тест проверяет, что метод updateProduct обновляет продукт в базе данных.
         * Ожидается, что все поля, переданные в request, будут обновлены.
         */
        @Test
        fun `should update product successfully`() {
            val savedProduct = productRepository.save(product1)

            val updateRequest =
                AdminProductUpdateRequest(
                    name = "Updated Pizza",
                    description = "Updated Description",
                    price = BigDecimal(18.0),
                    isAvailable = false,
                    category = ProductCategory.OTHER,
                )

            val updatedProduct = underTest.updateProduct(savedProduct.id, updateRequest)

            assertThat(updatedProduct.name).isEqualTo(updateRequest.name)
            assertThat(updatedProduct.description).isEqualTo(updateRequest.description)
            assertThat(updatedProduct.price).isEqualTo(updateRequest.price)
            assertThat(updatedProduct.isAvailable).isEqualTo(updateRequest.isAvailable)
            assertThat(updatedProduct.category).isEqualTo(updateRequest.category)
        }

        /**
         * Тест проверяет, что метод updateProduct выбрасывает исключение, если продукт с таким id не найден.
         * Ожидается, что будет выброшено исключение.
         */
        @Test
        fun `should throw exception when updating non-existent product`() {
            val updateRequest = AdminProductUpdateRequest(name = "New Name")

            assertThrows<IllegalStateException> {
                underTest.updateProduct(999L, updateRequest)
            }
        }

        /**
         * Тест проверяет, что метод deleteProduct удаляет продукт из базы данных.
         * Ожидается, что метод вызовет deleteById на репозитории.
         */
        @Test
        fun `should delete product successfully`() {
            val savedProduct = productRepository.save(product1)

            underTest.deleteProduct(savedProduct.id)

            val deletedProduct = productRepository.findByIdOrNull(savedProduct.id)
            assertThat(deletedProduct).isNull()
        }

        /**
         * Тест проверяет, что метод updateAvailability обновляет доступность продукта.
         * Ожидается, что доступность продукта будет обновлена в базе данных.
         */
        @Test
        fun `should update product availability successfully`() {
            val savedProduct = productRepository.save(product1)

            val availabilityRequest = AdminProductAvailabilityRequest(isAvailable = false)

            val updatedProduct = underTest.updateAvailability(savedProduct.id, availabilityRequest)

            assertThat(updatedProduct.isAvailable).isEqualTo(false)
        }

        /**
         * Тест проверяет, что метод updateAvailability выбрасывает исключение, если продукт не найден.
         */
        @Test
        fun `test that updateAvailability throws exception when product does not exist`() {
            val availabilityRequest = AdminProductAvailabilityRequest(isAvailable = true)

            assertThrows<IllegalStateException> {
                underTest.updateAvailability(999L, availabilityRequest)
            }
        }
    }
