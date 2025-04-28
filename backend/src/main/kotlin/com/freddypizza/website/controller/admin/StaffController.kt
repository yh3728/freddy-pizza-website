package com.freddypizza.website.controller.admin

import com.freddypizza.website.exception.StaffNotFoundException
import com.freddypizza.website.request.StaffRequest
import com.freddypizza.website.response.StaffResponse
import com.freddypizza.website.service.admin.StaffService
import com.freddypizza.website.util.toStaffEntity
import com.freddypizza.website.util.toStaffResponseDTO
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
    fun addStaff(
        @RequestBody staffRequest: StaffRequest,
    ): ResponseEntity<StaffResponse> {
        val staffEntity = staffRequest.toStaffEntity()
        val staffResponse = staffService.addStaff(staffEntity).toStaffResponseDTO()
        return ResponseEntity(staffResponse, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllStaff(): ResponseEntity<List<StaffResponse>> = ResponseEntity.ok(staffService.getAllStaff().map { it.toStaffResponseDTO() })

    @GetMapping("/{id}")
    fun getStaffById(
        @PathVariable id: Long,
    ): ResponseEntity<StaffResponse> {
        val staff = staffService.getStaffById(id) ?: throw StaffNotFoundException()
        return ResponseEntity.ok(staff.toStaffResponseDTO())
    }

    @DeleteMapping("/{id}")
    fun deleteStaff(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        staffService.deleteStaff(id)
        return ResponseEntity.noContent().build()
    }
}
