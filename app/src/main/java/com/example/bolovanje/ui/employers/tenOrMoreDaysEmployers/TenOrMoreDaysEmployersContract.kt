package com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers

import android.view.View
import com.example.bolovanje.model.Employer
import com.example.bolovanje.ui.base.BaseContract

class TenOrMoreDaysEmployersContract {

    interface Presenter: BaseContract.BasePresenter<View>{
        fun loadData()
        fun resetDatesForNewMonth()
    }

    interface View{
        fun showProgressBar(show: Boolean)
        fun showErrorMessage()
        fun showData(list: List<Employer>)
    }
}