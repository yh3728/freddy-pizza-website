package com.freddypizza.website.repository

import com.freddypizza.website.entity.StaffEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StaffRepository : JpaRepository<StaffEntity, Long> {
    fun findByUsername(username: String): StaffEntity?
}
