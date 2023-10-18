package com.example.pulse.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.pulse.R
import com.example.pulse.adapters.dataClass.Card
import com.example.pulse.databinding.CardStatisticsBinding
import com.example.pulse.fragment.Statistics
import com.example.pulse.viewModel.GeneralPageViewModel
import java.util.*

class CardAdapter: RecyclerView.Adapter<CardAdapter.CardHolder>() {

    companion object{
        var deleteCard = false
        var id = 0
        var idDB = 0
    }

    val cardList = ArrayList<Card>()

    class CardHolder(item: View): RecyclerView.ViewHolder(item){

        val binding = CardStatisticsBinding.bind(item)

        @SuppressLint("NotifyDataSetChanged")
        fun bind(card: Card, index: Int) = with(binding){
            cardDate.text = card.cardDate
            cardHealthy.text = card.cardHealthy?.replace("[", "")?.replace("]", "")
            cardUnhealthy.text = card.cardUnhealthy?.replace("[", "")?.replace("]", "")
            cardSymptoms.text = card.cardSymptoms?.replace("[", "")?.replace("]", "")
            cardCare.text = card.cardCare?.replace("[", "")?.replace("]", "")
            cardPulse.text = card.cardPulse.toString()
            cardOther.text = card.cardOther?.replace("[", "")?.replace("]", "")
            id = card.id

            //кнопка поделиться/удалить
            cardMore.setOnClickListener {
                val popupMenu = PopupMenu(it.context, cardMore)
                var pulse = it.context.getString(R.string.pulse)
                var date = it.context.getString(R.string.date)
                var healthy = it.context.getString(R.string.healthy)
                var txtUnhealthy = it.context.getString(R.string.txtUnhealthy)
                var txtSymptoms = it.context.getString(R.string.txtSymptoms)
                var txtCare = it.context.getString(R.string.txtCare)
                var txtOtherTags = it.context.getString(R.string.txtOtherTags)

                popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->

                    when(item.itemId) {
                        R.id.share ->
                        {
                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND
                            intent.putExtra(Intent.EXTRA_TEXT, "${pulse}: ${cardPulse.text} \n$date: ${cardDate.text} \n$healthy: ${cardHealthy.text} \n$txtUnhealthy: ${cardUnhealthy.text} \n$txtSymptoms: ${cardSymptoms.text} \n$txtCare: ${cardCare.text} \n$txtOtherTags: ${cardOther.text}")
                            intent.type = "text/plain"
                            it.context.startActivity(Intent.createChooser(intent, "Share To:"))
                        }
                        R.id.delete ->
                        {
                            idDB = card.id
                            Statistics.adapter.deleteCard(it.context)
                            Statistics.adapter.updateDelete(index)
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_statistics, parent, false)
        return CardHolder(view)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(cardList[position], position)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    //добавить карточку
    fun addCard(card: Card){
        deleteCard = false
        cardList.add(card)
        notifyDataSetChanged()
    }

    //удалить карочку
    fun deleteCard(context: Context){
        deleteCard = true
        if(cardList.size == 1){
            GeneralPageViewModel().deleteAllDB(context)
        }
        else{
            GeneralPageViewModel().deleteCardDB(context)
        }
    }

    //очистка адаптера
    fun update(){
        cardList.clear()
        notifyDataSetChanged()
    }

    //обновление адаптера после удаления
    fun updateDelete(index: Int){
        cardList.removeAt(index)
        notifyDataSetChanged()
    }
}