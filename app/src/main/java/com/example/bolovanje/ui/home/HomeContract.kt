package com.example.bolovanje.ui.home

import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.model.Employer
import com.example.bolovanje.ui.base.BaseContract
import io.reactivex.Observable
import java.util.*

class HomeContract {

    interface Presenter: BaseContract.BasePresenter<View>{
        fun writeData(firstName: String, lastName: String, excuse: Boolean)
        fun selectDates(dates: MutableList<Calendar>) : Observable<ConfirmDates>
        fun resetCalendar()
        fun formatDates(dates: MutableList<Calendar>): MutableList<String>
//        fun addNewEmployer(employer: Employer)
//        fun updateEmployerDates(firebaseListDates: String, datesList: MutableList<Calendar>): MutableList<String>
        fun resetDatesForNewMonth()
    }

    interface View {
        fun showCalendar(dates: MutableList<Calendar>)
        fun hideCalendar()
        fun showProgressBar(show: Boolean)
        fun readData()
        fun showSuccessfulMessage()
        fun showErrorMessage()
        fun showSuccessfulUpdateMessage()
//        fun submitData()
    }
}