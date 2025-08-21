package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.CollectionRepository
import javax.inject.Inject

class GetPublicCollectionsUseCase @Inject constructor(
    private val repository: CollectionRepository
) {

    operator fun invoke() = repository.getEditorCollectionsFlow()
}