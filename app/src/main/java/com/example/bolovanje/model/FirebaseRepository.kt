package com.example.bolovanje.model

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.reactivex.Observable
import java.util.*

object FirebaseRepository {
    val mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference

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

                                if(employer.hasChild("selectedDays")){
                                    employer.child("selectedDays").apply {
                                        if (hasChildren()){
                                            children.forEach {
                                                selectedDayesList.add(it.value.toString())
                                            }
                                        }
                                    }
                                }


                                if(employer.hasChild("daysThisMonthList")){
                                    employer.child("daysThisMonthList").apply {
                                        if (hasChildren()){
                                            children.forEach {
                                                dayesThisMonthList.add(it.value.toString())
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


                                val tempEmployer = Employer(firstName, lastName, excuse,
                                    selectedDayesList, numOfDays.toInt(),
                                    dayesThisMonthList, numOfDaysThisMonth.toInt(),
                                    daysWithExcuseList, numOfDaysWithExcuse.toInt())

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
                        val selectedDayesList = mutableListOf<String>()
                        val dayesThisMonthList = mutableListOf<String>()
                        val daysWithExcuseList = mutableListOf<String>()

                        val firstName = dataSnapshot.child("firstName").value.toString().trim()
                        val lastName = dataSnapshot.child("lastName").value.toString().trim()
                        val excuse = dataSnapshot.child("excuse").value as Boolean
                        val numOfDays = dataSnapshot.child("numOfDays").value.toString().trim()
                        val numOfDaysThisMonth = dataSnapshot.child("daysThisMonthNum").value.toString().trim()
                        val numOfDaysWithExcuse = dataSnapshot.child("daysWithExcuseNum").value.toString()

                        if(dataSnapshot.hasChild("selectedDays")){
                            dataSnapshot.child("selectedDays").apply {
                                if (hasChildren()){
                                    children.forEach {
                                        selectedDayesList.add(it.value.toString())
                                    }
                                }
                            }
                        }


                        if(dataSnapshot.hasChild("daysThisMonthList")){
                            dataSnapshot.child("daysThisMonthList").apply {
                                if (hasChildren()){
                                    children.forEach {
                                        dayesThisMonthList.add(it.value.toString())
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


                        val tempEmployer = Employer(firstName, lastName, excuse,
                            selectedDayesList, numOfDays.toInt(),
                            dayesThisMonthList, numOfDaysThisMonth.toInt(),
                            daysWithExcuseList, numOfDaysWithExcuse.toInt())

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
                val employer = Employer(firstName, lastName, false,
                    it.selectedDays, it.numOfDays,
                    it.daysThisMonthList, it.daysThisMonthNum,
                    it.daysWithExcuseList, it.daysWithExcuseNum)

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

    fun addDaysWithExcuse(key: String, selectedDays: MutableList<Calendar>, datesThisMonthList: MutableList<Calendar>): Observable<Employer>{

        return Observable.create<Employer> {emitter ->
            var employerObj :Employer

            readDataForOneEmployer(key).subscribe {employer->
                employer.daysWithExcuseList.addAll(DatesRepository.formatDates(selectedDays))
                employer.selectedDays.addAll(DatesRepository.formatDates(selectedDays))
                employer.daysThisMonthList.addAll(DatesRepository.formatDates(datesThisMonthList))

                mFirebaseDatabaseRef.child("Employer").child(key).child("daysWithExcuseList").setValue(employer.daysWithExcuseList.distinct())
                mFirebaseDatabaseRef.child("Employer").child(key).child("daysWithExcuseNum").setValue(employer.daysWithExcuseList.distinct().size)

                mFirebaseDatabaseRef.child("Employer").child(key).child("selectedDays").setValue(employer.selectedDays.distinct())
                mFirebaseDatabaseRef.child("Employer").child(key).child("numOfDays").setValue(employer.selectedDays.distinct().size)

                mFirebaseDatabaseRef.child("Employer").child(key).child("daysThisMonthList").setValue(employer.daysThisMonthList.distinct())
                mFirebaseDatabaseRef.child("Employer").child(key).child("daysThisMonthNum").setValue(employer.daysThisMonthList.distinct().size)

                employerObj = Employer(employer.firstName, employer.lastName, true,
                    employer.selectedDays.distinct() as MutableList<String>, employer.selectedDays.distinct().size,
                    employer.daysThisMonthList.distinct() as MutableList<String>, employer.daysThisMonthList.distinct().size,
                    employer.daysWithExcuseList.distinct() as MutableList<String>, employer.daysWithExcuseList.distinct().size)

                    emitter.onNext(employerObj)
            }
        }
    }

    fun resetDatesForNewMonth() {
        val thisMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        mFirebaseDatabaseRef.child("Employer")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot != null) {
                        val orderSnapshot = dataSnapshot.children

                        for (data in orderSnapshot) {
                            val listOfDatesThisMonth: MutableList<String> = mutableListOf()
                            val key = data.key
                            val datesThisMonth = data.child("daysThisMonthList").value.toString()
                            val selectedDays = data.child("selectedDays").value.toString()

                            if (datesThisMonth != "null") {
                                val tempListOfDates =
                                    datesThisMonth.drop(1).dropLast(1).split(", ").toMutableList()

                                if (tempListOfDates[0].drop(3) != thisMonth.toString()) {
                                    //reset daysThisMonthList to null and daysThisMonthNum to 0
                                    Log.e("das", thisMonth.toString())
                                    mFirebaseDatabaseRef.child("Employer").child(key!!)
                                        .child("daysThisMonthNum").setValue(0)
                                    mFirebaseDatabaseRef.child("Employer").child(key!!)
                                        .child("daysThisMonthList").setValue(null)
                                }
                            }

                            if (selectedDays != "null") {
                                val tempListOfSelectedDates =
                                    selectedDays.drop(1).dropLast(1).split(", ").toMutableList()

                                tempListOfSelectedDates.forEach {
                                    if (it.drop(3).equals(thisMonth.toString())) {
                                        listOfDatesThisMonth.add(it)
                                    }
                                }
                            }
                            mFirebaseDatabaseRef.child("Employer").child(key!!)
                                .child("daysThisMonthNum").setValue(listOfDatesThisMonth.size)
                            mFirebaseDatabaseRef.child("Employer").child(key!!)
                                .child("daysThisMonthList").setValue(listOfDatesThisMonth)
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
    }
}

