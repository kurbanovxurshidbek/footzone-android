package com.footzone.footzone.model

data class CommentsData (
    val message: String,
    val success: Boolean,
    val data: Data
)

data class Data (
    val countAllComments: List<CountAllComment>,
    val commentInfo: List<CommentInfo>
)

data class CommentInfo (
    val text: String,
    val stadiumID: String,
    val rate: Long,
    val createdAt: String,
    val userAttachmentName: String,
    val commentID: String,
    val userFullName: String
)

data class CountAllComment (
    val number: Long,
    val rate: String
)
