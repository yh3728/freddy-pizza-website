package com.freddypizza.website.enums

enum class OrderStatus(
    val displayName: String,
) {
    NEW("Новый"),
    IN_PROGRESS("В процессе"),
    OUT_FOR_DELIVERY("На доставке"),
    DELIVERED("Доставлено"),
    CANCELLED("Отменён"),
}
