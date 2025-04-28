package com.freddypizza.website.util

import com.freddypizza.website.dto.OrderItemDto
import com.freddypizza.website.dto.admin.*
import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.entity.OrderItemEntity
import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.request.AdminProductRequest
import com.freddypizza.website.request.StaffRequest
import com.freddypizza.website.response.AdminProductResponse
import com.freddypizza.website.response.StaffResponse

fun ProductEntity.toAdminProductResponseDTO() =
    AdminProductResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        isAvailable = this.isAvailable,
        category = this.category,
    )

fun AdminProductRequest.toProductEntity() =
    ProductEntity(
        name = this.name,
        description = this.description,
        price = this.price,
        isAvailable = this.isAvailable,
        category = this.category,
    )

fun OrderEntity.toAdminOrderResponse() =
    AdminOrderShortResponse(
        id = this.id,
        status = this.status,
        totalPrice = this.totalPrice,
        items = this.items.map { it.toOrderItemDto() },
        createdAt = this.createdAt,
        comment = this.comment,
    )

fun OrderItemEntity.toOrderItemDto() =
    OrderItemDto(
        productName = this.product.name,
        quantity = this.quantity,
    )

fun OrderEntity.toAdminOrderFullResponse() =
    AdminOrderFullResponse(
        id = this.id,
        status = this.status,
        totalPrice = this.totalPrice,
        items = this.items.map { it.toOrderItemDto() },
        createdAt = this.createdAt,
        comment = this.comment,
        customerName = this.customerName,
        phone = this.phone,
        address = this.address,
    )

fun StaffRequest.toStaffEntity() =
    StaffEntity(
        username = this.username,
        password = this.password,
        role = this.role,
    )

fun StaffEntity.toStaffResponseDTO() =
    StaffResponse(
        id = this.id,
        username = this.username,
        role = this.role,
    )
