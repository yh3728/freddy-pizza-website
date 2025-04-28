package com.freddypizza.website.service.admin

import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.repository.StaffRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class StaffUserDetailsService(
    private val staffRepository: StaffRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): CustomStaffUserDetails {
        val staff =
            staffRepository.findByUsername(username)
                ?: throw UsernameNotFoundException(username)
        return CustomStaffUserDetails(staff)
    }
}
