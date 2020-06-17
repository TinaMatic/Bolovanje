package com.example.bolovanje.model

import com.example.bolovanje.utils.DateUtils.Companion.convertDatesToCalendarObj
import com.example.bolovanje.utils.DateUtils.Companion.formatDates
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*


object FirebaseRepository {
    private val mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    var compositeDisposable = CompositeDisposable()

    fun readAllData(): Observable<Pair<List<Employer>, List<String>> >{
        return Observable.create {emitter->

            val listOfEmployer = arrayListOf<Employer>()
            val listOfKeys = arrayListOf<String>()

            mFirebaseDatabaseRef.child("Employer").orderByChild("numOfDays")
                .addListenerForSingleValueEvent(object :ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val orderSnapshot = dataSnapshot.children
                        if (dataSnapshot.value != null) {

                            for (employer in orderSnapshot) {
                                val selectedDaysList = mutableListOf<String>()
                                val daysThisMonthList = mutableListOf<String>()
                                val daysWithExcuseList = mutableListOf<String>()
                                val daysWithoutExcuseList = mutableListOf<String>()

                                val databaseKey = employer.key!!
                                val firstName = employer.child("firstName").value.toString().trim()
                                val lastName = employer.child("lastName").value.toString().trim()
                                val excuse = employer.child("excuse").value as Boolean
                                val numOfDays = employer.child("numOfDays").value.toString()
                                val numOfDaysThisMonth = employer.child("daysThisMonthNum").value.toString()
                                val numOfDaysWithExcuse = employer.child("daysWithExcuseNum").value.toString()
                                val numOfDaysWithoutExcuse = employer.child("daysWithoutExcuseNum").value.toString()

                                listOfKeys.add(databaseKey)

                                if(employer.hasChild("selectedDays")){
                                    employer.child("selectedDays").apply {
                                        if (hasChildren()){
                                            children.forEach {
                                                selectedDaysList.add(it.value.toString())
                                            }
                                        }
                                    }
                                }


                                if(employer.hasChild("daysThisMonthList")){
                                    employer.child("daysThisMonthList").apply {
                                        if (hasChildren()){
                                            children.forEach {
                                                daysThisMonthList.add(it.value.toString())
                                            }
                                        }
                                    }
                                }


                                if(employer.hasChild("daysWithExcuseList")){
                                    employer.child("daysWithExcuseList").apply {
                                        if (hasChildren()){
                                            children.forEach {
                                                daysWithExcuseList.add(it.value.toString())
                                            }
                                        }
                                    }
                                }

                                if(employer.hasChild("daysWithoutExcuseList")){
                                    employer.child("daysWithoutExcuseList").apply {
                                        if (hasChildren()){
                                            children.forEach {
                                                daysWithoutExcuseList.add(it.value.toString())
                                            }
                                        }
                                    }
                                }


                                val tempEmployer = Employer(firstName, lastName, excuse,
                                    selectedDaysList, numOfDays.toInt(),
                                    daysThisMonthList, numOfDaysThisMonth.toInt(),
                                    daysWithExcuseList, numOfDaysWithExcuse.toInt(),
                                    daysWithoutExcuseList, numOfDaysWithoutExcuse.toInt())

                                listOfEmployer.add(tempEmployer)
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

    fun readDataForOneEmployer(key: String): Observable<Employer>{
        return Observable.create<Employer> {emitter ->
            mFirebaseDatabaseRef.child("Employer").child(key)
                .addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        //val databaseKey = dataSnapshot.key
                        val selectedDaysList = mutableListOf<String>()
                        val daysThisMonthList = mutableListOf<String>()
                        val daysWithExcuseList = mutableListOf<String>()
                        val daysWithoutExcuseList = mutableListOf<String>()

                        val firstName = dataSnapshot.child("firstName").value.toString().trim()
                        val lastName = dataSnapshot.child("lastName").value.toString().trim()
                        val excuse = dataSnapshot.child("excuse").value as Boolean
                        val numOfDays = dataSnapshot.child("numOfDays").value.toString()
                        val numOfDaysThisMonth = dataSnapshot.child("daysThisMonthNum").value.toString()
                        val numOfDaysWithExcuse = dataSnapshot.child("daysWithExcuseNum").value.toString()
                        val numOfDaysWithoutExcuse = dataSnapshot.child("daysWithoutExcuseNum").value.toString()

                        if(dataSnapshot.hasChild("selectedDays")){
                            dataSnapshot.child("selectedDays").apply {
                                if (hasChildren()){
                                    children.forEach {
                                        selectedDaysList.add(it.value.toString())
                                    }
                                }
                            }
                        }


                        if(dataSnapshot.hasChild("daysThisMonthList")){
                            dataSnapshot.child("daysThisMonthList").apply {
                                if (hasChildren()){
                                    children.forEach {
                                        daysThisMonthList.add(it.value.toString())
                                    }
                                }
                            }
                        }


                        if(dataSnapshot.hasChild("daysWithExcuseList")){
                            dataSnapshot.child("daysWithExcuseList").apply {
                                if (hasChildren()){
                                    children.forEach {
                                        daysWithExcuseList.add(it.value.toString())
                                    }
                                }
                            }
                        }

                        if(dataSnapshot.hasChild("daysWithoutExcuseList")){
                            dataSnapshot.child("daysWithoutExcuseList").apply {
                                if (hasChildren()){
                                    children.forEach {
                                        daysWithoutExcuseList.add(it.value.toString())
                                    }
                                }
                            }
                        }


                        val tempEmployer = Employer(firstName, lastName, excuse,
                            selectedDaysList, numOfDays.toInt(),
                            daysThisMonthList, numOfDaysThisMonth.toInt(),
                            daysWithExcuseList, numOfDaysWithExcuse.toInt(),
                            daysWithoutExcuseList, numOfDaysWithoutExcuse.toInt())

                        emitter.onNext(tempEmployer)
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })

        }
    }

    fun deleteEmployer(key: String): Observable<Boolean> {
        return Observable.create<Boolean> {emitter->
            mFirebaseDatabaseRef.child("Employer").child(key)
                .removeValue().addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    emitter.onNext(true)
                } else {
                    emitter.onNext(false)
                }
            }
        }
    }

    fun editEmployer(key: String, firstName: String, lastName: String, selectedDays: MutableList<Calendar>): Observable<Pair<Boolean, Employer>> {

        return Observable.create<Pair<Boolean, Employer>> {emitter->
            readDataForOneEmployer(key).subscribe {

                var newListOfDaysThisMonth = mutableListOf<String>()
                val newAllSelectedDays = mutableListOf<String>()
                val thisMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

                //add all the selected days to this month list if they belong there
                if(selectedDays.isEmpty()){
                    newListOfDaysThisMonth = formatDates(mutableListOf(Calendar.getInstance()))
                }else{
                    val tempListOfDaysThisMonth = mutableListOf<Calendar>()
                    selectedDays.forEach {
                        if((it.get(Calendar.MONTH)+1).equals(thisMonth)){
                            tempListOfDaysThisMonth.add(it)
                        }
                    }
                    newListOfDaysThisMonth.addAll(formatDates(tempListOfDaysThisMonth))
                }

                //add all the days without excuse to days this month if they belong there
                if(it.daysWithoutExcuseList.isNotEmpty()){
                    it.daysWithoutExcuseList.forEach {
                        if(it.substring(3, 5).toInt().equals(thisMonth)){
                            newListOfDaysThisMonth.add(it)
                        }
                    }
                }

                newAllSelectedDays.addAll(formatDates(selectedDays))
                newAllSelectedDays.addAll(it.daysWithoutExcuseList)

                val employer = Employer(firstName, lastName, false,
                    newAllSelectedDays.distinct().toMutableList(), newAllSelectedDays.distinct().size,
                    newListOfDaysThisMonth.distinct().toMutableList(), newListOfDaysThisMonth.distinct().size,
                    formatDates(selectedDays).distinct().toMutableList(), formatDates(selectedDays).distinct().size,
                    it.daysWithoutExcuseList, it.daysWithoutExcuseList.size)

                mFirebaseDatabaseRef.child("Employer").child(key)
                    .setValue(employer).addOnCompleteListener {task: Task<Void> ->
                        if(task.isSuccessful){
                            emitter.onNext(Pair(true, employer))
                        }else{
                            emitter.onNext(Pair(false, employer))
                        }
                    }
            }
         }
    }

    fun updateDaysWithoutExcuse(key: String, selectedDays: MutableList<Calendar>, datesThisMonthList: MutableList<Calendar>): Observable<Employer>{

        return Observable.create<Employer> {emitter ->
            var employerObj :Employer

            readDataForOneEmployer(key).subscribe {employer->
                val thisMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

                val newDaysWithoutExcuseList = formatDates(selectedDays)

                val newSelectedDaysList = mutableListOf<String>()
                newSelectedDaysList.addAll(employer.daysWithExcuseList)
                newSelectedDaysList.addAll(formatDates(selectedDays))

                val newDaysThisMonthList = mutableListOf<String>()
                newSelectedDaysList.forEach {selectedDay->
                    if(selectedDay.substring(3, 5).toInt().equals(thisMonth)){
                        newDaysThisMonthList.add(selectedDay)
                    }
                }
                employerObj = Employer(employer.firstName, employer.lastName, true,
                    newSelectedDaysList.distinct().toMutableList(), newSelectedDaysList.distinct().size,
                    newDaysThisMonthList.distinct().toMutableList(), newDaysThisMonthList.distinct().size,
                    employer.daysWithExcuseList, employer.daysWithExcuseNum,
                    newDaysWithoutExcuseList.distinct().toMutableList(), newDaysWithoutExcuseList.distinct().size)

                mFirebaseDatabaseRef.child("Employer").child(key).setValue(employerObj)

                emitter.onNext(employerObj)
            }
        }
    }

    fun resetDatesForNewMonth() {
        val thisMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
        val listOfDatesThisMonth: MutableList<String> = mutableListOf()
        var count = 0

        compositeDisposable.add(readAllData().subscribe {(employers, keys)->

            employers.forEach {
                listOfDatesThisMonth.clear()

                if(it.daysThisMonthList.isNotEmpty() && it.daysThisMonthList[0].substring(3, 5).toInt() != thisMonth){
                    //reset daysThisMonthList to null and daysThisMonthNum to 0
                    mFirebaseDatabaseRef.child("Employer").child(keys[count])
                        .child("daysThisMonthNum").setValue(0)
                    mFirebaseDatabaseRef.child("Employer").child(keys[count])
                        .child("daysThisMonthList").setValue(null)
                }

                it.selectedDays.forEach {selectedDay->
                    if(selectedDay.substring(3, 5).toInt().equals(thisMonth)){
                        listOfDatesThisMonth.add(selectedDay)
                    }
                }

                mFirebaseDatabaseRef.child("Employer").child(keys[count])
                    .child("daysThisMonthNum").setValue(listOfDatesThisMonth.size)
                mFirebaseDatabaseRef.child("Employer").child(keys[count])
                    .child("daysThisMonthList").setValue(listOfDatesThisMonth)

                count += 1
            }
        })
    }

    fun getSelectedDaysWithExcuseForEmployer(key: String): Observable<MutableList<Calendar>>{

        return Observable.create {emitter ->
            val tempListOfSelectedDaysWithExcuse = mutableListOf<Calendar>()
            readDataForOneEmployer(key).subscribe {
                convertDatesToCalendarObj(it.daysWithExcuseList).forEach {
                    tempListOfSelectedDaysWithExcuse.add(it)
                }
                emitter.onNext(tempListOfSelectedDaysWithExcuse)
            }
        }
    }

    fun resetAllData(): Observable<Boolean>{
        return Observable.create<Boolean> {emitter ->
            mFirebaseDatabaseRef.child("Employer").removeValue().addOnCompleteListener {task: Task<Void> ->
                if(task.isSuccessful){
                    emitter.onNext(true)
                }else{
                    emitter.onNext(false)
                }
            }
        }
    }

    fun addNewEmployer(employer: Employer): Observable<Boolean>{
        return Observable.create<Boolean>{emitter ->
            mFirebaseDatabaseRef.child("Employer").push().setValue(employer)
                .addOnCompleteListener { task: Task<Void> ->
                    if (task.isSuccessful) {
                        emitter.onNext(true)
                    } else {
                        emitter.onNext(false)
                    }
                }
        }
    }
}

