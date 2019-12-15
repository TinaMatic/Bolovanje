package com.example.bolovanje.ui.home

import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.model.Employer
import com.example.bolovanje.model.FirebaseRepository
import com.example.bolovanje.utils.DateUtils
import com.example.bolovanje.utils.DateUtils.Companion.formatDates
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class HomePresenter: HomeContract.Presenter {

    private lateinit var view : HomeContract.View

    var selectedDates = mutableListOf<Calendar>(Calendar.getInstance())
//    var timesInMillisList: MutableList<Long> = mutableListOf()
    var numOfDays: Int = 1
    var daysThisMonthList = mutableListOf<Calendar>(Calendar.getInstance())
    var daysWithExcuseList : MutableList<Calendar> = mutableListOf()
    var daysWithoutExcuseList : MutableList<Calendar> = mutableListOf()
    var mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    var compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var employer: Employer

    @Inject
    lateinit var confirmDates: ConfirmDates


    override fun selectDates(dates: MutableList<Calendar>): Observable<ConfirmDates> {
        val thisMonth = Calendar.getInstance().get(Calendar.MONTH)
        val tempListDaysThisMonth = mutableListOf<Calendar>()

        if(dates.isEmpty()){
            selectedDates = mutableListOf(Calendar.getInstance())
            daysThisMonthList = mutableListOf(Calendar.getInstance())
        }else{
            selectedDates = dates

            selectedDates.forEach {
                if(it.get(Calendar.MONTH).equals(thisMonth)){
                    tempListDaysThisMonth.add(it)
                }
            }

            if(tempListDaysThisMonth.isNotEmpty()){
                daysThisMonthList = tempListDaysThisMonth.distinct() as MutableList<Calendar>
            }else{
                daysThisMonthList = mutableListOf()
            }

        }

        numOfDays = selectedDates.size

        return Observable.fromCallable { ConfirmDates(selectedDates, DateUtils.getSelectedDaysNumber(selectedDates)) }
    }

    override fun resetCalendar() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeData(firstName: String, lastName: String, excuse: Boolean) {

        if(excuse){
            selectedDates.forEach {
                daysWithExcuseList.add(it)
            }
        }else{
            selectedDates.forEach {
                daysWithoutExcuseList.add(it)
            }
        }

        val formattedAllSelectedDates= formatDates(selectedDates)
        val formattedDaysThisMonth = formatDates(daysThisMonthList)
        val formattedDaysWithExcuse = formatDates(daysWithExcuseList)
        val formattedDaysWithoutExcuse = formatDates(daysWithoutExcuseList)

        employer = Employer(firstName, lastName, excuse,
            formattedAllSelectedDates, numOfDays,
            formattedDaysThisMonth, daysThisMonthList.size,
            formattedDaysWithExcuse, daysWithExcuseList.size,
            formattedDaysWithoutExcuse, daysWithoutExcuseList.size)

        FirebaseRepository.readAllData()
            .subscribe ({
                writeCorrectDataToFirebase(it.first, it.second, firstName, lastName)
            },{error->
                view.showErrorMessage()
                view.showProgressBar(true)
            })
    }

    private fun writeCorrectDataToFirebase(listOfEmployers: List<Employer>, listOfKeys: List<String>, firstName: String, lastName: String){
        var doesExist = true
        if(listOfEmployers.isNotEmpty()){
            var count = 0
            for(data in listOfEmployers) {
                if (checkIfEmployerExists(firstName, lastName, data.firstName!!, data.lastName!!)){
                    updateEmployer(data, selectedDates, daysThisMonthList, daysWithExcuseList, daysWithoutExcuseList, listOfKeys[count])
                    doesExist = true
                    break
                }else{
                    doesExist = false
                    count += 1
                }

            }
            if(!doesExist){
                addNewEmployer(employer)
            }

        }else{
            addNewEmployer(employer)
        }
    }

    private fun checkIfEmployerExists(firstName: String, lastName: String, firstNameFirebase: String, lastNameFirebase: String): Boolean{
        return firstName.equals(firstNameFirebase) && lastName.equals(lastNameFirebase)
    }

    private fun updateEmployer(updatedEmployer: Employer, selectedDays: MutableList<Calendar>,
                       daysThisMonth: MutableList<Calendar>, daysWithExcuse: MutableList<Calendar>,
                       daysWithoutExcuse: MutableList<Calendar>, databaseKey: String){
        val updatedEmployerObj = Employer(updatedEmployer.firstName, updatedEmployer.lastName, updatedEmployer.excuse,
            updateEmployerDates(updatedEmployer.selectedDays, selectedDays), updateEmployerDates(updatedEmployer.selectedDays, selectedDays).size,
            updateEmployerDates(updatedEmployer.daysThisMonthList, daysThisMonth), updateEmployerDates(updatedEmployer.daysThisMonthList, daysThisMonth).size,
            updateEmployerDates(updatedEmployer.daysWithExcuseList, daysWithExcuse), updateEmployerDates(updatedEmployer.daysWithExcuseList, daysWithExcuse).size,
            updateEmployerDates(updatedEmployer.daysWithoutExcuseList, daysWithoutExcuse), updateEmployerDates(updatedEmployer.daysWithoutExcuseList, daysWithoutExcuse).size)

        mFirebaseDatabaseRef.child("Employer").child(databaseKey).setValue(updatedEmployerObj)
            .addOnCompleteListener {
                    task: Task<Void> ->
                if (task.isSuccessful){
                    view.showSuccessfulUpdateMessage()
                    view.showProgressBar(false)
                }else{
                    view.showErrorMessage()
                }
            }
    }

    private fun addNewEmployer(employer: Employer){
        mFirebaseDatabaseRef.child("Employer").push().setValue(employer)
            .addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    view.showSuccessfulMessage()
                    view.showProgressBar(false)
                } else {
                    view.showErrorMessage()
                    view.showProgressBar(true)
                }
            }
    }

    private fun updateEmployerDates(firebaseListDates: MutableList<String>, datesList: MutableList<Calendar>): MutableList<String>{
        val tempSelectedDays: MutableList<String> = mutableListOf()

        if(firebaseListDates.isNotEmpty()){
            tempSelectedDays.addAll(firebaseListDates)
            tempSelectedDays.addAll(formatDates(datesList))
        }else{
            tempSelectedDays.addAll(formatDates(datesList))
        }

        return tempSelectedDays.distinct() as MutableList<String>
    }

    override fun resetDatesForNewMonth() {
        FirebaseRepository.resetDatesForNewMonth()
    }

    override fun attach(view: HomeContract.View) {
        this.view = view
    }

    override fun destroy(){
        compositeDisposable.dispose()
    }

}