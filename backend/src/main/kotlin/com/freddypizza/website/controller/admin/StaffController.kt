package com.freddypizza.website.controller.admin

import com.freddypizza.website.exception.ErrorResponse
import com.freddypizza.website.exception.StaffNotFoundException
import com.freddypizza.website.request.admin.StaffRequest
import com.freddypizza.website.response.admin.StaffResponse
import com.freddypizza.website.service.admin.StaffService
import com.freddypizza.website.util.toStaffEntity
import com.freddypizza.website.util.toStaffResponseDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/staff")
class StaffController(
    private val staffService: StaffService,
) {
    @PostMapping
    @Operation(summary = "Добавить нового сотрудника")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Сотрудник успешно добавлен"),
            ApiResponse(
                responseCode = "409",
                description = "Имя пользователя уже занято",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun addStaff(
        @RequestBody staffRequest: StaffRequest,
    ): ResponseEntity<StaffResponse> {
        val staffEntity = staffRequest.toStaffEntity()
        val staffResponse = staffService.addStaff(staffEntity).toStaffResponseDTO()
        return ResponseEntity(staffResponse, HttpStatus.CREATED)
    }

    @GetMapping
    @Operation(summary = "Получить список всех сотрудников")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список сотрудников получен"),
        ],
    )
    fun getAllStaff(): ResponseEntity<List<StaffResponse>> = ResponseEntity.ok(staffService.getAllStaff().map { it.toStaffResponseDTO() })

    @GetMapping("/{id}")
    @Operation(summary = "Получить сотрудника по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Сотрудник найден"),
            ApiResponse(
                responseCode = "404",
                description = "Сотрудник не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun getStaffById(
        @PathVariable id: Long,
    ): ResponseEntity<StaffResponse> {
        val staff = staffService.getStaffById(id) ?: throw StaffNotFoundException()
        return ResponseEntity.ok(staff.toStaffResponseDTO())
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить сотрудника по ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Сотрудник удалён"),
            ApiResponse(
                responseCode = "404",
                description = "Сотрудник не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun deleteStaff(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        staffService.deleteStaff(id)
        return ResponseEntity.noContent().build()
    }
}
