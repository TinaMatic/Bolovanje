package com.example.bolovanje.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import com.example.bolovanje.R
import com.example.bolovanje.utils.DateUtils.Companion.getBusinessDaysForMonth
import com.example.bolovanje.utils.DateUtils.Companion.removeAllDaysForMonth
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.rxkotlin.flatMapIterable
import kotlinx.android.synthetic.main.calendar.*
import kotlinx.android.synthetic.main.calendar.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.DateFormat
import java.util.*



class DateDialog (context: Context, theme: Int, private var selectedDays: MutableList<Calendar>): AppCompatDialog(context, theme) {

    lateinit var confirmDateObservable: Observable<MutableList<Calendar>>
    lateinit var cancelObservable: Observable<Unit>
    var view: View

    init {
        setCancelable(false)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.calendar, null)

        initDialog(view, selectedDays)
        addClickEvents(view)
        setContentView(view)
    }

    private fun addClickEvents(rootView: View){

        //select all business days for a month
        rootView.btnBusinessDays.setOnClickListener {
            val selectedDays = getBusinessDaysForMonth(calSelectedDate.currentPageDate, selectedDays)
            calSelectedDate.selectedDates = selectedDays
            calSelectedDate.setHighlightedDays(selectedDays)
        }

        //deselect all days for a month
        rootView.btnNone.setOnClickListener {
            removeAllDaysForMonth(calSelectedDate.currentPageDate.get(Calendar.MONTH), selectedDays)
            calSelectedDate.selectedDates = selectedDays
            calSelectedDate.setHighlightedDays(selectedDays)
        }

        //handle single days selection
        rootView.calSelectedDate.setOnDayClickListener {
            if(!selectedDays.contains(it.calendar)){
                selectedDays.add(it.calendar)
            }else{
                selectedDays.remove(it.calendar)
                calSelectedDate.setHighlightedDays(selectedDays)
            }
        }

        //handle cancel click
        cancelObservable = rootView.btnCancel.clicks()

        //handle ok button click
        confirmDateObservable = rootView.btnSet.clicks()
            .switchMap {Observable.fromCallable { selectedDays }}
    }

    private fun initDialog(rootView: View, days: MutableList<Calendar>){
        if(days.isEmpty()){
            days.add(Calendar.getInstance())
        }

        rootView.calSelectedDate.selectedDates = days
    }
}