package com.freddypizza.website.entity

import com.freddypizza.website.enums.StaffRole
import jakarta.persistence.*

@Entity
@Table(name = "staff")
data class StaffEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val username: String,
    @Column(nullable = false)
    val password: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: StaffRole,
)
