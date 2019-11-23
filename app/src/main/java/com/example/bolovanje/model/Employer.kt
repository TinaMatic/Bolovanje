package com.example.bolovanje.model

import java.util.*
import javax.inject.Inject

class Employer @Inject constructor (val firstName: String?, val lastName: String?, val excuse: Boolean, val selectedDays: MutableList<String>, val numOfDays: Int){
}