package com.example.bolovanje.di.component

import android.app.Application
import com.example.bolovanje.SickLeaveApplication
import com.example.bolovanje.di.module.ActivityModule
import com.example.bolovanje.di.module.ApplicationModule
import com.example.bolovanje.di.module.EmployerModule
import com.example.bolovanje.di.module.FragmentModule
import com.example.bolovanje.ui.main.MainActivity
import com.example.bolovanje.ui.employers.EmployersFragment
import com.example.bolovanje.ui.employers.allEmployers.AllEmployersFragment
import com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers.TenOrMoreDaysEmployersFragment
import com.example.bolovanje.ui.home.HomeFragment
import com.example.bolovanje.ui.search.SearchFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class, EmployerModule::class, FragmentModule::class, ActivityModule::class])
interface ApplicationComponent: AndroidInjector<SickLeaveApplication> {

    // TODO: check out here and use modules to provide fragments and activities also extend daaggerfragment which handles inject by itself
    // https://medium.com/@khreniak/dagger-scopes-simple-explanation-184684707227
    fun inject(act: MainActivity)
    fun inject(frag: EmployersFragment)
    fun inject(frag: HomeFragment)
    fun inject(frag: SearchFragment)
    fun inject(frag: AllEmployersFragment)
    fun inject(frag: TenOrMoreDaysEmployersFragment)


    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder
        fun applicationModule(applicationModule: ApplicationModule): Builder
        fun employerModule(employerModule: EmployerModule): Builder
        fun fragmentModule(fragmentModule: FragmentModule): Builder
        fun activityModule(activityModule: ActivityModule): Builder
        fun build(): ApplicationComponent
    }
}