package com.freddypizza.website.util

import com.freddypizza.website.service.admin.StaffService
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val staffService: StaffService,
) {
    // ! НЕ ЗАПУСКАТЬ ВМЕСТЕ С ТЕСТАМИ, ОБЯЗАТЕЛЬНО ЗАКОММЕНТИРОВАТЬ
//    @PostConstruct
//    fun init() {
//        val adminUsername = "admin"
//        val adminPassword = "admin"
//        val adminRole = StaffRole.ADMIN
//
//        val existingAdmin = staffService.getStaffByUsername(adminUsername)
//        if (existingAdmin == null) {
//            staffService.addStaff(
//                StaffEntity(
//                    username = adminUsername,
//                    password = adminPassword,
//                    role = adminRole,
//                ),
//            )
//            println("Admin account created successfully.")
//        } else {
//            println("Admin account already exists.")
//        }
//    }
}
