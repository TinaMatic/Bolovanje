package com.example.bolovanje.model

import com.example.bolovanje.utils.DateUtils
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*

object DatesRepository {

    var compositeDisposable = CompositeDisposable()

    fun formatDates(dates: MutableList<Calendar>): MutableList<String> {
        val formattedAllSelectedDates : MutableList<String> = mutableListOf()

        //format selected days so they are all in format dd.mm
        compositeDisposable.add(Observable.fromCallable { dates }
            .flatMapIterable {
                it
            }.subscribe {
                formattedAllSelectedDates.add(
                    DateUtils.getFormattedDate(it.timeInMillis, org.threeten.bp.format.DateTimeFormatterBuilder().appendPattern(
                        DateUtils.DATE_FORMAT
                    ).toFormatter()))
            })

        return formattedAllSelectedDates
    }
}