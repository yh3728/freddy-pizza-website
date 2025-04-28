package com.freddypizza.website.controller.admin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.request.StaffRequest
import com.freddypizza.website.service.admin.StaffService
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

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = ["ADMIN"])
class StaffControllerTest
    @Autowired
    constructor(
        @MockkBean val staffService: StaffService,
        private val mockMvc: MockMvc,
    ) {
        private val staffRequest = StaffRequest(username = "admin", password = "password123", role = StaffRole.ADMIN)
        private val staffEntity = StaffEntity(id = 1, username = "admin", password = "password123", role = StaffRole.ADMIN)

        @BeforeEach
        fun setUp() {
            every { staffService.addStaff(any()) } returns staffEntity
            every { staffService.getAllStaff() } returns listOf(staffEntity)
            every { staffService.getStaffById(1L) } returns staffEntity
            every { staffService.getStaffById(999L) } returns null
            every { staffService.deleteStaff(1L) } returns Unit
        }

        /**
         * Тест для добавления нового сотрудника.
         * Ожидается статус 201 (Created).
         */
        @Test
        fun `should add staff successfully`() {
            mockMvc
                .perform(
                    post("/admin/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(staffRequest)),
                ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
	
            verify { staffService.addStaff(any()) }
        }

        /**
         * Тест для получения списка всех сотрудников.
         * Ожидается статус 200 (OK).
         */
        @Test
        fun `should get all staff successfully`() {
            mockMvc
                .perform(get("/admin/staff"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
	
            verify { staffService.getAllStaff() }
        }

        /**
         * Тест для получения сотрудника по ID.
         * Ожидается статус 200 (OK), если сотрудник найден.
         */
        @Test
        fun `should get staff by id successfully`() {
            mockMvc
                .perform(get("/admin/staff/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
	
            verify { staffService.getStaffById(1L) }
        }

        /**
         * Тест для получения сотрудника по несуществующему ID.
         * Ожидается статус 404 (Not Found).
         */
        @Test
        fun `should return not found when staff by id does not exist`() {
            mockMvc
                .perform(get("/admin/staff/999"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Сотрудник не найден"))
	
            verify { staffService.getStaffById(999L) }
        }

        /**
         * Тест для удаления сотрудника по ID.
         * Ожидается статус 204 (No Content).
         */
        @Test
        fun `should delete staff successfully`() {
            mockMvc
                .perform(delete("/admin/staff/1"))
                .andExpect(status().isNoContent)
	
            verify { staffService.deleteStaff(1L) }
        }
    }
