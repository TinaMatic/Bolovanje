package com.example.bolovanje.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.bolovanje.SickLeaveApplication
import com.example.bolovanje.R
import com.example.bolovanje.ui.main.MainActivity
import com.example.bolovanje.utils.DateUtils
import com.example.bolovanje.view.DateDialog
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import javax.inject.Inject

class HomeFragment : Fragment(), HomeContract.View {

    private var datePicker: DateDialog? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var dates: MutableList<Calendar> = mutableListOf()
    var previousDates : MutableList<Calendar> = mutableListOf(Calendar.getInstance())
    private lateinit var firstName: String
    private lateinit var lastName: String
    private var excuse: Boolean = false
    val now = Calendar.getInstance()
    val today: MutableList<Calendar> = mutableListOf(now)

    @Inject
    lateinit var presenter : HomeContract.Presenter

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
        presenter.resetDatesForNewMonth()


        //set today as default value
        tvDate.text = DateUtils.getSelectedDaysNumber(today)

        ivCalendar.setOnClickListener {
            showCalendar(dates)
        }

        compositeDisposable.add(btnSubmit.clicks().subscribe{
            readData()
            (activity as MainActivity).enableBottomNaigation(true)
            resetData()
            btnSubmit.findNavController().navigate(R.id.action_navigation_home_self)
        })

    }

    override fun onResume() {
        super.onResume()
        presenter.resetDatesForNewMonth()
    }

    override fun onAttach(context: Context) {
        (activity?.application as SickLeaveApplication).getSickLeaveComponent()
            .inject(this)
        super.onAttach(context)
    }


    override fun showCalendar(date: MutableList<Calendar>) {
        datePicker = DateDialog(activity!!,R.style.DialogTheme, date)

        //handle the cancel button
        compositeDisposable.add(datePicker!!.cancelObservable.subscribe {
            hideCalendar()
        })

        //handle the ok button
        compositeDisposable.add(datePicker!!.confirmDateObservable.flatMap {presenter.selectDates(date)}.subscribe {
            hideCalendar()
            tvDate.text = it.dataLabel
            previousDates = it.selectedDays!!

        })

        datePicker!!.show()
    }

    override fun hideCalendar() {
        if(datePicker != null && datePicker!!.isShowing){
            datePicker!!.dismiss()
        }
    }

    override fun showProgressBar(show: Boolean) {
        if (show){
            progressBarHome?.visibility = View.VISIBLE
        }else{
            progressBarHome?.visibility = View.GONE
        }

    }

    override fun readData() {
        (activity as MainActivity).enableBottomNaigation(false)
        firstName = etFirstName.text.toString()
        lastName = etLastName.text.toString()
        excuse = cbExcuse.isChecked

        presenter.writeData(firstName, lastName, excuse)
    }

    override fun showSuccessfulMessage(){
        Toast.makeText(activity, "${firstName} ${lastName}'s bolovanje has successfully been added", Toast.LENGTH_LONG).show()
    }

    override fun showErrorMessage() {
        Toast.makeText(activity, "Something when wrong", Toast.LENGTH_LONG).show()
    }

    override fun showSuccessfulUpdateMessage() {
        Toast.makeText(activity, "${firstName} ${lastName} has been successfully updated", Toast.LENGTH_LONG).show()
    }

    private fun resetData(){
        etFirstName.setText("")
        etLastName.setText("")
        cbExcuse.isChecked = false
        dates = mutableListOf()
        tvDate.text = DateUtils.getSelectedDaysNumber(today)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.destroy()
    }
}