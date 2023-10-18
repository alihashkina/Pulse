package com.example.pulse.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pulse.R
import com.example.pulse.adapters.CardAdapter
import com.example.pulse.databinding.StatisticsFragmentBinding
import com.example.pulse.viewModel.StatisticsViewModel
import androidx.lifecycle.Observer
import com.example.pulse.viewModel.StatisticsViewModel.Companion.counterSts

class Statistics : Fragment() {

    companion object {
        fun newInstance() = Statistics()
        val adapter = CardAdapter()
    }

    val viewModel: StatisticsViewModel by viewModels()
    lateinit var bindingStatistics: StatisticsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingStatistics = DataBindingUtil.inflate(inflater, R.layout.statistics_fragment,container,false)
        return bindingStatistics.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //заполнение статистики
        bindingStatistics.apply {
            recyclerStatistics.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            recyclerStatistics.adapter = adapter

            counterSts.observe(viewLifecycleOwner, Observer{
                if(counterSts.value == false){
                    txtOnbord.visibility = View.GONE
                }else{
                    txtOnbord.visibility = View.VISIBLE
                }
            })
        }
    }
}