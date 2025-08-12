package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.BannedUserDto
import ru.mirea.moviestash.domain.entities.BannedUserEntity

fun BannedUserDto.toEntity(): BannedUserEntity {
    return BannedUserEntity(
        id = id,
        email = email,
        nickname = nickname,
        banDate = banDate.orEmpty(),
        banReason = banReason.orEmpty()
    )
}

fun List<BannedUserDto>.toEntityList(): List<BannedUserEntity> {
    return map { it.toEntity() }
}