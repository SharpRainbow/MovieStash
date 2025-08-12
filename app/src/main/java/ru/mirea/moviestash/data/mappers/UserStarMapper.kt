package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.UserStarDto
import ru.mirea.moviestash.domain.entities.UserStarEntity

fun UserStarDto.toEntity(): UserStarEntity {
    return UserStarEntity(
        id = id,
        rating = rating,
        contentId = contentId,
        userId = userId
    )
}