package com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolovanje.SickLeaveApplication

import com.example.bolovanje.R
import com.example.bolovanje.model.Employer
import kotlinx.android.synthetic.main.fragment_ten_or_more_days_employers.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class TenOrMoreDaysEmployersFragment : Fragment(), TenOrMoreDaysEmployersContract.View {

    private lateinit var adapter: TenOrMoreEmployersAdapter

    @Inject
    lateinit var presenter: TenOrMoreDaysEmployersContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ten_or_more_days_employers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attach(this)
        presenter.resetDatesForNewMonth()
        presenter.loadData()

        clNoResultsTenOrMoreDaysEmployer.findViewById<TextView>(R.id.tvNoResultText)
            .text = getString(R.string.ten_or_more_employers_no_data_text)

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

    override fun showProgressBar(show: Boolean) {
        if(progressBarTenOrMoreEmployers != null){
            if(show){
                progressBarTenOrMoreEmployers?.visibility = View.VISIBLE
            }else{
                progressBarTenOrMoreEmployers?.visibility = View.GONE
            }
        }
    }

    override fun showErrorMessage() {
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
    }

    override fun showData(list: List<Employer>) {
        if(list.isEmpty()){
            tenOrMoreEmployersRecyclerViewId?.visibility = View.GONE
            clNoResultsTenOrMoreDaysEmployer?.visibility = View.VISIBLE
        }else{
            tenOrMoreEmployersRecyclerViewId?.visibility = View.VISIBLE
            clNoResultsTenOrMoreDaysEmployer?.visibility = View.GONE

            if(context != null){
                adapter = TenOrMoreEmployersAdapter(context!!, list)
                tenOrMoreEmployersRecyclerViewId?.layoutManager = LinearLayoutManager(context)
                tenOrMoreEmployersRecyclerViewId?.adapter = adapter
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.destroy()
    }
}
