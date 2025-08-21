package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.content.ContentViewModel

@Module
interface ContentModule {

    @Binds
    @IntoMap
    @ViewModelKey(ContentViewModel::class)
    fun bindContentViewModel(impl: ContentViewModel): ViewModel
}