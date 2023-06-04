package com.example.neuralnetworkkotlin.di

import com.example.neuralnetworkkotlin.repo.BannersRepo
import org.koin.dsl.module

val repositoryModule = module {
    single { BannersRepo() }
}