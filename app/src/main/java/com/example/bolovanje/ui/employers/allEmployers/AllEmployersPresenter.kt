package com.example.bolovanje.ui.employers.allEmployers

import com.example.bolovanje.model.Employer
import com.example.bolovanje.model.FirebaseRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AllEmployersPresenter : AllEmployersContract.Presenter {

    private lateinit var view: AllEmployersContract.View
    private var compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var employers: Employer

    override fun loadData() {

       compositeDisposable.add(FirebaseRepository.readData()
           .subscribeOn(Schedulers.io())
           .map {
               if (it.first.isNotEmpty()){
                   Pair(it.first as ArrayList<Employer>, it.second as ArrayList<String>)
//                   it.first as ArrayList<Employer>

               }else{
                   Pair(arrayListOf(), arrayListOf())
//                   arrayListOf()
               }}
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe (
            {
               if(it.first.isNotEmpty()){
                   view.showProgressBar(false)
                   view.showData(it.first)
               }else{
                   view.showProgressBar(false)
                   view.showData(it.first)
               }

           },{error->
               view.showErrorMessage(error.toString())
               view.showProgressBar(true)
           }))
    }


    override fun attach(view: AllEmployersContract.View) {
        this.view = view
    }

    override fun destroy() {
        compositeDisposable.dispose()
    }

}