package com.example.bolovanje.model

import com.example.bolovanje.utils.DateUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*

object FirebaseRepository {
    val mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    var searchList: MutableList<Employer> = mutableListOf()
    var listOfDatabseKeys: MutableList<String> = mutableListOf()
    var compositeDisposable = CompositeDisposable()

    fun readData(): Observable<Pair<List<Employer>, List<String>> >{
        return Observable.create {emitter->

            val listOfEmployer = arrayListOf<Employer>()
            val listOfKeys = arrayListOf<String>()
            mFirebaseDatabaseRef.child("Employer").orderByChild("numOfDays")
                .addListenerForSingleValueEvent(object :ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val orderSnapshot = dataSnapshot.children
                        if (dataSnapshot.value != null) {

                            for (employer in orderSnapshot) {
                                val selectedDayesList = mutableListOf<String>()
                                val dayesThisMonthList = mutableListOf<String>()
                                val daysWithExcuseList = mutableListOf<String>()

                                val databaseKey = employer.key!!
                                val firstName = employer.child("firstName").value.toString().trim()
                                val lastName = employer.child("lastName").value.toString().trim()
                                val excuse = employer.child("excuse").value as Boolean
                                val numOfDays = employer.child("numOfDays").value.toString().trim()
                                val numOfDaysThisMonth = employer.child("daysThisMonthNum").value.toString().trim()
                                val numOfDaysWithExcuse = employer.child("daysWithExcuseNum").value.toString()

                                listOfKeys.add(databaseKey)

                                employer.child("selectedDays").apply {
                                    if (hasChildren()){
                                        children.forEach {
                                            selectedDayesList.add(it.value.toString())
                                        }
                                    }
                                }

                                employer.child("daysThisMonthList").apply {
                                    if (hasChildren()){
                                        children.forEach {
                                            dayesThisMonthList.add(it.value.toString())
                                        }
                                    }
                                }

                                employer.child("daysWithExcuseList").apply {
                                    if (hasChildren()){
                                        children.forEach {
                                            daysWithExcuseList.add(it.value.toString())
                                        }
                                    }
                                }

                                val tempEmployer = Employer(firstName, lastName, excuse,
                                    selectedDayesList, numOfDays.toInt(),
                                    dayesThisMonthList, numOfDaysThisMonth.toInt(),
                                    daysWithExcuseList, numOfDaysWithExcuse.toInt())

                                listOfEmployer.add(tempEmployer)
                            }

                            listOfEmployer.sortByDescending {
                                it.numOfDays
                            }

                            emitter.onNext(Pair(listOfEmployer, listOfKeys))
                        } else {

                            emitter.onNext(Pair(emptyList(), emptyList()))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
            })
        }

    }

    fun searchData(searchData: String): Observable<List<Employer>> {
        return Observable.create<List<Employer>> { emitter ->

            searchList.clear()
            listOfDatabseKeys.clear()
            val databaseReference = mFirebaseDatabaseRef.child("Employer").orderByChild("numOfDays")

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val orderSnapshot = dataSnapshot.children
                    if (dataSnapshot.value != null) {

                        for (employer in orderSnapshot) {
                            val datesThisMonthList = mutableListOf<String>()
                            val daysWithExcuseList = mutableListOf<String>()

                            val databaseKey = employer.key
                            val firstName = employer.child("firstName").value.toString().trim()
                            val lastName = employer.child("lastName").value.toString().trim()
                            val excuse = employer.child("excuse").value as Boolean
                            val numOfDays = employer.child("numOfDays").value.toString().trim()
                            val selectedDaysFirebase = employer.child("selectedDays").value.toString()
                            val numOfDaysThisMonth = employer.child("daysThisMonthNum").value.toString().trim()

                            employer.child("daysThisMonthList").apply {
                                if (hasChildren()){
                                    children.forEach {
                                        datesThisMonthList.add(it.value.toString())
                                    }
                                }
                            }

                            val numOfDaysWithExcuse = employer.child("daysWithExcuseNum").value.toString()
                            employer.child("daysWithExcuseList").children.forEach {
                                daysWithExcuseList.add(it.value.toString())
                            }

                            //convert the values of the list into mutableList
//                            val datesThisMonthList = daysThisMonthListFirebase.drop(1).dropLast(1).split(", ").toMutableList()
                            val selectedDayesList = selectedDaysFirebase.drop(1).dropLast(1).split(", ").toMutableList()
//                            val daysWithExcuseList = daysWithExcuseListFirebase.drop(1).dropLast(1).split(", ").toMutableList()

                            val employer = Employer(firstName, lastName, excuse,
                                selectedDayesList.distinct() as MutableList<String>, numOfDays.toInt(),
                                datesThisMonthList, numOfDaysThisMonth.toInt(),
                                daysWithExcuseList, numOfDaysWithExcuse.toInt())

                            if (firstName.contains(searchData, true) || lastName.contains(searchData, true)) {
                                searchList.add(employer)
                                listOfDatabseKeys.add(databaseKey!!)
                            }

                        }
                        emitter.onNext(searchList)
                    } else {

                        emitter.onNext(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }


    }

    fun deleteEmployer(position: Int): Observable<Boolean> {
        return Observable.create<Boolean> {emitter->
            mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position])
                .removeValue().addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    emitter.onNext(true)
                } else {
                    emitter.onNext(false)
                }
            }
        }
    }

    fun editEmployer(position: Int, firstName: String, lastName: String, selectedDays: MutableList<Calendar>): Observable<Pair<Boolean, Employer>> {
        return Observable.create<Pair< Boolean, Employer>> { emitter->
            mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position])
                .addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val databaseKey = dataSnapshot.key
                        val daysThisMonthFirebase = dataSnapshot.child("daysThisMonthList").value.toString()
                        val daysThisMonthNum = dataSnapshot.child("daysThisMonthNum").value.toString()
                        val daysWithExcuseFirebase = dataSnapshot.child("daysWithExcuseList").value.toString()
                        val daysWithExcuseNum = dataSnapshot.child("daysWithExcuseNum").value.toString()

                        val daysThisMonth = daysThisMonthFirebase.drop(1).dropLast(1).split(", ").toMutableList()
                        val daysWithExcuse = daysWithExcuseFirebase.drop(1).dropLast(1).split(", ").toMutableList()

                        val employer = Employer(firstName, lastName, false, formatDates(selectedDays), formatDates(selectedDays).size,
                            daysThisMonth as MutableList<String>, daysThisMonthNum.toInt(),
                            daysWithExcuse as MutableList<String>, daysWithExcuseNum.toInt())

                        mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position])
                            .setValue(employer).addOnCompleteListener {task: Task<Void> ->
                            if(task.isSuccessful){
                                emitter.onNext(Pair(true, employer))
                            }else{
                                emitter.onNext(Pair(false, employer))
                            }
                        }


                    }

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
        }
    }

    fun addDaysWithExcuse(position: Int, selectedDays: MutableList<Calendar>, datesThisMonthList: MutableList<Calendar>): Observable<Employer>{

        return Observable.create<Employer> {emitter->
            var employer: Employer
            var addDaysWithExcuseList: MutableList<String> = mutableListOf()
            var tempListOfDaysThisMonth: MutableList<String>


            mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position])
                .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val databaseKey = dataSnapshot.key
                    val firstName = dataSnapshot.child("firstName").value.toString()
                    val lastName = dataSnapshot.child("lastName").value.toString()
                    val excuse = true
                    val daysWithExcuseListFirebase = dataSnapshot.child("daysWithExcuseList").value.toString()
                    val selectedDaysFirebase = dataSnapshot.child("selectedDays").value.toString()
                    val daysThisMonthFirebase = dataSnapshot.child("daysThisMonthList").value.toString()

                    //check if there are dates with excuse in database
                    if (daysWithExcuseListFirebase != "null"){
                        addDaysWithExcuseList = daysWithExcuseListFirebase.drop(1).dropLast(1).split(", ").toMutableList()

                        //also add the new selected daytes
                        addDaysWithExcuseList.addAll(formatDates(selectedDays))
                    }else{
                        addDaysWithExcuseList.addAll(formatDates(selectedDays))
                    }

                    //get all the selected dates from database
                    val tempListOfSelectedDays = selectedDaysFirebase.drop(1).dropLast(1).split(", ").toMutableList()
                    //all the dates with excuse have to be in selected dates as well

                    //add the selected days
                    tempListOfSelectedDays.addAll(formatDates(selectedDays))

                    //check if there are any dates from this month
                    if(daysThisMonthFirebase != "null"){
                        tempListOfDaysThisMonth = daysThisMonthFirebase.drop(1).dropLast(1).split(", ").toMutableList()

                        formatDates(datesThisMonthList).forEach {
                            tempListOfDaysThisMonth.add(it)
                        }
                    }else{
                        tempListOfDaysThisMonth = formatDates(datesThisMonthList)
                    }

                    mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position]).child("daysWithExcuseList").setValue(addDaysWithExcuseList.distinct())
                    mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position]).child("daysWithExcuseNum").setValue(addDaysWithExcuseList.distinct().size)

                    mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position]).child("selectedDays").setValue(tempListOfSelectedDays.distinct())
                    mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position]).child("numOfDays").setValue(tempListOfSelectedDays.distinct().size)

                    mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position]).child("daysThisMonthList").setValue(tempListOfDaysThisMonth.distinct())
                    mFirebaseDatabaseRef.child("Employer").child(listOfDatabseKeys[position]).child("daysThisMonthNum").setValue(tempListOfDaysThisMonth.distinct().size)

                    employer = Employer(firstName, lastName, excuse,
                        tempListOfSelectedDays.distinct() as MutableList<String>, tempListOfSelectedDays.distinct().size,
                        tempListOfDaysThisMonth.distinct() as MutableList<String>, tempListOfSelectedDays.distinct().size,
                        addDaysWithExcuseList.distinct() as MutableList<String>, addDaysWithExcuseList.distinct().size)

                    emitter.onNext(employer)

                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }) }


//        return employer
    }


    fun formatDates(dates: MutableList<Calendar>): MutableList<String> {
        val formattedAllSelectedDates : MutableList<String> = mutableListOf()

        //format selected days so they are all in format dd.mm
        compositeDisposable.add(Observable.fromCallable { dates }
            .flatMapIterable {
                it
            }.subscribe {
                formattedAllSelectedDates.add(DateUtils.getFormattedDate(it.timeInMillis, org.threeten.bp.format.DateTimeFormatterBuilder().appendPattern(
                    DateUtils.DATE_FORMAT
                ).toFormatter()))
            })

        return formattedAllSelectedDates
    }
}

