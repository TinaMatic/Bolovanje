package com.example.bolovanje.di.module

import com.example.bolovanje.model.Employer
import com.example.bolovanje.model.EmployerRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ApplicationModule::class])
class EmployerModule {

    @Singleton
    @Provides
    fun provideEmployerRepository(firstName: String, lastName: String, excuse: Boolean, numOfDays: Int): EmployerRepository{
        return EmployerRepository(Employer( firstName, lastName, excuse, numOfDays))
    }
}