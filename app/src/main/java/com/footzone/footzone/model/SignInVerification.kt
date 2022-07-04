package com.footzone.footzone.model

data class SignInVerification(
    val codeSent: String,
    val deviceName: String,
    val deviceToken: String,
    val deviceType: String,
    val phoneNumber: String
)