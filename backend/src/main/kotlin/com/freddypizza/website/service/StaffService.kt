package com.freddypizza.website.service

import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.repository.StaffRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StaffService(
    private val staffRepository: StaffRepository,
) {
    fun addStaff(staffEntity: StaffEntity): StaffEntity {
        val found = staffRepository.findByUsername(staffEntity.username)
        require(found == null)
        return staffRepository.save(staffEntity)
    }

    fun getAllStaff(): List<StaffEntity> = staffRepository.findAll()

    fun getStaffById(id: Long): StaffEntity? = staffRepository.findByIdOrNull(id)

    fun getStaffByUsername(username: String): StaffEntity? = staffRepository.findByUsername(username)
}
