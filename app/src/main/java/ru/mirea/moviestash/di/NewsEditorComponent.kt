package ru.mirea.moviestash.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.mirea.moviestash.presentation.news_editor.NewsEditorFragment

@Subcomponent(
    modules = [NewsEditorModule::class]
)
interface NewsEditorComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance newsId: Int
        ): NewsEditorComponent
    }

    fun inject(fragment: NewsEditorFragment)
}