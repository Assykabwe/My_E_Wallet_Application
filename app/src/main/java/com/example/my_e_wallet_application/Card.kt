package com.example.my_e_wallet_application

data class Card(
    val cardHolderName: String = "",
    val cardNumber: String = "",
    val expiryDate: String = "",
    val cvv: String=""

)