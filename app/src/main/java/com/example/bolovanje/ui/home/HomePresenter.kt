package com.example.bolovanje.ui.home

import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.utils.DateUtils
import io.reactivex.Observable
import java.util.*

class HomePresenter: HomeContract.Presenter {

    private lateinit var view : HomeContract.View

    var selectedDates = mutableListOf<Calendar>(Calendar.getInstance())



    override fun selectDates(dates: MutableList<Calendar>): Observable<ConfirmDates> {
        if(dates.isEmpty()){
            selectedDates = mutableListOf(Calendar.getInstance())
        }else{
            selectedDates = dates
        }

        return Observable.fromCallable {
            ConfirmDates(selectedDates, DateUtils.getSelectedDaysNumber(selectedDates))
        }
    }

    override fun loadData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeDataToFirebase() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}