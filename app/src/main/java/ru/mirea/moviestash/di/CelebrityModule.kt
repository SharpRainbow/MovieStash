package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.celebrity.CelebrityViewModel

@Module
interface CelebrityModule {

    @Binds
    @IntoMap
    @ViewModelKey(CelebrityViewModel::class)
    fun bindCelebrityViewModel(impl: CelebrityViewModel): ViewModel
}