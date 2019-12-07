package com.example.bolovanje.ui.search

import java.util.*

interface OnSearchItemClickListener {
    fun onDeleteClick(position: Int)
    fun onEditClick(position: Int, firstName: String, lastName: String, selectedDays: MutableList<String>)
    fun onAddDaysWithExcuseClick(position: Int)
//    fun addDaysWithExcuse(position: Int, selectedDays: MutableList<Calendar>)
//    fun showCalendar(date: MutableList<Calendar>)
}