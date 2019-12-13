package com.example.bolovanje.di.module

import com.example.bolovanje.ui.employers.allEmployers.AllEmployersContract
import com.example.bolovanje.ui.employers.allEmployers.AllEmployersPresenter
import com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers.TenOrMoreDaysEmployersContract
import com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers.TenOrMoreDaysEmployersPresenter
import com.example.bolovanje.ui.home.HomeContract
import com.example.bolovanje.ui.home.HomePresenter
import com.example.bolovanje.ui.search.SearchContract
import com.example.bolovanje.ui.search.SearchPresenter
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

    @Provides
    fun provideTenOrMoreEmployersPresenter(): TenOrMoreDaysEmployersContract.Presenter{
        return TenOrMoreDaysEmployersPresenter()
    }

    @Provides
    fun provideSearchPresenter(): SearchContract.Presenter{
        return SearchPresenter()
    }

}