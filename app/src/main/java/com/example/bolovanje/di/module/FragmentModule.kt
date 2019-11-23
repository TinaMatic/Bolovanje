package com.example.bolovanje.di.module

import com.example.bolovanje.ui.employers.allEmployers.AllEmployersContract
import com.example.bolovanje.ui.employers.allEmployers.AllEmployersPresenter
import com.example.bolovanje.ui.home.HomeContract
import com.example.bolovanje.ui.home.HomePresenter
import dagger.Module
import dagger.Provides

@Module(includes = [ApplicationModule::class])
class FragmentModule {

    @Provides
    fun provideHomePresenter(): HomeContract.Presenter{
        return HomePresenter()
    }

    @Provides
    fun provideAllEmployersPresenter(): AllEmployersContract.Presenter{
        return AllEmployersPresenter()
    }

}