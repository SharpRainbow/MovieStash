package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.NewsRepository

class UpdateNewsUseCase(
    private val newsRepository: NewsRepository,
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(
        newsId: Int,
        title: String,
        content: String,
        imageName: String?,
        image: ByteArray?
    ) = newsRepository.updateNews(
            authRepository.getValidToken(),
            newsId = newsId,
            title = title,
            description = content,
            imageName = imageName,
            image = image
        )
}