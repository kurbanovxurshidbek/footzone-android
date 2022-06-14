package com.footzone.footzone.model

data class LogInResponse(val message: String, val success: Boolean, val data: UserProperty)

data class UserProperty(val user_id: String, val token: String, val stadiumHolder: Boolean)