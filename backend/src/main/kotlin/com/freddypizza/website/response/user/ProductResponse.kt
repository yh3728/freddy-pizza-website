package com.freddypizza.website.response.user

import com.freddypizza.website.enums.ProductCategory
import java.math.BigDecimal

data class ProductResponse (
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val category: ProductCategory
)