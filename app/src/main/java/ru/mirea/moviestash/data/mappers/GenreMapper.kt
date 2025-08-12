package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.GenreDto
import ru.mirea.moviestash.domain.entities.GenreEntity

fun GenreDto.toEntity(): GenreEntity {
    return GenreEntity(
        id = id,
        name = name,
        icon = icon
    )
}

fun List<GenreDto>.toListEntity(): List<GenreEntity> {
    return map { it.toEntity() }
}