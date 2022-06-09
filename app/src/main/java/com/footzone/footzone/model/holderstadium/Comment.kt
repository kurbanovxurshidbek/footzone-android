package com.footzone.footzone.model.holderstadium

data class Comment(
    val commentId: String,
    val createdAt: String,
    val rate: Int,
    val stadiumId: String,
    val text: String,
    val userAttachmentName: String,
    val userFullName: String
)