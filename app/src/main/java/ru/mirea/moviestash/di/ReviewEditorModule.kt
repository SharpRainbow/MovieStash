package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.review_editor.ReviewEditorViewModel

@Module
interface ReviewEditorModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReviewEditorViewModel::class)
    fun bindReviewEditorViewModel(impl: ReviewEditorViewModel): ViewModel
}