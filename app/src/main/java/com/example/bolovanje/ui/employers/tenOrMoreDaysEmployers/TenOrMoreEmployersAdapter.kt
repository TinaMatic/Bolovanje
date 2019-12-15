package com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bolovanje.R
import com.example.bolovanje.model.Employer
import kotlinx.android.synthetic.main.employers_row.view.*

class TenOrMoreEmployersAdapter(private val context: Context, private val employerList: List<Employer>):
    RecyclerView.Adapter<TenOrMoreEmployersAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.employers_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return employerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(employerList[position])
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindItem(employer: Employer){
            itemView.txtEmployers.text = employer.firstName + " " + employer.lastName
            itemView.txtNumberOfDays.text = employer.numOfDays.toString()
            itemView.txtTenOrMoreDays.text = employer.daysThisMonthNum.toString()
            itemView.txtDaysWithExcuse.text = employer.daysWithExcuseNum.toString()
            itemView.txtDaysWithoutExcuse.text = employer.daysWithoutExcuseNum.toString()
        }
    }
}