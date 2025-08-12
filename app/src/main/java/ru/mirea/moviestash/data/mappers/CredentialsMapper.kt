package ru.mirea.moviestash.data.mappers

import ru.mirea.moviestash.domain.entities.CredentialsEntity
import ru.mirea.moviestash.data.database.CredentialsDbModel

fun CredentialsDbModel.toEntity(): CredentialsEntity {
    return CredentialsEntity(
        login = login,
        password = password,
    )
}

fun List<CredentialsDbModel>.toEntityList(): List<CredentialsEntity> {
    return map { it.toEntity() }
}