package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.news.NewsPageViewModel

@Module
interface NewsPageModule {

    @Binds
    @IntoMap
    @ViewModelKey(NewsPageViewModel::class)
    fun bindNewsViewModel(impl: NewsPageViewModel): ViewModel
}