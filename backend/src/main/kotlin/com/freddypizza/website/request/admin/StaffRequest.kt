package com.freddypizza.website.request.admin

import com.freddypizza.website.enums.StaffRole

data class StaffRequest(
    val username: String,
    val password: String,
    val role: StaffRole,
)
