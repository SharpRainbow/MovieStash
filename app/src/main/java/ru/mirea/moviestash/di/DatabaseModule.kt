package ru.mirea.moviestash.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ru.mirea.moviestash.data.database.CredentialsDatabase
import ru.mirea.moviestash.data.database.CredentialsDao

@Module
object DatabaseModule {

    @Provides
    @ApplicationScope
    fun provideCredentialsDatabase(application: Application): CredentialsDatabase {
        return CredentialsDatabase.getDatabase(application)
    }

    @Provides
    fun provideCredentialsDao(credentialsDatabase: CredentialsDatabase): CredentialsDao {
        return credentialsDatabase.credsDao()
    }
}