package com.example.neuralnetworkkotlin

import android.app.Application
import com.example.neuralnetworkkotlin.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initKoin()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            if (BuildConfig.DEBUG) Level.DEBUG
            else Level.ERROR
            modules(
                repositoryModule,
            )
        }
    }


}