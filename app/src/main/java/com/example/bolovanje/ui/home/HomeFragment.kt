package com.example.bolovanje.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bolovanje.BolovanjeApplication
import com.example.bolovanje.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onAttach(context: Context) {
        (activity?.application as BolovanjeApplication).getBolovanjeComponent()
            .inject(this) // TODO: instead of this line extend DaggerFragment to remove boilerplate code
        super.onAttach(context)
    }
}