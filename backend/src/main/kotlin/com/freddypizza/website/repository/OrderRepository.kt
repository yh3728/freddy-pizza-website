package com.freddypizza.website.repository

import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.enums.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<OrderEntity, Long> {
    fun findByStatus(status: OrderStatus): List<OrderEntity>

    fun findByStatusIn(statusList: List<OrderStatus>): List<OrderEntity>
}
