package com.example.bolovanje.ui.home

import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.ui.base.BaseContract
import io.reactivex.Observable
import java.util.*

class HomeContract {

    interface Presenter{
        fun loadData()
        fun writeDataToFirebase()
        fun selectDates(dates: MutableList<Calendar>) : Observable<ConfirmDates>
    }

    interface View {
        fun showCalendar(dates: MutableList<Calendar>)
        fun hideCalendar()
        fun showProgressBar()
    }
}