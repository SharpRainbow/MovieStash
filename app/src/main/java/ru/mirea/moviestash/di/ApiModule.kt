package ru.mirea.moviestash.di

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi

@Module
object ApiModule {

    @Provides
    @ApplicationScope
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ApiProvider.BASE_URL)
            .build()
    }

    @Provides
    @ApplicationScope
    fun provideMovieStashApi(retrofit: Retrofit): MovieStashApi {
        return retrofit.create(MovieStashApi::class.java)
    }
}