package com.example.bolovanje.utils

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.format.DateTimeFormatterBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DateUtils {
    companion object{

        const val DATE_FORMAT = "dd.MM.yyyy"
        var compositeDisposable = CompositeDisposable()

        fun getBusinessDaysForMonth(currentDate: Calendar, days: MutableList<Calendar>): List<Calendar>{
            currentDate.set(Calendar.DAY_OF_MONTH, 1)
            val month = currentDate.get(Calendar.MONTH)

            removeAllDaysForMonth(month, days)

            //go through all days in month
            while(month == currentDate.get(Calendar.MONTH)){
                val date = Calendar.getInstance()
                date.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH))
                date.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                date.set(Calendar.YEAR, currentDate.get(Calendar.YEAR))

                if(date.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                    days.add(date)
                }

                currentDate.add(Calendar.DAY_OF_MONTH, 1)
            }

            return days
        }

        fun removeAllDaysForMonth(month: Int, days: MutableList<Calendar>){
            days.removeAll {
                it.get(Calendar.MONTH) == month
            }
        }

        fun getSelectedDaysNumber(selectedDays: List<Calendar>): String{
            return when(selectedDays.size){
                1->{
                    val now = Calendar.getInstance()
                    val tomorrow = Calendar.getInstance()
                    tomorrow.add(Calendar.DAY_OF_YEAR, 1)
                    val yesterday = Calendar.getInstance()
                    yesterday.add(Calendar.DAY_OF_YEAR, -1)
                    val selectedDay = selectedDays.first()

                    if(selectedDay.get(Calendar.DAY_OF_YEAR).equals(now.get(Calendar.DAY_OF_YEAR)) &&
                        selectedDay.get(Calendar.YEAR).equals(now.get(Calendar.YEAR))){
                        return "Today"
                    }else if(selectedDay.get(Calendar.DAY_OF_YEAR).equals(tomorrow.get(Calendar.DAY_OF_YEAR)) &&
                        selectedDay.get(Calendar.YEAR).equals(tomorrow.get(Calendar.YEAR))){
                        return "Tomorrow"
                    }else if (selectedDay.get(Calendar.DAY_OF_YEAR).equals(yesterday.get(Calendar.DAY_OF_YEAR)) &&
                        selectedDay.get(Calendar.YEAR).equals(yesterday.get(Calendar.YEAR))){
                        return "Yesterday"
                    }else{
                        getFormattedDate(selectedDay.timeInMillis,
                            DateTimeFormatterBuilder().appendPattern(DATE_FORMAT).toFormatter())
                    }

                }else ->{
                    val label = StringBuilder(selectedDays.size.toString())
                    label.append(" ")
                    label.append("Days")
                    label.toString()
                }
            }

        }

        fun getFormattedDate(timeInMillis: Long, customFormater: org.threeten.bp.format.DateTimeFormatter): String{
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis

            val zonedDateTime: org.threeten.bp.ZonedDateTime = DateTimeUtils.toZonedDateTime(calendar)

            return zonedDateTime.format(customFormater)
        }

        fun formatDates(dates: MutableList<Calendar>): MutableList<String> {
            val formattedAllSelectedDates : MutableList<String> = mutableListOf()

            //format selected days so they are all in format dd.mm
            compositeDisposable.add(Observable.fromCallable { dates }
                .flatMapIterable {
                    it
                }.subscribe {
                    formattedAllSelectedDates.add(
                        getFormattedDate(it.timeInMillis, DateTimeFormatterBuilder().appendPattern(DATE_FORMAT).toFormatter()))
                })

            return formattedAllSelectedDates
        }

        fun convertDatesToCalendarObj(dates: MutableList<String>): MutableList<Calendar>{
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            var dateObj: Date
            val calendarList: ArrayList<Calendar> = ArrayList()

            dates.forEach {
                val cal = Calendar.getInstance()
                dateObj = sdf.parse(it)!!
                cal.time = dateObj
                calendarList.add(cal)
            }

            return calendarList
        }

    }
}