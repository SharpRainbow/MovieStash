package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.NewsDto
import ru.mirea.moviestash.domain.entities.NewsEntity

fun NewsDto.toEntity() = NewsEntity(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    date = date
)

fun List<NewsDto>.toListEntity(): List<NewsEntity> {
    return map { it.toEntity() }
}