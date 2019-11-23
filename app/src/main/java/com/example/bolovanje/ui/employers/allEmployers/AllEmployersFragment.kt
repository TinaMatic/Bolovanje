package com.example.bolovanje.ui.employers.allEmployers


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolovanje.BolovanjeApplication

import com.example.bolovanje.R
import com.example.bolovanje.model.Employer
import kotlinx.android.synthetic.main.fragment_all_employers.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class AllEmployersFragment : Fragment(), AllEmployersContract.View {

    @Inject
    lateinit var presenter: AllEmployersContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_employers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attach(this)
        presenter.loadData()

        clNoResults.findViewById<TextView>(R.id.tvNoResultText)
            .text = getString(R.string.employer_no_data_text)
//        initView()
    }

    override fun onAttach(context: Context) {
        (activity?.application as BolovanjeApplication).getBolovanjeComponent()
            .inject(this) // TODO: instead of this line extend DaggerFragment to remove boilerplate code
        super.onAttach(context)
    }


    override fun showProgressBar(show: Boolean) {
        if(progressBarAllEmployers != null){
            if(show){
                progressBarAllEmployers.visibility = View.VISIBLE
            }else{
                progressBarAllEmployers.visibility = View.GONE
            }
        }

    }

    override fun showErrorMessage(error: String) {
        Toast.makeText(context, "Something went wrong " + error, Toast.LENGTH_LONG).show()
    }

    override fun showData(list: MutableList<Employer>) {

        if(list.isEmpty()){
            allEmployersRecyclerViewId.visibility = View.GONE
            clNoResults.visibility = View.VISIBLE
        }else{
            allEmployersRecyclerViewId.visibility = View.VISIBLE
            clNoResults.visibility = View.GONE

            var adapter = AllEmployersAdapter(context!!, list)
            allEmployersRecyclerViewId.layoutManager = LinearLayoutManager(context)
            allEmployersRecyclerViewId.adapter = adapter
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()

        presenter.destroy()
    }

}
