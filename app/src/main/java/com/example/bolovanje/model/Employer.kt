package com.example.bolovanje.model

import java.util.*
import javax.inject.Inject

class Employer @Inject constructor
    (var firstName: String?, var lastName: String?, var excuse: Boolean,
     var selectedDays: MutableList<String>, var numOfDays: Int,
     var daysThisMonthList: MutableList<String>, var daysThisMonthNum: Int,
     var daysWithExcuseList: MutableList<String>, var daysWithExcuseNum: Int){
}