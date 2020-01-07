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
        fun updateDaysWithoutExcuse(position: Int): Observable<Employer>
        fun deleteEmployer(position: Int): Observable<Boolean>
        fun editEmployer(position: Int, firstName: String, lastName: String, selectedDays: MutableList<Calendar>): Observable<Pair<Boolean, Employer>>
        fun resetDatesForNewMonth()
        fun getSelectedDaysWithExcuseForEmployer(position: Int): Observable<MutableList<Calendar>>
        fun findMonthDates(month: String, selectedDays: MutableList<String>): String
    }

    interface View{
        fun createButtonClickSearch(): Observable<String>
        fun createTextChangeSearch(): Observable<String>
        fun showProgressBar(show: Boolean)
        fun showErrorMessage(error: String)
        fun showData(list: List<Employer>, databaseKeyList: List<String>)
        fun showCalendarForUpdatingDatesWithothExcuse(date: MutableList<Calendar>, position: Int)
        fun showCalendarForUpdatingEmployer(dates: MutableList<Calendar>, position: Int)
        fun hideCalendar()
    }
}