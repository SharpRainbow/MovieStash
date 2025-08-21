package ru.mirea.moviestash.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.mirea.moviestash.presentation.review.ReviewFragment

@Subcomponent(
    modules = [ReviewModule::class]
)
interface ReviewComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance reviewId: Int
        ): ReviewComponent
    }

    fun inject(fragment: ReviewFragment)
}