package com.freddypizza.website.controller.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.enums.PaymentType
import com.freddypizza.website.request.user.CreateOrderRequest
import com.freddypizza.website.service.user.OrderService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        @MockkBean private val orderService: OrderService,
    ) {
        private val orderEntity =
            OrderEntity(
                id = 1L,
                customerName = "Alice",
                phone = "222",
                address = "Addr 2",
                comment = "No onions",
                payment = PaymentType.CARD,
                trackingCode = "ABCDEF",
            )

        private val orderRequest =
            CreateOrderRequest(
                customerName = "Alice",
                phone = "222",
                address = "Addr 2",
                comment = "No onions",
                payment = PaymentType.CARD,
                items = mutableListOf(),
            )

        @BeforeEach
        fun setUp() {
            every { orderService.createOrder(any()) } returns orderEntity
            every { orderService.getOrderByCode("ABCDEF") } returns orderEntity
        }

        /**
         * Тест для создания нового заказа.
         * Ожидается статус 201 (Created) и проверка корректных данных заказа в ответе.
         */
        @Test
        fun `should create order successfully`() {
            mockMvc
                .perform(
                    post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(orderRequest)),
                ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.comment").value("No onions"))

            verify { orderService.createOrder(any()) }
        }

        /**
         * Тест для получения заказа по трек коду.
         * Ожидается статус 200 (OK) и правильные данные о заказе.
         */
        @Test
        fun `should get order by code successfully`() {
            mockMvc
                .perform(get("/orders/ABCDEF"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.comment").value("No onions"))

            verify { orderService.getOrderByCode("ABCDEF") }
        }

        /**
         * Тест для обработки случая, когда заказ не найден по трек коду.
         * Ожидается статус 404 (Not Found) и соответствующее сообщение об ошибке.
         */
        @Test
        fun `should return not found when order does not exist`() {
            every { orderService.getOrderByCode("ABCDEH") } returns null

            mockMvc
                .perform(get("/orders/ABCDEH"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Заказ не найден"))

            verify { orderService.getOrderByCode("ABCDEH") }
        }
    }
