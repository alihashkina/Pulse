package com.example.pulse.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pulse.R
import com.example.pulse.adapters.dataClass.Card
import com.example.pulse.databinding.CardStatisticsBinding
import java.util.*

class CardAdapter: RecyclerView.Adapter<CardAdapter.CardHolder>() {

    companion object{
        val cardList = ArrayList<Card>()
    }

    class CardHolder(item: View): RecyclerView.ViewHolder(item){

        val binding = CardStatisticsBinding.bind(item)

        fun bind(card: Card){
            binding.cardDate.text = card.cardDate
            binding.cardHealthy.text = card.cardHealthy
            binding.cardUnhealthy.text = card.cardUnhealthy
            binding.cardSymptoms.text = card.cardSymptoms
            binding.cardCare.text = card.cardCare
            binding.cardPulse.text = card.cardPulse
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_statistics, parent, false)
        return CardHolder(view)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun addCard(card: Card){
        cardList.add(card)
        notifyDataSetChanged()
    }
}