package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.celebrity_list.CelebrityListViewModel

@Module
interface CelebrityListModule {

    @Binds
    @IntoMap
    @ViewModelKey(CelebrityListViewModel::class)
    fun bindCelebrityListViewModel(impl: CelebrityListViewModel): ViewModel
}