package com.example.bolovanje.di.module

import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.model.Employer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ApplicationModule::class])
class EmployerModule {

//    @Singleton
//    @Provides
//    fun provideEmployerRepository(firstName: String, lastName: String, excuse: Boolean, dates: ConfirmDates): Employer{
//        return Employer( firstName, lastName, excuse, dates)
//    }

//    @Singleton
//    @Provides
//    fun provideConfirmDatesRepository(selectedDays: MutableList<Calendar>, numOfDays: String): ConfirmDates{
//        return ConfirmDates(selectedDays, numOfDays)
//    }
}