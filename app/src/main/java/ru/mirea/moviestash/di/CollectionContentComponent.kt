package ru.mirea.moviestash.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.mirea.moviestash.presentation.collection_content.CollectionContentFragment

@Subcomponent(
    modules = [CollectionContentModule::class]
)
interface CollectionContentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance @CollectionIdQualifier collectionId: Int,
            @BindsInstance @UserIdQualifier userId: Int
        ): CollectionContentComponent
    }

    fun inject(fragment: CollectionContentFragment)

}