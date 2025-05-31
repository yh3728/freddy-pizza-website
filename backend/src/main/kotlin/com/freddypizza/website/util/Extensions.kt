package com.freddypizza.website.util

import com.freddypizza.website.entity.OrderEntity
import com.freddypizza.website.entity.OrderItemEntity
import com.freddypizza.website.entity.ProductEntity
import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.request.admin.AdminProductRequest
import com.freddypizza.website.request.admin.StaffRequest
import com.freddypizza.website.response.admin.*
import com.freddypizza.website.response.user.CardItemResponse
import com.freddypizza.website.response.user.MenuItemResponse
import com.freddypizza.website.response.user.OrderResponse

fun ProductEntity.toMenuItemDTO() =
    MenuItemResponse(
        name = this.name,
        description = this.description,
        price = this.price,
        imagePath = this.imagePath,
    )

fun ProductEntity.toCardItemDTO() =
    CardItemResponse(
        name = this.name,
        ingredients = this.ingredients,
        weight = this.weight,
        price = this.price,
        imagePath = this.imagePath,
    )

fun OrderEntity.toOrderDTO() =
    OrderResponse(
        status = this.status,
        totalPrice = this.totalPrice,
        comment = this.comment,
        createdAt = this.createdAt,
        items = this.items.map { it.toOrderItemDto() },
        payment = this.payment,
    )

fun ProductEntity.toAdminProductResponseDTO() =
    AdminProductResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        weight = this.weight,
        ingredients = this.ingredients,
        price = this.price,
        quantity = this.quantity,
        category = this.category,
        imagePath = this.imagePath,
    )

fun AdminProductRequest.toProductEntity() =
    ProductEntity(
        name = this.name,
        description = this.description,
        weight = this.weight,
        ingredients = this.ingredients,
        price = this.price,
        quantity = this.quantity,
        category = this.category,
        imagePath = this.imagePath,
    )

fun OrderItemEntity.toOrderItemDto() =
    OrderItemResponse(
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
        payment = this.payment,
        trackingCode = this.trackingCode,
    )

fun OrderEntity.toAdminOrderShortResponse() =
    AdminOrderShortResponse(
        id = this.id,
        status = this.status,
        createdAt = this.createdAt,
        items = this.items.map { it.toOrderItemDto() },
        address = this.address,
        phone = this.phone,
        customerName = this.customerName,
    )

fun OrderEntity.toCookOrderShortResponse() =
    CookOrderShortResponse(
        id = this.id,
        status = this.status,
        createdAt = this.createdAt,
        items = this.items.map { it.toOrderItemDto() },
        comment = this.comment,
    )

fun OrderEntity.toDeliveryOrderResponse() =
    DeliveryOrderResponse(
        id = this.id,
        status = this.status,
        createdAt = this.createdAt,
        comment = this.comment,
        address = this.address,
        phone = this.phone,
        customerName = this.customerName,
        payment = this.payment,
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
