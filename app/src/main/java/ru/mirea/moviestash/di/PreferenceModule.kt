package ru.mirea.moviestash.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides

@Module
object PreferenceModule {

    @Provides
    fun providePreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
    }

    private const val PREFERENCE_NAME = "AUTH"
}