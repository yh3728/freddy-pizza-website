package com.freddypizza.website.controller.admin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.enums.ProductCategory
import com.freddypizza.website.request.admin.AdminProductAvailabilityRequest
import com.freddypizza.website.request.admin.AdminProductRequest
import com.freddypizza.website.request.admin.AdminProductUpdateRequest
import com.freddypizza.website.service.admin.AdminProductService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.nio.charset.StandardCharsets

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = ["ADMIN"])
class AdminMenuControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        @MockkBean private val productService: AdminProductService,
    ) {
        private val productRequestDTO =
            AdminProductRequest(
                name = "Pizza Margherita",
                description = "Classic pizza with mozzarella and basil",
                price = BigDecimal(10.99),
                isAvailable = true,
                category = ProductCategory.PIZZA,
            )

        private val productUpdateRequest =
            AdminProductUpdateRequest(
                price = BigDecimal(12.99),
                isAvailable = false,
            )

        private val productEntity =
            ProductEntity(
                id = 1L,
                name = "Pizza Margherita",
                description = "Classic pizza with mozzarella and basil",
                price = BigDecimal(10.99),
                isAvailable = true,
                category = ProductCategory.PIZZA,
            )

        @BeforeEach
        fun setUp() {
            every { productService.addProduct(any()) } returns productEntity
            every { productService.getAllProducts() } returns listOf(productEntity)
            every { productService.getProductById(1L) } returns productEntity
            every { productService.updateProduct(1L, productUpdateRequest) } returns
                productEntity.copy(
                    price = productUpdateRequest.price!!,
                    isAvailable = productUpdateRequest.isAvailable!!,
                )
            every { productService.updateAvailability(1L, any()) } returns
                productEntity.copy(
                    isAvailable = productUpdateRequest.isAvailable!!,
                )
            every { productService.deleteProduct(1L) } returns Unit
        }

        /**
         * Тест для добавления нового продукта в меню.
         * Ожидается статус 201 (Created) и проверка корректных данных продукта в ответе.
         */
        @Test
        fun `should add product successfully`() {
            mockMvc
                .perform(
                    post("/admin/menu/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(productRequestDTO)),
                ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.name").value("Pizza Margherita"))
                .andExpect(jsonPath("$.price").value(10.99))

            verify { productService.addProduct(any()) }
        }

        /**
         * Тест для получения списка всех продуктов.
         * Ожидается статус 200 (OK) и что хотя бы один продукт будет в ответе.
         */
        @Test
        fun `should get all products successfully`() {
            mockMvc
                .perform(get("/admin/menu/"))
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
                .perform(get("/admin/menu/1"))
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
                .perform(get("/admin/menu/99"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Продукт не найден"))

            verify { productService.getProductById(99L) }
        }

        /**
         * Тест для удаления продукта.
         * Ожидается статус 204 (No Content) при успешном удалении.
         */
        @Test
        fun `should delete product successfully`() {
            mockMvc
                .perform(delete("/admin/menu/1"))
                .andExpect(status().isNoContent)

            verify { productService.deleteProduct(1L) }
        }

        /**
         * Тест для обновления данных продукта.
         * Ожидается статус 200 (OK) и что данные продукта обновляются.
         */
        @Test
        fun `should update product successfully`() {
            mockMvc
                .perform(
                    put("/admin/menu/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(productUpdateRequest)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.price").value(12.99))
                .andExpect(jsonPath("$.isAvailable").value(false))

            verify { productService.updateProduct(1L, productUpdateRequest) }
        }

        /**
         * Тест для обновления доступности продукта.
         * Ожидается статус 200 (OK) и что доступность продукта обновляется.
         */
        @Test
        fun `should update product availability successfully`() {
            val availabilityRequest = AdminProductAvailabilityRequest(isAvailable = false)

            mockMvc
                .perform(
                    patch("/admin/menu/1/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(availabilityRequest)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.isAvailable").value(false))

            verify { productService.updateAvailability(1L, availabilityRequest) }
        }

        /**
         * Тест для обновления изображения продукта.
         * Ожидается статус 200 (OK) и что путь к изображению продукта обновляется.
         */
        @Test
        fun `should upload product photo successfully`() {
            val updatedEntity = productEntity.copy(imagePath = "/uploads/products/test-image.jpg")
            every { productService.updateImagePath(1L, any()) } returns updatedEntity
            val content = "fake image content".toByteArray(StandardCharsets.UTF_8)
            val multipartFile =
                MockMultipartFile(
                    "photo",
                    "test.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    content,
                )
            mockMvc
                .perform(
                    multipart("/admin/menu/1/photo")
                        .file(multipartFile),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.imagePath").value("/uploads/products/test-image.jpg"))
            verify { productService.updateImagePath(1L, any()) }
        }
    }
