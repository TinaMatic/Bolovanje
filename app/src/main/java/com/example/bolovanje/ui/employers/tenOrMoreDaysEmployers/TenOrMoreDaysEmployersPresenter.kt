package com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers

import com.example.bolovanje.model.Employer
import com.example.bolovanje.model.FirebaseRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TenOrMoreDaysEmployersPresenter: TenOrMoreDaysEmployersContract.Presenter {

    private lateinit var view: TenOrMoreDaysEmployersContract.View
    private var compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var employers: Employer

    override fun loadData() {

        compositeDisposable.add(FirebaseRepository.readAllData()
            .subscribeOn(Schedulers.io())
            .switchMap {
                filterNumOfDays(it.first)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {listOfEmployer->
                    if(listOfEmployer.isNotEmpty()){
                        val tempList = listOfEmployer.sortedByDescending {
                            it.numOfDays
                        }
                        view.showProgressBar(false)
                        view.showData(tempList)
                    }else{
                        view.showProgressBar(false)
                        view.showData(emptyList())
                    }

                },{error->
                    view.showErrorMessage()
                    view.showProgressBar(true)
                }))
    }

    private fun filterNumOfDays(listOfEmployers: List<Employer>): Observable<List<Employer>> {
        return Observable.fromCallable {
            listOfEmployers.filter {
                it.numOfDays >= 10
            }
        }
    }

    override fun destroy() {
        compositeDisposable.dispose()
    }

    override fun attach(view: TenOrMoreDaysEmployersContract.View) {
        this.view = view
    }

}