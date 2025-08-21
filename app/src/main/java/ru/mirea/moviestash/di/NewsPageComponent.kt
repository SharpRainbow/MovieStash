package ru.mirea.moviestash.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.mirea.moviestash.presentation.news.NewsFragment

@Subcomponent(
    modules = [NewsPageModule::class]
)
interface NewsPageComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance newsId: Int
        ): NewsPageComponent
    }

    fun inject(fragment: NewsFragment)
}