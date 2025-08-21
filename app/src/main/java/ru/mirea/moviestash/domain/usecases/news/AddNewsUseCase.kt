package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.NewsRepository
import javax.inject.Inject

class AddNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        title: String,
        content: String,
        imageName: String?,
        image: ByteArray?
    ) {
        newsRepository.addNews(
            authRepository.getValidToken(),
            title = title,
            description = content,
            imageName = imageName,
            image = image
        )
    }
}