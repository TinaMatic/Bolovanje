package com.example.bolovanje.ui.employers

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bolovanje.R
import kotlinx.android.synthetic.main.fragment_employers.*

class EmployersFragment : Fragment() {

    var pagerAdapter: EmployerPageAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_employers, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = EmployerPageAdapter(childFragmentManager)
        viewPagerId.adapter = pagerAdapter

        mainTabs.setupWithViewPager(viewPagerId)
        mainTabs.setTabTextColors(Color.WHITE, Color.rgb(143,186,255))
    }

//    companion object{
//        val TAG: String = EmployersFragment::class.java.simpleName
//    }

}