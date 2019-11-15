package com.example.bolovanje.di.module

import com.example.bolovanje.model.EmployerRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ApplicationModule::class])
class EmployerModule {

    @Singleton
    @Provides
    fun provideEmployerRepository(firstName: String, lastName: String, doznaka: Boolean, numOfDays: Int): EmployerRepository{
        return EmployerRepository(firstName, lastName, doznaka, numOfDays)
    }
}