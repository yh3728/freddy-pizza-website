package com.freddypizza.website.response

import com.freddypizza.website.enums.StaffRole

data class StaffResponse(
    val id: Long,
    val username: String,
    val role: StaffRole,
)
