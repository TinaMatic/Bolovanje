package com.example.bolovanje.di.module

import com.example.bolovanje.ui.home.HomeContract
import com.example.bolovanje.ui.home.HomePresenter
import dagger.Module
import dagger.Provides

@Module
class FragmentModule {

    @Provides
    fun provideHomePresenter(): HomeContract.Presenter{
        return HomePresenter()
    }

}