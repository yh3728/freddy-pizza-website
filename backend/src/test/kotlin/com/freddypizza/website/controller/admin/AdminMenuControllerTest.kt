package com.freddypizza.website.controller.admin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.enums.ProductCategory
import com.freddypizza.website.request.AdminProductAvailabilityRequest
import com.freddypizza.website.request.AdminProductRequest
import com.freddypizza.website.request.AdminProductUpdateRequest
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
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

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

        @Test
        fun `should add product successfully`() {
            mockMvc
                .perform(
                    post("/admin/menu/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(productRequestDTO)),
                ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.name").value("Pizza Margherita"))
                .andExpect(jsonPath("$.price").value(10.99))

            verify { productService.addProduct(any()) }
        }

        @Test
        fun `should get all products successfully`() {
            mockMvc
                .perform(get("/admin/menu/items"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].name").value("Pizza Margherita"))

            verify { productService.getAllProducts() }
        }

        @Test
        fun `should get product by id successfully`() {
            mockMvc
                .perform(get("/admin/menu/items/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value("Pizza Margherita"))

            verify { productService.getProductById(1L) }
        }

        @Test
        fun `should return not found when product id does not exist`() {
            every { productService.getProductById(99L) } returns null

            mockMvc
                .perform(get("/admin/menu/items/99"))
                .andExpect(status().isNotFound)

            verify { productService.getProductById(99L) }
        }

        @Test
        fun `should delete product successfully`() {
            mockMvc
                .perform(delete("/admin/menu/items/1"))
                .andExpect(status().isNoContent)

            verify { productService.deleteProduct(1L) }
        }

        @Test
        fun `should update product successfully`() {
            mockMvc
                .perform(
                    put("/admin/menu/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(productUpdateRequest)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.price").value(12.99))
                .andExpect(jsonPath("$.isAvailable").value(false))

            verify { productService.updateProduct(1L, productUpdateRequest) }
        }

        @Test
        fun `should update product availability successfully`() {
            val availabilityRequest = AdminProductAvailabilityRequest(isAvailable = false)

            mockMvc
                .perform(
                    patch("/admin/menu/items/1/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(availabilityRequest)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.isAvailable").value(false))

            verify { productService.updateAvailability(1L, availabilityRequest) }
        }
    }
