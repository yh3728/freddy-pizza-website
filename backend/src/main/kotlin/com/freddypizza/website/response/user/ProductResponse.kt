package com.freddypizza.website.response.user

import com.freddypizza.website.enums.ProductCategory
import java.math.BigDecimal

data class ProductResponse(
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val imagePath: String? = "/uploads/products/image_placeholder.png",
    val category: ProductCategory,
)
