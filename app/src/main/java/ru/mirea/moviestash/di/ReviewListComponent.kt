package ru.mirea.moviestash.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.mirea.moviestash.presentation.review_list.ReviewListFragment

@Subcomponent(
    modules = [ReviewListModule::class]
)
interface ReviewListComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance contentId: Int
        ): ReviewListComponent
    }

    fun inject(fragment: ReviewListFragment)
}