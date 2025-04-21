package com.freddypizza.website.response

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
)
