package com.freddypizza.website.enums

enum class OrderStatus(
    val displayName: String,
) {
    NEW("Новый"),
    IN_PROGRESS("В процессе"),
    OUT_FOR_DELIVERY("На доставке"),
    READY_FOR_DELIVERY("Готово к доставке"),
    DELIVERED("Доставлено"),
    CANCELLED("Отменён"),
}
