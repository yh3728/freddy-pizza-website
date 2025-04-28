package com.freddypizza.website.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class InvalidRefreshTokenException(
    message: String = "Invalid refresh token",
) : Exception(message)
