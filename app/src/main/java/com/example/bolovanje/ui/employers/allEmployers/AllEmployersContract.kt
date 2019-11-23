package com.example.bolovanje.ui.employers.allEmployers

import android.view.View
import com.example.bolovanje.model.Employer
import com.example.bolovanje.ui.base.BaseContract

class AllEmployersContract {

    interface Presenter: BaseContract.BasePresenter<View>{
        fun loadData()
//        fun destroy()
//        fun attach(view: AllEmployersContract.View)
    }

    interface View{
        fun showProgressBar(show: Boolean)
        fun showErrorMessage(error: String)
        fun showData(list: MutableList<Employer>)
    }
}