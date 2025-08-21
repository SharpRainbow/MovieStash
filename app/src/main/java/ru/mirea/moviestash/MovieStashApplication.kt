package ru.mirea.moviestash

import android.app.Application
import ru.mirea.moviestash.di.ApplicationComponent
import ru.mirea.moviestash.di.DaggerApplicationComponent

class MovieStashApplication: Application() {

    val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}