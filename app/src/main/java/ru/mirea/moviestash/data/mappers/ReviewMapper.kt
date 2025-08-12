package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.ReviewDto
import ru.mirea.moviestash.domain.entities.ReviewEntity

fun ReviewDto.toEntity(): ReviewEntity {
    return ReviewEntity(
        id = id,
        contentId = contentId,
        userId = userId,
        title = title,
        description = description,
        opinion = opinion.name,
        date = date,
        userName = userName
    )
}

fun List<ReviewDto>.toListEntity(): List<ReviewEntity> {
    return map { it.toEntity() }
}