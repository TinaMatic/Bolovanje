package com.example.bolovanje.ui.search

import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.model.Employer
import com.example.bolovanje.model.FirebaseRepository
import com.example.bolovanje.utils.DateUtils
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class SearchPresenter: SearchContract.Presenter {

    private lateinit var view: SearchContract.View
    var compositeDisposable = CompositeDisposable()
    var selectedDates = mutableListOf<Calendar>(Calendar.getInstance())
    var datesThisMonthList = mutableListOf<Calendar>(Calendar.getInstance())
    var mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    var searchList: MutableList<Employer> = mutableListOf()
    var listOfDatabseKeys: MutableList<String> = mutableListOf()
    lateinit var databaseKey: String

    @Inject
    lateinit var employers: Employer

    override fun destroy() {
        compositeDisposable.clear()
    }

    override fun attach(view: SearchContract.View) {
        this.view = view
    }

    override fun searchData(searchData: String): Observable<Pair<List<Employer>, List<String>>> {


        val searchData = FirebaseRepository.searchData(searchData)

        return searchData.map {
            Pair(it, FirebaseRepository.listOfDatabseKeys)
        }
    }

    override fun selectDates(dates: MutableList<Calendar>): Observable<Pair<ConfirmDates, MutableList<Calendar>>> {
        val thisMonth = Calendar.getInstance().get(Calendar.MONTH)
        val tempListDaysThisMonth = mutableListOf<Calendar>()

        if(dates.isEmpty()){
            selectedDates = mutableListOf(Calendar.getInstance())
            datesThisMonthList = mutableListOf(Calendar.getInstance())
        }else{
            selectedDates = dates
            selectedDates.forEach {
                if(it.get(Calendar.MONTH).equals(thisMonth)){
                    tempListDaysThisMonth.add(it)
                }
            }

            if(tempListDaysThisMonth.size != 0){
                datesThisMonthList = tempListDaysThisMonth.distinct() as MutableList<Calendar>
            }else{
                datesThisMonthList = mutableListOf()
            }

        }

        val selectedDatesObservable = Observable.fromCallable { ConfirmDates(selectedDates, DateUtils.getSelectedDaysNumber(selectedDates)) }
        return selectedDatesObservable.map {
            Pair(it, datesThisMonthList)
        }
    }

    override fun addDaysWithExcuse(position: Int): Observable<Employer> {
        return FirebaseRepository.addDaysWithExcuse(position, selectedDates, datesThisMonthList)
    }
}