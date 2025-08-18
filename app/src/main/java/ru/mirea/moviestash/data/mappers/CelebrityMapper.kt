package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.CelebrityBaseDto
import ru.mirea.moviestash.data.api.dto.CelebrityDto
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity

fun CelebrityBaseDto.toEntityBase() = CelebrityEntityBase(
    id = id,
    name = name,
    image = image,
    birthDate = date.orEmpty()
)

fun List<CelebrityBaseDto>.toListEntityBase(): List<CelebrityEntityBase> {
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

fun CelebrityInContentDto.toEntity() = CelebrityInContentEntity(
    id = id,
    name = name,
    image = image,
    description = description,
    role = role
)

fun List<CelebrityInContentDto>.toListEntity(): List<CelebrityInContentEntity> {
    return map { it.toEntity() }
}