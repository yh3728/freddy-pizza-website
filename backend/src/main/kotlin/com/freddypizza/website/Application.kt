package com.freddypizza.website

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

// @Component
// class AdminDataInitializer(
//    private val staffRepository: StaffRepository,
//    private val staffService: StaffService,
// ) {
//    @PostConstruct
//    fun init() {
//        val adminUsername = "admin"
//        val adminPassword = "admin"
//        val adminRole = StaffRole.ADMIN
//
//        staffService.addStaff(StaffEntity(username = adminUsername, password = adminPassword, role = adminRole))
//        println("Admin account created successfully.")
//    }
// }
