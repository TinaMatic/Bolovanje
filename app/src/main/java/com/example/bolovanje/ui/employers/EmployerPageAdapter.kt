package com.example.bolovanje.ui.employers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bolovanje.ui.employers.allEmployers.AllEmployersFragment
import com.example.bolovanje.ui.employers.tenOrMoreDaysEmployers.TenOrMoreDaysEmployersFragment

class EmployerPageAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private val COUNT = 2
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> {
                return AllEmployersFragment()
            }
            1 -> {
                return TenOrMoreDaysEmployersFragment()
            }
        }

        return null!!
    }

    override fun getCount(): Int {
        return COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> {
                return "ALL EMPLOYERS"
            }

            1 -> {
                return "MORE THAN 10 DAYS EMPLOYERS"
            }
        }

        return null!!
    }
}