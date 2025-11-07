package com.example.my_e_wallet_application

// Data class to map to a Firestore 'users' document
data class User(
    val fullName: String = "",
    val email: String = ""
    // Add other user fields here, e.g., phoneNumber, address
)