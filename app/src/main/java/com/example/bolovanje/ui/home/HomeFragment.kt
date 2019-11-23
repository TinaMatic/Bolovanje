package com.example.bolovanje.ui.home

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bolovanje.BolovanjeApplication
import com.example.bolovanje.R
import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.utils.DateUtils
import com.example.bolovanje.view.DateDialog
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import javax.inject.Inject

class HomeFragment : Fragment(), HomeContract.View {

    private var datePicker: DateDialog? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var dates: MutableList<Calendar> = mutableListOf()

    private lateinit var firstName: String
    private lateinit var lastName: String
    private var excuse: Boolean = false

    @Inject
    lateinit var presenter : HomeContract.Presenter

//    @Inject lateinit var confirmDatesObject: ConfirmDates
//    var confirmDatesObject: ConfirmDates = ConfirmDates(dates, dates.size.toString())

    var confirmDates: PublishSubject<ConfirmDates> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attach(this)

        //set today as default value
        val now = Calendar.getInstance()
        val today: MutableList<Calendar> = mutableListOf(now)
        tvDate.text = DateUtils.getSelectedDaysNumber(today)

        ivCalendar.setOnClickListener {
            showCalendar(dates)
        }

        compositeDisposable.add(btnSubmit.clicks().subscribe{
            readData()
            etFirstName.setText("")
            etLastName.setText("")
            cbExcuse.isChecked = false

        })

    }

    override fun onAttach(context: Context) {
        (activity?.application as BolovanjeApplication).getBolovanjeComponent()
            .inject(this) // TODO: instead of this line extend DaggerFragment to remove boilerplate code
        super.onAttach(context)
    }

    override fun showCalendar(date: MutableList<Calendar>) {
        datePicker = DateDialog(activity!!,R.style.DialogTheme, date)

        //handle the cancel button
        compositeDisposable.add(datePicker!!.cancelObservable.subscribe { hideCalendar() })

        //handle the ok button
        compositeDisposable.add(datePicker!!.confirmDateObservable.switchMap {presenter.selectDates(date)}.subscribe {
//            confirmDates.onNext(it)
            hideCalendar()
//            Log.i("Confirm date selected days ", it.selectedDays.toString())
            tvDate.text = it.dataLabel

        })

        datePicker!!.show()
    }

    override fun hideCalendar() {
        if(datePicker != null && datePicker!!.isShowing){
            datePicker!!.dismiss()
//            dates.clear()
        }
    }

    override fun showProgressBar(show: Boolean) {
        if (show){
            progressBarHome.visibility = View.VISIBLE
        }else{
            progressBarHome.visibility = View.GONE
        }

    }

    override fun readData() {
        firstName = etFirstName.text.toString()
        lastName = etLastName.text.toString()
        excuse = cbExcuse.isChecked

        presenter.writeData(firstName, lastName, excuse)
//        btnSubmit.clicks().subscribe(publishFromContract)
//        publishFromContract.map{}.subscribe()

    }

    override fun showSuccessfulMessage(){
        Toast.makeText(activity, "${firstName} ${lastName}'s bolovanje has successfully been added", Toast.LENGTH_LONG).show()
    }

    override fun showErrorMessage() {
        Toast.makeText(activity, "Something when wrong", Toast.LENGTH_LONG).show()
    }

//    override fun submitData() {
//        compositeDisposable.add(btnSubmit.clicks().subscribe { presenter.loadData() })
//    }


    override fun onDestroyView() {
        super.onDestroyView()

        //should be from presenter
        compositeDisposable.clear()
        presenter.destroy()
    }

}