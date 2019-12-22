package com.example.bolovanje.ui.search

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolovanje.SickLeaveApplication
import com.example.bolovanje.R
import com.example.bolovanje.model.Employer
import com.example.bolovanje.view.DateDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.edit_employer_dialog.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.show_all_dates_dialog.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

class SearchFragment : Fragment(), SearchContract.View, OnSearchItemClickListener {

    private var adapter: SearchAdapter? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var datePicker: DateDialog? = null

    private var selectedDatesForDaysWithExcuse = mutableListOf<Calendar>(Calendar.getInstance())
    private var selectedDatesForUpdateEmployer = mutableListOf<Calendar>()
    private var datesThisMonthList = mutableListOf<Calendar>(Calendar.getInstance())

    private var isCanceled = false
    private var showDatesForUpdate = mutableListOf<Calendar>()
    private val previousDates = mutableListOf<Calendar>()

    @Inject
    lateinit var presenter: SearchContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attach(this)
        presenter.resetDatesForNewMonth()

        clNoResultsSearch.findViewById<TextView>(R.id.tvNoResultText)
            .text = getString(R.string.search_employer_no_result)

        val buttonClickStream = createButtonClickSearch()
        val textChangeStream = createTextChangeSearch()
        val searchTextObservable = Observable.merge<String>(buttonClickStream, textChangeStream)

        compositeDisposable.add(searchTextObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                showProgressBar(true)
            }
            .observeOn(Schedulers.io())
            .switchMap { presenter.searchData(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                showProgressBar(false)
                showData(it.first, it.second)
            }, {error->
              showErrorMessage(error = error.toString())
                showProgressBar(true)
            }))
    }

    override fun onResume() {
        super.onResume()
        presenter.resetDatesForNewMonth()
    }

    override fun onAttach(context: Context) {
        (activity?.application as SickLeaveApplication).getSickLeaveComponent()
            .inject(this) // TODO: instead of this line extend DaggerFragment to remove boilerplate code
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        compositeDisposable.clear()
        presenter.destroy()
    }

    override fun showProgressBar(show: Boolean) {
        if(progressBarSearch != null){
            if(show){
                progressBarSearch?.visibility = View.VISIBLE
            }else{
                progressBarSearch?.visibility = View.GONE
            }
        }
    }

    override fun showErrorMessage(error: String) {
        Toast.makeText(context, "Something went wrong " + error, Toast.LENGTH_LONG).show()
    }

    override fun showData(list: List<Employer>, databaseKeyList: List<String>) {
        if(list.isEmpty()){
            recyclerViewSearch?.visibility = View.GONE
            clNoResultsSearch?.visibility = View.VISIBLE

        }else{
            recyclerViewSearch?.visibility = View.VISIBLE
            clNoResultsSearch?.visibility = View.GONE

            if(context != null){
                adapter = SearchAdapter(context!!, ArrayList(list), selectedDatesForDaysWithExcuse, this)
                recyclerViewSearch?.layoutManager = LinearLayoutManager(context)
                recyclerViewSearch?.setHasFixedSize(true)
                recyclerViewSearch?.adapter = adapter
            }

        }

    }

    override fun createButtonClickSearch(): Observable<String> {
        return Observable.create { emitter ->
            btnSearch.setOnClickListener {
                emitter.onNext(etEmployerName.text.toString())
            }

            emitter.setCancellable {
                btnSearch.setOnClickListener(null)
            }
        }
    }

    override fun createTextChangeSearch(): Observable<String> {
        val textChangeObservable = Observable.create<String>{ emitter->
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.toString()?.let {
                        emitter.onNext(it)
                    }
                }

            }
            etEmployerName.addTextChangedListener(textWatcher)

            emitter.setCancellable {
                etEmployerName.removeTextChangedListener(textWatcher)
            }
        }

        return textChangeObservable.filter {
            it.length >= 2
        }.debounce (1000, TimeUnit.MILLISECONDS)
    }

    private fun showDeleteDialog(position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete")
            .setMessage("Are you sure you want to delete this employer")
            .setCancelable(false)
            .setPositiveButton("Yes"){dialog, id ->
                presenter.deleteEmployer(position).subscribe {
                    if(it){
                        Toast.makeText(context, "Employer has been deleted", Toast.LENGTH_LONG).show()
                        adapter?.employerList?.removeAt(position)
                        adapter!!.notifyItemRemoved(position)
                    }else{
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                    }
                }
            }.setNegativeButton("No"){dialog, id ->

            }

        val alert = builder.create()
        alert.show()
    }


    private fun showEditDialog(position: Int, firstName: String, lastName: String, selectedDays: MutableList<Calendar>){
        val view = LayoutInflater.from(context).inflate(R.layout.edit_employer_dialog, null)
        var firstNameValue: String?
        var lastNameValue: String?

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Edit Employer")
            .setCancelable(false)
            .setPositiveButton("Update"){dialog, id ->
                firstNameValue = view.etFirstName.text.toString()
                lastNameValue = view.etLastName.text.toString()

                if (selectedDatesForUpdateEmployer.isNotEmpty()){
                    presenter.editEmployer(position, firstNameValue!!, lastNameValue!!, selectedDatesForUpdateEmployer).subscribe {
                        if(it.first){
                            Toast.makeText(context, "Employer has been successfully updated", Toast.LENGTH_LONG).show()
                            adapter?.employerList!![position] = it.second
                            adapter?.notifyItemChanged(position)
                        }else{
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    presenter.editEmployer(position, firstNameValue!!, lastNameValue!!, selectedDays).subscribe {
                        if(it.first){
                            Toast.makeText(context, "Employer has been successfully updated", Toast.LENGTH_LONG).show()
                            adapter?.employerList!![position] = it.second
                            adapter?.notifyItemChanged(position)
                        }else{
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }.setNegativeButton("Cancel"){dialog, id ->

            }

        val alert = builder.create()
        alert.setView(view)
        alert.show()

        view.etFirstName.setText(firstName)
        view.etLastName.setText(lastName)
        view.ivCalendar.setOnClickListener {
            if(isCanceled){

                presenter.getSelectedDaysForEmployer(position).subscribe {
                    if(previousDates.isNotEmpty()){
                        showDatesForUpdate = previousDates.distinct().toMutableList()
                    }else{
                        showDatesForUpdate = it
                    }
                    showCalendarForUpdatingEmployer(showDatesForUpdate, position)
                }

            }else{
                if(selectedDatesForUpdateEmployer.isEmpty()){
                    showDatesForUpdate = selectedDays
                }else{
                    showDatesForUpdate = selectedDatesForUpdateEmployer
                }
                showCalendarForUpdatingEmployer(showDatesForUpdate, position)
            }
        }
    }

    private fun showAllDatesDialogBox(selectedDays: MutableList<String>){
        val view = LayoutInflater.from(context).inflate(R.layout.show_all_dates_dialog, null)

        val builder = AlertDialog.Builder(context)
        builder.setNeutralButton("OK", null)

        val alert = builder.create()
        alert.setView(view)
        alert.show()

        view.januaryList.text   = presenter.findMonthDates("01", selectedDays)
        view.februaryList.text  = presenter.findMonthDates("02", selectedDays)
        view.marchList.text     = presenter.findMonthDates("03", selectedDays)
        view.aprilList.text     = presenter.findMonthDates("04", selectedDays)
        view.mayList.text       = presenter.findMonthDates("05", selectedDays)
        view.juneList.text      = presenter.findMonthDates("06", selectedDays)
        view.julyList.text      = presenter.findMonthDates("07", selectedDays)
        view.augustList.text    = presenter.findMonthDates("08", selectedDays)
        view.septemberList.text = presenter.findMonthDates("09", selectedDays)
        view.octoberList.text   = presenter.findMonthDates("10", selectedDays)
        view.novemberList.text  = presenter.findMonthDates("11", selectedDays)
        view.decemberList.text  = presenter.findMonthDates("12", selectedDays)

    }

    override fun showCalendarForUpdatingDatesWithothExcuse(calendarDates: MutableList<Calendar>, position: Int) {
        datePicker = DateDialog(activity!!, R.style.DialogTheme, calendarDates)

        //handle the cancel button
        compositeDisposable.add(datePicker!!.cancelObservable.subscribe {
            hideCalendar()
        })

        //handle the ok button
        compositeDisposable.add(datePicker!!.confirmDateObservable.switchMap {presenter.selectDates(calendarDates)}.subscribe {
            selectedDatesForDaysWithExcuse = it.first.selectedDays!!
            datesThisMonthList = it.second
            hideCalendar()

            val updatedEmployer = presenter.updateDaysWithoutExcuse(position)
            updatedEmployer.subscribe {
                adapter?.employerList!![position] = it
                adapter?.notifyItemChanged(position)
            }
        })

        datePicker!!.show()
    }

    override fun showCalendarForUpdatingEmployer(calendarDates: MutableList<Calendar>, position: Int) {
        datePicker = DateDialog(activity!!, R.style.DialogTheme, calendarDates)

        //handle the cancel button
        compositeDisposable.add(datePicker!!.cancelObservable.subscribe {
            hideCalendar()
            isCanceled = true
        })

        //handle the ok button
        compositeDisposable.add(datePicker!!.confirmDateObservable.switchMap {presenter.selectDates(calendarDates)}.subscribe {
            selectedDatesForUpdateEmployer = it.first.selectedDays!!
            hideCalendar()
            previousDates.clear()
            previousDates.addAll(it.first.selectedDays!!)
            isCanceled = false
        })

        datePicker!!.show()
    }

    override fun hideCalendar() {
        if(datePicker != null && datePicker!!.isShowing){
            datePicker!!.dismiss()
        }
    }

    override fun onDeleteClick(position: Int) {
        showDeleteDialog(position)
    }

    override fun onEditClick(position: Int,firstName: String, lastName: String, selectedDays: MutableList<Calendar>) {
        showEditDialog(position, firstName, lastName, selectedDays)
    }

    override fun onUpdateDaysWithoutExcuseClick(position: Int, daysWithoutExcuse: MutableList<Calendar>) {
        showCalendarForUpdatingDatesWithothExcuse(daysWithoutExcuse, position)
    }

    override fun onShowClick(selectedDays: MutableList<String>) {
        showAllDatesDialogBox(selectedDays)
    }
}