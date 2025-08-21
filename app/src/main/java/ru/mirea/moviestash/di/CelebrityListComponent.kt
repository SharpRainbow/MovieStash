package ru.mirea.moviestash.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.mirea.moviestash.presentation.celebrity_list.CelebrityListFragment

@Subcomponent(
    modules = [CelebrityListModule::class]
)
interface CelebrityListComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance contentId: Int,
            @BindsInstance actors: Boolean
        ): CelebrityListComponent
    }

    fun inject(fragment: CelebrityListFragment)
}