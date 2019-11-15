package com.example.bolovanje.di.component

import android.app.Application
import com.example.bolovanje.BolovanjeApplication
import com.example.bolovanje.di.module.ApplicationModule
import com.example.bolovanje.di.module.EmployerModule
import com.example.bolovanje.ui.MainActivity
import com.example.bolovanje.ui.employers.EmployersFragment
import com.example.bolovanje.ui.employers.allEmployers.AllEmployersFragment
import com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers.TenOrMoreDaysEmployersFragment
import com.example.bolovanje.ui.home.HomeFragment
import com.example.bolovanje.ui.search.SearchFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, ApplicationModule::class, EmployerModule::class])
interface ApplicationComponent: AndroidInjector<BolovanjeApplication> {

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
        fun build(): ApplicationComponent
    }
}