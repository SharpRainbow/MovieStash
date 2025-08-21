package ru.mirea.moviestash.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.mirea.moviestash.presentation.review_editor.ReviewEditorFragment

@Subcomponent(
    modules = [ReviewEditorModule::class]
)
interface ReviewEditorComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance @ReviewIdQualifier reviewId: Int,
            @BindsInstance @ContentIdQualifier contentId: Int
        ): ReviewEditorComponent
    }

    fun inject(fragment: ReviewEditorFragment)
}