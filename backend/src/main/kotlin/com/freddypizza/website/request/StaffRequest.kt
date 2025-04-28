package com.freddypizza.website.request

import com.freddypizza.website.enums.StaffRole

data class StaffRequest(
    val username: String,
    val password: String,
    val role: StaffRole,
)
