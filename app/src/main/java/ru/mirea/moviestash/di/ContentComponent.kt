package ru.mirea.moviestash.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.mirea.moviestash.presentation.content.ContentFragment

@Subcomponent(
    modules = [ContentModule::class]
)
interface ContentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance contentId: Int
        ): ContentComponent
    }

    fun inject(fragment: ContentFragment)
}