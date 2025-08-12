package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.UserDto
import ru.mirea.moviestash.domain.entities.UserEntity

fun UserDto.toEntity() = UserEntity(
    id = id,
    login = login,
    nickname = nickname,
    email = email,
    isBanned = isBanned,
    banDate = banDate.orEmpty(),
    banReason = banReason
)