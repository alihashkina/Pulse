package com.example.pulse.viewModel

import android.content.ContentValues
import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pulse.MainActivity.Companion.helper
import com.example.pulse.adapters.CardAdapter
import com.example.pulse.adapters.CardAdapter.Companion.deleteCard
import com.example.pulse.adapters.dataClass.Card
import com.example.pulse.db.MyDBHelper
import com.example.pulse.fragment.GeneralPage.Companion.bindingGeneralPage
import com.example.pulse.fragment.GeneralPage.Companion.tinyDB
import com.example.pulse.fragment.Statistics
import kotlin.collections.ArrayList

class GeneralPageViewModel : ViewModel() {

    var counter: MutableLiveData<Int> = MutableLiveData(0)

    companion object{
        var dateDB = ""
        var arrayPulseGraph: ArrayList<Int> = arrayListOf()
        var arrayDateGraph : MutableList<String> = mutableListOf()
    }

    lateinit var cards: Card
    var chipsHDB = ""
    var chipsUhDB = ""
    var chipsSDB = ""
    var chipsCDB = ""
    var chipsODB = ""
    var pulseDB = 60
    var idDB = 0
    var cv = ContentValues()

    fun addDB(context: Context, dateDB: String, pulseDB: Int, chipsHealthyDB: String, chipsUnHealthyDB: String, chipsSymptomsDB: String, chipsCareDB: String, daysDB: Int, monthDB: Int, yearsDB: Int, hoursDB: Int, minuteDB: Int, chipsOtherDB: String, idDB: Int){

        cv.put("DATE", dateDB)
        cv.put("PULSE", pulseDB)
        cv.put("CHIPSHEALTHY", chipsHealthyDB)
        cv.put("CHIPSUNHEALTHY", chipsUnHealthyDB)
        cv.put("CHIPSSYMPTOMS", chipsSymptomsDB)
        cv.put("CHIPSCARE", chipsCareDB)
        cv.put("DAYS", daysDB)
        cv.put("MONTH", monthDB)
        cv.put("YEARS", yearsDB)
        cv.put("HOURS", hoursDB)
        cv.put("MINUTE", minuteDB)
        cv.put("CHIPSOTHER", chipsOtherDB)
        cv.put("ID", idDB)

        MyDBHelper(context).writableDatabase.insert("USERS", null, cv)
    }

    fun readDB(){
        var db = helper.readableDatabase
        var rs = db.rawQuery(
            "SELECT DATE, PULSE, CHIPSHEALTHY, CHIPSUNHEALTHY, CHIPSSYMPTOMS, CHIPSCARE, CHIPSOTHER, DAYS, MONTH, YEARS, HOURS, MINUTE, ID FROM USERS ORDER BY YEARS, MONTH, DAYS, HOURS, MINUTE ASC",
            null
        )

        arrayDateGraph = arrayListOf()
        arrayPulseGraph = arrayListOf()

        if(!deleteCard){
            Statistics.adapter.update()
        }

        while (rs != null && rs.getCount() > 0 && rs.moveToNext()) {
            dateDB = rs.getString(0)
            pulseDB = rs.getString(1).toInt()
            chipsHDB = rs.getString(2).replace(",", " | ")
            chipsUhDB = rs.getString(3).replace(",", " | ")
            chipsSDB = rs.getString(4).replace(",", " | ")
            chipsCDB = rs.getString(5).replace(",", " | ")
            chipsODB = rs.getString(6).replace(",", " | ")
            idDB = rs.getString(12).toInt()

            tinyDB.putString("DateDB", "+")

            arrayDateGraph.add(dateDB)
            arrayPulseGraph.add(pulseDB)

            if (!deleteCard) {
                cards = Card(
                    dateDB,
                    chipsHDB,
                    chipsUhDB,
                    chipsSDB,
                    chipsCDB,
                    pulseDB,
                    chipsODB,
                    idDB
                )
                Statistics.adapter.addCard(cards)
            }

            counter.value?.let {
                counter.value = it + 1
            }
        }

    }

    fun deleteAllDB(){
        helper.writableDatabase.delete("USERS", null, null)
        bindingGeneralPage.scrollGraph.visibility = View.GONE
        bindingGeneralPage.txtOnbord.visibility = View.VISIBLE
        readDB()
    }

    fun deleteCardDB(){
        helper.writableDatabase.delete("USERS", "USERID=${CardAdapter.idDB}", null)
        readDB()
        bindingGeneralPage.deleteCard.callOnClick()
    }
}