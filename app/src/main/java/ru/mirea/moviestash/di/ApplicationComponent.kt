package ru.mirea.moviestash.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component

@Component(modules = [ApplicationModule::class, DatabaseModule::class, PreferenceModule::class,
    ApiModule::class, RepositoryModule::class])
@ApplicationScope
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }

    fun rootDestinationsComponentFactory(): RootDestinationsComponent.Factory

    fun celebrityComponentFactory(): CelebrityComponent.Factory

    fun celebrityListComponentFactory(): CelebrityListComponent.Factory

    fun collectionContentComponentFactory(): CollectionContentComponent.Factory

    fun contentComponentFactory(): ContentComponent.Factory

    fun newsComponentFactory(): NewsPageComponent.Factory

    fun newsEditorComponentFactory(): NewsEditorComponent.Factory

    fun reviewComponentFactory(): ReviewComponent.Factory

    fun reviewEditorComponentFactory(): ReviewEditorComponent.Factory

    fun reviewListComponentFactory(): ReviewListComponent.Factory
}