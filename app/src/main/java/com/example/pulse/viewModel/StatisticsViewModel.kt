package com.example.pulse.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StatisticsViewModel : ViewModel() {
    companion object{
        var counterSts: MutableLiveData<Boolean> = MutableLiveData(true)
    }
}