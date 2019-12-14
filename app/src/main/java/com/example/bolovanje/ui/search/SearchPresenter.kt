package com.example.bolovanje.ui.search

import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.model.Employer
import com.example.bolovanje.model.FirebaseRepository
import com.example.bolovanje.utils.DateUtils
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class SearchPresenter: SearchContract.Presenter {

    private lateinit var view: SearchContract.View
    var compositeDisposable = CompositeDisposable()
    var selectedDates = mutableListOf<Calendar>(Calendar.getInstance())
    var datesThisMonthList = mutableListOf<Calendar>(Calendar.getInstance())
    var searchList: MutableList<Employer> = mutableListOf()
    var listOfDatabseKeys: MutableList<String> = mutableListOf()

    @Inject
    lateinit var employers: Employer

    override fun destroy() {
        compositeDisposable.clear()
    }

    override fun attach(view: SearchContract.View) {
        this.view = view
    }

    override fun searchData(searchData: String): Observable<Pair<List<Employer>, List<String>>> {
        return Observable.create<Pair<List<Employer>, List<String>>> { emitter ->
            FirebaseRepository.readAllData().subscribe {
                if(checkIfSearchDataExists(it.first, searchData, it.second).first.isNotEmpty()){
                    emitter.onNext(Pair(checkIfSearchDataExists(it.first, searchData, it.second).first,
                        checkIfSearchDataExists(it.first, searchData, it.second).second))
                }else{
                    emitter.onNext(Pair(listOf(), listOf()))
                }
            }
        }
    }

    private fun checkIfSearchDataExists(employerList: List<Employer>, searchData: String, listOfKeys: List<String>): Pair<List<Employer>, List<String>>{
         searchList.clear()
            listOfDatabseKeys.clear()

            if(employerList.isNotEmpty()){
                var count = 0
                employerList.forEach {employer->
                    if(employer.firstName!!.contains(searchData, true) || employer.lastName!!.contains(searchData, true)){
                        searchList.add(employer)
                        listOfDatabseKeys.add(listOfKeys[count])
                    }
                    count +=1
                }
            }

        return Pair(searchList, listOfDatabseKeys)

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
        return FirebaseRepository.addDaysWithExcuse(listOfDatabseKeys[position], selectedDates, datesThisMonthList)
    }

    override fun deleteEmployer(position: Int): Observable<Boolean> {
        return FirebaseRepository.deleteEmployer(listOfDatabseKeys[position])
    }

    override fun editEmployer(position: Int, firstName: String, lastName: String, selectedDays: MutableList<Calendar>): Observable<Pair<Boolean, Employer>> {
        return FirebaseRepository.editEmployer(listOfDatabseKeys[position], firstName, lastName, selectedDays)
    }

    override fun resetDatesForNewMonth() {
        FirebaseRepository.resetDatesForNewMonth()
    }
}