package com.freddypizza.website.service.admin

import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.exception.BadPasswordException
import com.freddypizza.website.exception.BadUsernameException
import com.freddypizza.website.exception.StaffNotFoundException
import com.freddypizza.website.exception.UsernameAlreadyExistsException
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
        if (found != null) {
            throw UsernameAlreadyExistsException()
        }
        if (!checkLogin(staff.username)){
            throw BadUsernameException()
        }
        if (!checkPassword(staff.password)){
            throw BadPasswordException()
        }
        val updated = staff.copy(password = encoder.encode(staff.password))
        return staffRepository.save(updated)
    }

    fun getAllStaff(): List<StaffEntity> = staffRepository.findAll()

    fun getStaffById(id: Long): StaffEntity? = staffRepository.findByIdOrNull(id)

    fun getStaffByUsername(username: String): StaffEntity? = staffRepository.findByUsername(username)

    fun deleteStaff(id: Long) {
        staffRepository.findById(id).orElseThrow { StaffNotFoundException() }
        staffRepository.deleteById(id)
    }
    private fun checkLogin(login: String): Boolean{
        val loginPattern = Regex("^[a-zA-Z0-9]{3,10}$")
        return loginPattern.matches(login)
    }

    private fun checkPassword(password: String): Boolean{
        val passwordPattern = Regex("^[a-zA-Z0-9]{3,30}$")
        return passwordPattern.matches(password)
    }
}


