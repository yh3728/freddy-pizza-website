package com.freddypizza.website.detail

import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.enums.StaffRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomStaffUserDetails(
    private val staff: StaffEntity,
) : UserDetails {
    val id: Long get() = staff.id

    override fun getUsername(): String = staff.username

    override fun getPassword(): String = staff.password

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_${staff.role.name}"))

    fun getRole(): StaffRole = staff.role
}
