package com.freddypizza.website.request.admin

import org.springframework.web.multipart.MultipartFile

data class AdminProductImageRequest(
    val image: MultipartFile,
)
