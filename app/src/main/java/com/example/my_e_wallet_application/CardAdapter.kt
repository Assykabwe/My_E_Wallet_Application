package com.example.my_e_wallet_application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter now takes a list of Pairs: (documentId, Card object) and a click listener
class CardAdapter(
    private val cards: MutableList<Pair<String, Card>>,
    private val onItemClicked: (cardId: String) -> Unit // Lambda for handling clicks
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val aliasText: TextView = view.findViewById(R.id.cardAliasText)
        val lastFourText: TextView = view.findViewById(R.id.cardLastFourText)
        val typeIcon: ImageView = view.findViewById(R.id.cardTypeIcon)
        // val menuIcon: ImageView = view.findViewById(R.id.cardMenuIcon) // Removing the separate menu icon for simplicity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val (cardId, card) = cards[position]

        // Use the last 4 digits of the cardNumber for the summary view
        val lastFour = card.cardNumber.takeLast(4)

        // Placeholder alias; you might want to store a proper alias field in Firestore
        holder.aliasText.text = "${card.cardHolderName.split(" ").firstOrNull() ?: "Card"} (${card.cardNumber.take(4)})"
        holder.lastFourText.text = "**** **** **** $lastFour"

        // TODO: Logic to set card type icon based on card.cardNumber prefix

        // Set the click listener on the entire card view
        holder.itemView.setOnClickListener {
            onItemClicked(cardId)
        }
    }

    override fun getItemCount() = cards.size
}