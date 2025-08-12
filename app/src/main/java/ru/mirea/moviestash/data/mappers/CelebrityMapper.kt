package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.CelebrityDto
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.CelebrityEntity

fun CelebrityDto.toEntityBase() = CelebrityEntityBase(
    id = id,
    name = name,
    image = image,
    birthDate = birthDate.orEmpty()
)

fun List<CelebrityDto>.toListEntityBase(): List<CelebrityEntityBase> {
    return map { it.toEntityBase() }
}

fun CelebrityDto.toEntity() = CelebrityEntity(
    id = id,
    name = name,
    image = image,
    birthDate = birthDate.orEmpty(),
    death = death.orEmpty(),
    birthPlace = birthPlace,
    career = career,
    height = height
)