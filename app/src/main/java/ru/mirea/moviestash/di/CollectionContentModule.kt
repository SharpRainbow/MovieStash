package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.collection_content.CollectionContentViewModel

@Module
interface CollectionContentModule {

    @Binds
    @IntoMap
    @ViewModelKey(CollectionContentViewModel::class)
    fun bindCollectionContentViewModel(impl: CollectionContentViewModel): ViewModel
}