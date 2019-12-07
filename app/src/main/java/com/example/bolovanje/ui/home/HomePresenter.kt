package com.example.bolovanje.ui.home

import android.util.Log
import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.model.Employer
import com.example.bolovanje.model.FirebaseRepository
import com.example.bolovanje.utils.DateUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.format.DateTimeFormatterBuilder
import java.util.*
import javax.inject.Inject

class HomePresenter: HomeContract.Presenter {

    private lateinit var view : HomeContract.View

    var selectedDates = mutableListOf<Calendar>(Calendar.getInstance())
    var numOfDays: Int = 1
    var daysThisMonthList = mutableListOf<Calendar>(Calendar.getInstance())
    var daysWithExcuseList : MutableList<Calendar> = mutableListOf()
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

            if(!tempListDaysThisMonth.isEmpty()){
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

    override fun formatDates(dates: MutableList<Calendar>): MutableList<String> {
        val formattedAllSelectedDates : MutableList<String> = mutableListOf()

        //format selected days so they are all in formatt dd.mm
        compositeDisposable.add(Observable.fromCallable { dates }
            .flatMapIterable {
                it
            }.subscribe {
                formattedAllSelectedDates.add(DateUtils.getFormattedDate(it.timeInMillis, DateTimeFormatterBuilder().appendPattern(
                    DateUtils.DATE_FORMAT
                ).toFormatter()))
            })

        return formattedAllSelectedDates
    }

    override fun writeData(firstName: String, lastName: String, excuse: Boolean) {
        val databaseRef = mFirebaseDatabaseRef.child("Employer")
        var doesExist = true

        if(excuse){
            selectedDates.forEach {
                daysWithExcuseList.add(it)
            }
        }

        val formattedAllSelectedDates= formatDates(selectedDates)
        val formattedDaysThisMonth = formatDates(daysThisMonthList)
        val formattedDaysWithExcuse = formatDates(daysWithExcuseList)

        employer = Employer(firstName, lastName, excuse,  formattedAllSelectedDates,
            numOfDays, formattedDaysThisMonth, daysThisMonthList.size, formattedDaysWithExcuse, daysWithExcuseList.size)

        compositeDisposable.add(FirebaseRepository.readData()
            .subscribeOn(Schedulers.io())
            .map {
                if (it.first.isNotEmpty()){
                    Pair(it.first as ArrayList<Employer>, it.second as ArrayList<String>)
                }else{
                    Pair(arrayListOf(), arrayListOf())
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                writeCorrectDataToFirebase(it.first, it.second, firstName, lastName)
            },{error->
                view.showErrorMessage()
                view.showProgressBar(true)
            }))

        //need to check if the employer already exists
//        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//                if (dataSnapshot.value != null) {
//                    val orderSnapshot = dataSnapshot.children
//                    for (data in orderSnapshot) {
//                        val dateKey = data.key
//                        val firstNameFirebase = data.child("firstName").value.toString().trim()
//                        val lastNameFirebase = data.child("lastName").value.toString().trim()
//                        val selectedDayesListFirebase = mutableListOf<String>()
//                        val dayesThisMonthListFirebase = mutableListOf<String>()
//                        val daysWithExcuseListFirebase = mutableListOf<String>()
////                        val selectedDaysFirebase = data.child("selectedDays").value.toString()
////                        val daysThisMonthFirebase = data.child("daysThisMonthList").value.toString()
////                        val daysWithExcuseFirebase = data.child("daysWithExcuseList").value.toString()
//
//                        data.child("selectedDays").apply {
//                            if (hasChildren()){
//                                children.forEach {
//                                    selectedDayesListFirebase.add(it.value.toString())
//                                }
//                            }
//                        }
//
//                        data.child("daysThisMonthList").apply {
//                            if (hasChildren()){
//                                children.forEach {
//                                    dayesThisMonthListFirebase.add(it.value.toString())
//                                }
//                            }
//                        }
//
//                        data.child("daysWithExcuseList").apply {
//                            if (hasChildren()){
//                                children.forEach {
//                                    daysWithExcuseListFirebase.add(it.value.toString())
//                                }
//                            }
//                        }
//
//                        if (firstName.equals(firstNameFirebase) && lastName.equals(lastNameFirebase)) {
//                            //if it does add the seleceted dates and overwrite the number
//                            employer = Employer(firstNameFirebase, lastNameFirebase, excuse,
//                                updateEmployerDates(selectedDayesListFirebase, selectedDates), updateEmployerDates(selectedDayesListFirebase, selectedDates).size,
//                                updateEmployerDates(dayesThisMonthListFirebase, daysThisMonthList), updateEmployerDates(dayesThisMonthListFirebase, daysThisMonthList).size,
//                                updateEmployerDates(daysWithExcuseListFirebase, daysWithExcuseList), updateEmployerDates(daysWithExcuseListFirebase, daysWithExcuseList).size)
//
//                            mFirebaseDatabaseRef!!.child("Employer").child(dateKey!!).setValue(employer)
//                                .addOnCompleteListener {
//                                task: Task<Void> ->
//                                if (task.isSuccessful){
//                                    view?.showSuccessfulUpdateMessage()
//                                    view?.showProgressBar(false)
//                                }else{
//                                    view?.showErrorMessage()
//                                }
//                            }
//                            doesExist = true
//                            break
//                        } else {
//                            doesExist = false
//                        }
//                    }
//
//                    if (!doesExist){
//                        addNewEmployer(employer)
//                    }
//                } else {
//                    databaseRef.push().setValue(employer)
//                        .addOnCompleteListener { task: Task<Void> ->
//                            if (task.isSuccessful) {
//                                view?.showSuccessfulMessage()
//                                view?.showProgressBar(false)
//                            } else {
//                                view?.showErrorMessage()
//                            }
//                        }
//                }
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                view?.showProgressBar(true)
//            }
//
//        })

    }

    private fun writeCorrectDataToFirebase(listOfEmployers: List<Employer>, listOfKeys: List<String>, firstName: String, lastName: String){
        var doesExist = true
        if(listOfEmployers.isNotEmpty()){
            var count = 0
            for(data in listOfEmployers) {
                if (checkIfEmployerExists(firstName, lastName, data.firstName!!, data.lastName!!)){
                    updateEmployer(data, selectedDates, daysThisMonthList, daysWithExcuseList, listOfKeys[count])
                    doesExist = true
                    break
                }else{
                    doesExist = false
                }
                count += 1
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
                       databaseKey: String){
        val updatedEmployerObj = Employer(updatedEmployer.firstName, updatedEmployer.lastName, updatedEmployer.excuse,
            updateEmployerDates(updatedEmployer.selectedDays, selectedDays), updateEmployerDates(updatedEmployer.selectedDays, selectedDays).size,
            updateEmployerDates(updatedEmployer.daysThisMonthList, daysThisMonth), updateEmployerDates(updatedEmployer.daysThisMonthList, daysThisMonth).size,
            updateEmployerDates(updatedEmployer.daysWithExcuseList, daysWithExcuse), updateEmployerDates(updatedEmployer.daysWithExcuseList, daysWithExcuse).size)

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
//        if (firebaseListDates != "null" || datesList.size != 0){
//            if(firebaseListDates != "null"){
//                var newList = firebaseListDates.drop(1).dropLast(1).split(", ").toMutableList()
//                tempSelectedDays.addAll(newList)
//                tempSelectedDays.addAll(formatDates(datesList))
//            }else{
//                tempSelectedDays.addAll(formatDates(datesList))
//            }
//
//        }

        return tempSelectedDays
    }

    override fun resetDatesForNewMonth() {
        var thisMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        mFirebaseDatabaseRef.child("Employer").addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot != null){
                    val orderSnapshot = dataSnapshot.children

                    for (data in orderSnapshot){
                        val listOfDatesThisMonth: MutableList<String> = mutableListOf()
                        val key = data.key
                        val datesThisMonth = data.child("daysThisMonthList").value.toString()
                        val selectedDays = data.child("selectedDays").value.toString()

                        if(datesThisMonth != "null"){
                            var tempListOfDates = datesThisMonth.drop(1).dropLast(1).split(", ").toMutableList()

                            if(tempListOfDates[0].drop(3) != thisMonth.toString()){
                                //reset daysThisMonthList to null and daysThisMonthNum to 0
                                Log.e("das", thisMonth.toString())
                                mFirebaseDatabaseRef.child("Employer").child(key!!).child("daysThisMonthNum").setValue(0)
                                mFirebaseDatabaseRef.child("Employer").child(key!!).child("daysThisMonthList").setValue(null)
                            }
                        }

                        if(selectedDays != "null"){
                            val tempListOfSelectedDates = selectedDays.drop(1).dropLast(1).split(", ").toMutableList()

                            tempListOfSelectedDates.forEach {
                                if(it.drop(3).equals(thisMonth.toString())){
                                    listOfDatesThisMonth.add(it)
                                }
                            }
                        }
                        mFirebaseDatabaseRef.child("Employer").child(key!!).child("daysThisMonthNum").setValue(listOfDatesThisMonth.size)
                        mFirebaseDatabaseRef.child("Employer").child(key!!).child("daysThisMonthList").setValue(listOfDatesThisMonth)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    override fun attach(view: HomeContract.View) {
        this.view = view
    }

    override fun destroy(){
        compositeDisposable.dispose()
    }



}