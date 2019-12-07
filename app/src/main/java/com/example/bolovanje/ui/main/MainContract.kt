package com.example.bolovanje.ui.main

import android.view.View
import com.example.bolovanje.ui.base.BaseContract

class MainContract {

    interface Presenter: BaseContract.BasePresenter<View>{

    }

    interface View{
        fun showHomeFragment()
        fun showEmployerFragment()
        fun showAllEmployersFragment()
        fun showTenOrMoreEmployersFragment()
        fun showSearchFragment()
    }
}