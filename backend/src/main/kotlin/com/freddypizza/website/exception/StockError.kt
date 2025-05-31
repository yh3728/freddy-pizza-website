package com.freddypizza.website.exception

data class StockError(
    val productId: Long,
    val requestedQuantity: Int,
    val availableQuantity: Int,
)
