package com.example.bolovanje.ui.search

import android.view.View
import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.model.Employer
import com.example.bolovanje.ui.base.BaseContract
import io.reactivex.Observable
import java.util.*

class SearchContract {

    interface Presenter: BaseContract.BasePresenter<View>{
        fun searchData(searchData: String): Observable<Pair<List<Employer>, List<String>>>
        fun selectDates(dates: MutableList<Calendar>): Observable<Pair<ConfirmDates, MutableList<Calendar>>>
        fun addDaysWithExcuse(position: Int): Observable<Employer>
//        fun deleteEmployer(databaseKey: String)
//        fun edit()
    }

    interface View{
        fun createButtonClickSearch(): Observable<String>
        fun createTextChangeSearch(): Observable<String>
        fun showProgressBar(show: Boolean)
        fun showErrorMessage(error: String)
        fun showData(list: List<Employer>, databaseKeyList: List<String>)
        fun showCalendarForAddingDatesWithExcuse(date: MutableList<Calendar>, position: Int)
        fun showCalendarForUpdatingEmployer(dates: MutableList<Calendar>, position: Int)
        fun hideCalendar()

    }
//
//    interface Model{
//        fun onDataChanged()
//        fun onCancelled()
//    }
}