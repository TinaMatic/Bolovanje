package com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bolovanje.R
import com.example.bolovanje.model.Employer

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
        var employerName = itemView.findViewById<TextView>(R.id.txtEmployers)
        var numOfAllDays = itemView.findViewById<TextView>(R.id.txtNumberOfDays)
        var numOfDaysThisMonth = itemView.findViewById<TextView>(R.id.txtTenOrMoreDays)
        var numOfDaysWithExcuse = itemView.findViewById<TextView>(R.id.txtDaysWithExcuse)

        fun bindItem(employer: Employer){
            employerName.text = employer.firstName + " " + employer.lastName
            numOfAllDays.text = employer.numOfDays.toString()
            numOfDaysThisMonth.text = employer.daysThisMonthNum.toString()
            numOfDaysWithExcuse.text = employer.daysWithExcuseNum.toString()
        }
    }
}