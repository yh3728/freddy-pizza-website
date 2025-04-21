package com.freddypizza.website.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class InvalidRefreshTokenException : Exception("Invalid refresh token")

@ResponseStatus(HttpStatus.FORBIDDEN)
class ExpiredRefreshTokenException : Exception("Refresh token has expired")
