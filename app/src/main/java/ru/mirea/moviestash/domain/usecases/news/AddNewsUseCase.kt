package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.NewsRepository
import javax.inject.Inject

class AddNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(
        title: String,
        content: String,
        imageName: String?,
        image: ByteArray?
    ) {
        newsRepository.addNews(
            jwtManager.getValidToken(),
            title = title,
            description = content,
            imageName = imageName,
            image = image
        )
    }
}