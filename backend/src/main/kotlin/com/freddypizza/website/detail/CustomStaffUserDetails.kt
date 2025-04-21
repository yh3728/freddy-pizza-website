package com.freddypizza.website.detail

import com.freddypizza.website.entity.StaffEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomStaffUserDetails(
    private val staff: StaffEntity, // Привязка к сущности StaffEntity
) : UserDetails {
    val id: Long get() = staff.id

    override fun getUsername(): String = staff.username

    override fun getPassword(): String = staff.password

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${staff.role.name}")) // Преобразуем роль в формат "ROLE_*"
    }
}
