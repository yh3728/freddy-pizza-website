package com.freddypizza.website.service.admin

import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.repository.StaffRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class StaffService(
    private val staffRepository: StaffRepository,
    private val encoder: BCryptPasswordEncoder,
) {
    fun addStaff(staff: StaffEntity): StaffEntity {
        val found = staffRepository.findByUsername(staff.username)
        require(found == null)
        val updated = staff.copy(password = encoder.encode(staff.password))
        return staffRepository.save(updated)
    }

    fun getAllStaff(): List<StaffEntity> = staffRepository.findAll()

    fun getStaffById(id: Long): StaffEntity? = staffRepository.findByIdOrNull(id)

    fun getStaffByUsername(username: String): StaffEntity? = staffRepository.findByUsername(username)

    fun deleteStaff(id: Long) = staffRepository.deleteById(id)
}
