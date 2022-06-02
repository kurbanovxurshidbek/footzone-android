package com.footzone.footzone.model.profile

data class Data(
    val enabled: Boolean,
    val fullName: String,
    val id: String,
    val phoneNumber: String,
    val photo: Photo,
    val roles: List<Role>
)