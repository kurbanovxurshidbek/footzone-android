package com.footzone.footzone.model

data class SignInVerification(
    val codeSent: Int,
    val deviceName: String,
    val deviceToken: String,
    val deviceType: String,
    val phoneNumber: String
)