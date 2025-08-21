package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.review_list.ReviewListViewModel

@Module
interface ReviewListModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReviewListViewModel::class)
    fun bindReviewListViewModel(impl: ReviewListViewModel): ViewModel
}