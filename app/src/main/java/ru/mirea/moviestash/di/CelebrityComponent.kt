package ru.mirea.moviestash.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.mirea.moviestash.presentation.celebrity.CelebrityFragment

@Subcomponent(
    modules = [CelebrityModule::class]
)
interface CelebrityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance celebrityId: Int
        ): CelebrityComponent
    }

    fun inject(fragment: CelebrityFragment)
}