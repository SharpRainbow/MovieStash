package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.CollectionDto
import ru.mirea.moviestash.domain.entities.CollectionEntity

fun CollectionDto.toEntity(): CollectionEntity {
    return CollectionEntity(
        id = id,
        name = name,
        description = description,
        userId = userId
    )
}

fun List<CollectionDto>.toListEntity(): List<CollectionEntity> {
    return map { it.toEntity() }
}