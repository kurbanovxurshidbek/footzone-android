package com.footzone.footzone.model

data class CommentsData (
    val message: String,
    val success: Boolean,
    val data: Data
)

data class Data (
    val commentInfo: List<Comment>,
    val allComments: List<AllComment>
)

data class AllComment (
    val text: String,
    val stadiumID: String,
    val rate: Long,
    val userAttachmentName: String,
    val userFullName: String,
    val createdAt: String,
    val commentID: String
)
