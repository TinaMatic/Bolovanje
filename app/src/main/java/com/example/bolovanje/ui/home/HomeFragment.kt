package com.example.bolovanje.ui.home

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
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
import com.example.bolovanje.view.DateDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import javax.inject.Inject

class HomeFragment : Fragment(), HomeContract.View {

    private var datePicker: DateDialog? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var dates: MutableList<Calendar> = mutableListOf()
    @Inject lateinit var presenter : HomeContract.Presenter

    val confirmDates: PublishSubject<ConfirmDates> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivCalendar.setOnClickListener {
            showCalendar(dates)
        }
    }

    override fun onAttach(context: Context) {
        (activity?.application as BolovanjeApplication).getBolovanjeComponent()
            .inject(this) // TODO: instead of this line extend DaggerFragment to remove boilerplate code
        super.onAttach(context)
    }

    override fun showCalendar(dates: MutableList<Calendar>) {
        datePicker = DateDialog(activity!!, dates)

        //handle the cancel button
        compositeDisposable.add(datePicker!!.cancelObservable.subscribe { hideCalendar() })

        //handle the ok button
//        compositeDisposable.add(datePicker!!.confirmDateObservable.map { ConfirmDates() })

        datePicker!!.show()
    }

    override fun hideCalendar() {
        if(datePicker != null && datePicker!!.isShowing){
            datePicker!!.dismiss()
        }
    }

    override fun showProgressBar() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}