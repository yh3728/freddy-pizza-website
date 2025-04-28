package com.freddypizza.website.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(UsernameAlreadyExistsException::class)
    fun handleUsernameAlreadyExists(ex: UsernameAlreadyExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(
                error = HttpStatus.CONFLICT.name,
                message = ex.message ?: "Пользователь с таким именем уже существует",
            ),
            HttpStatus.CONFLICT,
        )

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(ex: ProductNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(
                error = HttpStatus.NOT_FOUND.name,
                message = ex.message ?: "Продукт не найден",
            ),
            HttpStatus.NOT_FOUND,
        )

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefreshToken(ex: InvalidRefreshTokenException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(
                error = HttpStatus.FORBIDDEN.name,
                message = ex.message ?: "Неверный токен обновления",
            ),
            HttpStatus.FORBIDDEN,
        )

    @ExceptionHandler(StaffNotFoundException::class)
    fun handleStaffNotFoundException(ex: StaffNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                error = "NOT_FOUND",
                message = ex.message ?: "Сотрудник не найден",
            )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                error = "UNAUTHORIZED",
                message = ex.message ?: "Неверные учетные данные",
            )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                error = HttpStatus.INTERNAL_SERVER_ERROR.name,
                message = "Произошла непредвиденная ошибка. Попробуйте снова позже: ",
            )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
