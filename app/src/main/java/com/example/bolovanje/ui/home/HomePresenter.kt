package com.example.bolovanje.ui.home

import android.util.Log
import android.widget.Toast
import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.model.Employer
import com.example.bolovanje.utils.DateUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.format.DateTimeFormatterBuilder
import java.util.*
import javax.inject.Inject

class HomePresenter: HomeContract.Presenter {

    private lateinit var view : HomeContract.View

    var selectedDates = mutableListOf<Calendar>(Calendar.getInstance())
    var numOfDays: Int = 1
    var mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    var compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var employer: Employer

    @Inject
    lateinit var confirmDates: ConfirmDates


    override fun selectDates(dates: MutableList<Calendar>): Observable<ConfirmDates> {
        if(dates.isEmpty()){
            selectedDates = mutableListOf(Calendar.getInstance())
        }else{
            selectedDates = dates
        }

        numOfDays = selectedDates.size

        Log.i("selected days: ", selectedDates.toString())

        return Observable.fromCallable { ConfirmDates(selectedDates, DateUtils.getSelectedDaysNumber(selectedDates)) }
    }

    override fun resetCalendar() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeData(firstName: String, lastName: String, excuse: Boolean) {
//        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().
        var databaseRef = mFirebaseDatabaseRef.child("Employer")
        var doesExist = true

        confirmDates = ConfirmDates(selectedDates, numOfDays.toString())

        var formattedDates : MutableList<String> = mutableListOf()

        //format selected days so they are all in formatt dd.mm
        compositeDisposable.add(Observable.fromCallable { confirmDates }
            .flatMapIterable {
                it.selectedDays
            }
            .subscribe {
                formattedDates.add(DateUtils.getFormattedDate(it.timeInMillis, DateTimeFormatterBuilder().appendPattern(
                    DateUtils.DATE_FORMAT
                ).toFormatter()))
            })


        employer = Employer(firstName, lastName, excuse,  formattedDates, numOfDays)
//        mFirebaseDatabaseRef!!.child("Employer").setValue(employer)

        //need to check if the employer already exists
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {
                    val orderSnapshot = dataSnapshot.children

                    for (data in orderSnapshot) {
                        val firstNameFirebase = data.child("firstName").value.toString().trim()
                        val lastNameFirebase = data.child("lastName").value.toString().trim()

                        if (firstName.equals(firstNameFirebase) && lastName.equals(lastNameFirebase)) {
                            //if it does add the seleceted dates and overwrite the number
                            Log.i("Employer does exist", firstName)
                            doesExist = true
                            break
                        } else {
                            doesExist = false
                        }
                    }

                    if (!doesExist){
                        addNewEmployer(employer)
                    }
                } else {
                    mFirebaseDatabaseRef!!.child("Employer").push().setValue(employer)
                        .addOnCompleteListener { task: Task<Void> ->
                            if (task.isSuccessful) {
                                view.showSuccessfulMessage()
                                view.showProgressBar(false)
                            } else {
                                view.showErrorMessage()
                            }
                        }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                view.showProgressBar(true)
            }

        })

    }

    fun addNewEmployer(employer: Employer){
        mFirebaseDatabaseRef!!.child("Employer").push().setValue(employer)
            .addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    view.showSuccessfulMessage()
                    view.showProgressBar(false)
                } else {
                    view.showErrorMessage()
                }
            }
    }

    override fun attach(view: HomeContract.View) {
        this.view = view
    }

    override fun destroy(){
        compositeDisposable.clear()
    }



}