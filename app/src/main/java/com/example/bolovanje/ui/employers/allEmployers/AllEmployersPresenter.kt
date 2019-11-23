package com.example.bolovanje.ui.employers.allEmployers

import android.util.Log
import android.view.View
import com.example.bolovanje.model.Employer
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class AllEmployersPresenter: AllEmployersContract.Presenter {

    private lateinit var view: AllEmployersContract.View
    private var compositeDisposable = CompositeDisposable()
    var mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    var selectedDays : MutableList<String> = mutableListOf()

    var employerList: MutableList<Employer> = mutableListOf()

    @Inject
    lateinit var employers: Employer

    override fun loadData() {
        var databaseReference = mFirebaseDatabaseRef.child("Employer").orderByChild("numOfDays")


        databaseReference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value != null){
                    var orderSnapshot = dataSnapshot.children

                    for(employer in orderSnapshot){
                        val firstName = employer.child("firstName").value.toString().trim()
                        val lastName = employer.child("lastName").value.toString().trim()
                        val numOfDays = employer.child("numOfDays").value.toString().trim()
                        val excuse = employer.child("excuse").value as Boolean

                        employers = Employer(firstName, lastName, excuse, selectedDays, numOfDays.toInt())
                        employerList.add(employers)

                        employerList.sortByDescending {
                            it.numOfDays
                        }

                        view.showProgressBar(false)
//                        Log.i("Sorted employer list ", employers.numOfDays.toString())
                        view.showData(employerList)
                    }
                }else{
                    view.showProgressBar(false)
                    view.showData(employerList)
                }
            }


            override fun onCancelled(error: DatabaseError) {
                view.showErrorMessage(error.toString())
            }

        })
    }



    override fun attach(view: AllEmployersContract.View) {
        this.view = view
    }

    override fun destroy() {
        compositeDisposable.clear()
    }

}