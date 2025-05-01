package com.freddypizza.website.response.admin

import com.freddypizza.website.enums.StaffRole

data class StaffResponse(
    val id: Long,
    val username: String,
    val role: StaffRole,
)
