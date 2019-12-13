package com.example.bolovanje.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bolovanje.R
import com.example.bolovanje.model.ConfirmDates
import com.example.bolovanje.model.Employer
import com.example.bolovanje.utils.DateUtils
import com.example.bolovanje.view.DateDialog
import com.google.firebase.database.*
import io.reactivex.Observable
import kotlinx.android.synthetic.main.employers_search_row.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SearchAdapter (private val context: Context,
                     var employerList: ArrayList<Employer>,
                     val selectedDates: MutableList<Calendar>,
                     val onSearchItemClickListener: OnSearchItemClickListener):
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    @Inject
    lateinit var presenter: SearchContract.Presenter


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.employers_search_row, parent, false)
        return ViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return employerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(employerList[position], position)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindItem(employer: Employer, position: Int){
            itemView.txtEmployers.text = employer.firstName + " " + employer.lastName
            itemView.txtNumberOfDays.text = employer.numOfDays.toString()
            itemView.txtTenOrMoreDays.text = employer.daysThisMonthNum.toString()
            itemView.txtDaysWithExcuse.text = employer.daysWithExcuseNum.toString()
            itemView.txtDaysThisMonth.text = ""

            var listOfDaysThisMonth: String = " "

            if(employer.daysThisMonthList.isNotEmpty()){
                employer.daysThisMonthList.forEach {
                    listOfDaysThisMonth +=  "$it "
                }
                itemView.txtDaysThisMonth.text = listOfDaysThisMonth.drop(1).dropLast(1)
            }else{
                itemView.txtDaysThisMonth.text = context.getString(R.string.no_sick_leave)
            }

            itemView.btnRemove.setOnClickListener {
                onSearchItemClickListener.onDeleteClick(position)
            }

            itemView.btnEdit.setOnClickListener {
                onSearchItemClickListener.onEditClick(position, employer.firstName!!, employer.lastName!!, employer.selectedDays)
            }

            itemView.txtAddDaysWithExcuse.setOnClickListener{
                onSearchItemClickListener.onAddDaysWithExcuseClick(position)
            }

        }
    }

}