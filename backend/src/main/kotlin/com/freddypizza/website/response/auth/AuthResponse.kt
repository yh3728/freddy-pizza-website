package com.freddypizza.website.response.auth

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
)
