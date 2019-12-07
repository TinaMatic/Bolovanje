package com.example.bolovanje.di.module

import com.example.bolovanje.ui.main.MainContract
import com.example.bolovanje.ui.main.MainPresenter
import dagger.Module
import dagger.Provides

@Module(includes = [ApplicationModule::class])
class ActivityModule {

    @Provides
    fun provideMainPresenter(): MainContract.Presenter{
        return MainPresenter()
    }
}