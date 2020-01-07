package com.example.bolovanje.ui.search

import java.util.*

interface OnSearchItemClickListener {
    fun onDeleteClick(position: Int)
    fun onEditClick(position: Int, firstName: String, lastName: String, selectedDays: MutableList<Calendar>)
    fun onUpdateDaysWithoutExcuseClick(position: Int, daysWithoutExcuse: MutableList<Calendar>)
    fun onShowClick(selectedDays: MutableList<String>)
}