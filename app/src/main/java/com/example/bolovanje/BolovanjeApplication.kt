package com.example.bolovanje

import android.app.Activity
import android.app.Application
import com.example.bolovanje.di.component.ApplicationComponent
import com.example.bolovanje.di.component.DaggerApplicationComponent
import com.example.bolovanje.di.module.ApplicationModule
import com.example.bolovanje.di.module.EmployerModule
import com.example.bolovanje.di.module.FragmentModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

open class BolovanjeApplication: Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    private lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerApplicationComponent.builder()
            .application(this)
            .applicationModule(ApplicationModule(applicationContext))
            .employerModule(EmployerModule())
            .fragmentModule(FragmentModule())
            .build()
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }

    fun getBolovanjeComponent(): ApplicationComponent{
        return appComponent
    }

}