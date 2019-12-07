package com.example.bolovanje.di.module

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("DEPRECATION")
@Module
class ApplicationModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context{
        return context
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(context)
    }


}