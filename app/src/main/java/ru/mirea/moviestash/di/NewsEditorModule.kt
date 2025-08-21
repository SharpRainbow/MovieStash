package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.news_editor.NewsEditorViewModel

@Module
interface NewsEditorModule {

    @Binds
    @IntoMap
    @ViewModelKey(NewsEditorViewModel::class)
    fun bindNewsEditorViewModel(impl: NewsEditorViewModel): ViewModel
}