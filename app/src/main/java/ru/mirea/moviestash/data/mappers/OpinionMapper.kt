package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.OpinionDto
import ru.mirea.moviestash.domain.entities.OpinionEntity

fun OpinionDto.toEntity() = OpinionEntity(
    id = id,
    name = name,
)

fun List<OpinionDto>.toEntityList(): List<OpinionEntity> {
    return map { it.toEntity() }
}