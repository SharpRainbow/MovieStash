package ru.mirea.moviestash.di

import dagger.Binds
import dagger.Module
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.CelebrityRepositoryImpl
import ru.mirea.moviestash.data.CollectionRepositoryImpl
import ru.mirea.moviestash.data.ContentRepositoryImpl
import ru.mirea.moviestash.data.CredentialsRepositoryImpl
import ru.mirea.moviestash.data.GenreRepositoryImpl
import ru.mirea.moviestash.data.NewsRepositoryImpl
import ru.mirea.moviestash.data.OpinionRepositoryImpl
import ru.mirea.moviestash.data.ReviewRepositoryImpl
import ru.mirea.moviestash.data.UserRepositoryImpl
import ru.mirea.moviestash.data.UserStarRepositoryImpl
import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.CelebrityRepository
import ru.mirea.moviestash.domain.CollectionRepository
import ru.mirea.moviestash.domain.ContentRepository
import ru.mirea.moviestash.domain.CredentialsRepository
import ru.mirea.moviestash.domain.GenreRepository
import ru.mirea.moviestash.domain.NewsRepository
import ru.mirea.moviestash.domain.OpinionRepository
import ru.mirea.moviestash.domain.ReviewRepository
import ru.mirea.moviestash.domain.UserRepository
import ru.mirea.moviestash.domain.UserStarRepository

@Module
interface RepositoryModule {

    @Binds
    @ApplicationScope
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @ApplicationScope
    fun provideCelebrityRepository(impl: CelebrityRepositoryImpl): CelebrityRepository

    @Binds
    @ApplicationScope
    fun provideCollectionRepository(impl: CollectionRepositoryImpl): CollectionRepository

    @Binds
    @ApplicationScope
    fun provideContentRepository(impl: ContentRepositoryImpl): ContentRepository

    @Binds
    @ApplicationScope
    fun provideCredentialsRepository(impl: CredentialsRepositoryImpl): CredentialsRepository

    @Binds
    @ApplicationScope
    fun provideGenreRepository(impl: GenreRepositoryImpl): GenreRepository

    @Binds
    @ApplicationScope
    fun provideOpinionRepository(impl: OpinionRepositoryImpl): OpinionRepository

    @Binds
    @ApplicationScope
    fun provideReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @ApplicationScope
    fun provideUserStarRepository(impl: UserStarRepositoryImpl): UserStarRepository

    @Binds
    @ApplicationScope
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @ApplicationScope
    fun provideNewsRepository(impl: NewsRepositoryImpl): NewsRepository
}