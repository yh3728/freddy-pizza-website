package com.freddypizza.website.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class InvalidRefreshTokenException(
    message: String = "Неверный токен обновления",
) : Exception(message)

@ResponseStatus(HttpStatus.CONFLICT)
class UsernameAlreadyExistsException(
    message: String = "Пользователь с таким именем уже существует",
) : Exception(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class ProductNotFoundException(
    message: String = "Продукт не найден",
) : Exception(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class StaffNotFoundException(
    message: String = "Сотрудник не найден",
) : Exception(message)
