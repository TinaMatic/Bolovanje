package com.example.bolovanje.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
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
    private var previousDates : MutableList<Calendar> = mutableListOf()
    private lateinit var firstName: String
    private lateinit var lastName: String
    private var excuse: Boolean = false
    private val now = Calendar.getInstance()
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
            resetData()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

         when(item.itemId){
            R.id.reset-> {
                presenter.resetAllData().subscribe {
                    if(it){
                        Toast.makeText(context, "All data has been successfully deleted", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return true
    }

    override fun showCalendar(calendarDates: MutableList<Calendar>) {
        datePicker = DateDialog(activity!!,R.style.DialogTheme, calendarDates)

        //handle the cancel button
        compositeDisposable.add(datePicker!!.cancelObservable.subscribe {
            hideCalendar()
            if(previousDates.isNotEmpty()){
                dates = previousDates.distinct() as MutableList<Calendar>
            }else{
                dates = mutableListOf()
            }
        })

        //handle the ok button
        compositeDisposable.add(datePicker!!.confirmDateObservable.switchMap {presenter.selectDates(calendarDates)}.subscribe {
            hideCalendar()
            tvDate.text = it.dataLabel

            //reset the previous dates
            previousDates.clear()

            //save only the currently selected dates as previous dates
            previousDates.addAll(it.selectedDays!!)

            //reset the dates to all the selected days
            dates = it.selectedDays
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
        firstName = etFirstName.text.toString().toLowerCase().capitalize()
        lastName = etLastName.text.toString().toLowerCase().capitalize()
        excuse = cbExcuse.isChecked

        presenter.writeData(firstName, lastName, excuse)
    }

    override fun showSuccessfulMessage(){
        Toast.makeText(activity, "${firstName} ${lastName}'s sick leave days have successfully been added", Toast.LENGTH_LONG).show()
        btnSubmit.findNavController().navigate(R.id.action_navigation_home_self)
    }

    override fun showErrorMessage() {
        Toast.makeText(activity, "Something when wrong", Toast.LENGTH_LONG).show()
    }

    override fun showSuccessfulUpdateMessage() {
        Toast.makeText(activity, "${firstName} ${lastName} has been successfully updated", Toast.LENGTH_LONG).show()
        btnSubmit.findNavController().navigate(R.id.action_navigation_home_self)
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