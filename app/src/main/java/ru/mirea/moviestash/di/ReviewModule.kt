package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.review.ReviewViewModel

@Module
interface ReviewModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReviewViewModel::class)
    fun bindReviewViewModel(impl: ReviewViewModel): ViewModel
}