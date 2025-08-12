package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.data.api.dto.ContentDto
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase

fun ContentDto.toEntityBase(): ContentEntityBase {
    return ContentEntityBase(
        id = id,
        name = name,
        image = image,
        rating = rating,
        releaseDate = releaseDate.orEmpty(),
    )
}

fun ContentDto.toEntity(): ContentEntity {
    return ContentEntity(
        id = id,
        name = name,
        image = image,
        rating = rating,
        ratingKinopoisk = ratingKinopoisk,
        ratingImdb = ratingImdb,
        releaseDate = releaseDate.orEmpty(),
        budget = budget,
        boxOffice = boxOffice,
        countries = countries.joinToString(", ") { it.name },
        genres = genres.joinToString(" · ") { it.name },
        duration = duration,
        description = description
    )
}

fun List<ContentDto>.toListEntityBase(): List<ContentEntityBase> {
    return map { it.toEntityBase() }
}