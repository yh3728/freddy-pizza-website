package com.freddypizza.website.controller.user

import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.enums.ProductCategory
import com.freddypizza.website.service.user.ProductService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        @MockkBean private val productService: ProductService,
    ) {
        private val productEntity =
            ProductEntity(
                id = 1L,
                name = "Pizza Margherita",
                description = "Classic pizza with mozzarella and basil",
                price = BigDecimal(10.99),
                quantity = 1,
                category = ProductCategory.PIZZA,
                ingredients = "example ingredients",
                weight = 100,
            )

        @BeforeEach
        fun setUp() {
            every { productService.getAllProducts() } returns listOf(productEntity)
            every { productService.getProductById(1L) } returns productEntity
        }

        /**
         * Тест для получения списка всех продуктов.
         * Ожидается статус 200 (OK) и что хотя бы один продукт будет в ответе.
         */
        @Test
        fun `should get all products successfully`() {
            mockMvc
                .perform(get("/menu"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].name").value("Pizza Margherita"))

            verify { productService.getAllProducts() }
        }

        /**
         * Тест для получения продукта по ID.
         * Ожидается статус 200 (OK) и правильные данные о продукте.
         */
        @Test
        fun `should get product by id successfully`() {
            mockMvc
                .perform(get("/menu/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value("Pizza Margherita"))

            verify { productService.getProductById(1L) }
        }

        /**
         * Тест для обработки случая, когда продукт не найден по ID.
         * Ожидается статус 404 (Not Found) и соответствующее сообщение об ошибке.
         */
        @Test
        fun `should return not found when product id does not exist`() {
            every { productService.getProductById(99L) } returns null

            mockMvc
                .perform(get("/menu/99"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Продукт не найден"))

            verify { productService.getProductById(99L) }
        }
    }
