package com.footzone.footzone.model

data class RegisterResponse(val message: String, val success: Boolean, val data: UserPriority)

data class UserPriority(val user_id: String, val token: String)
