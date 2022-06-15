package com.footzone.footzone.model

data class CommentsData (
    val message: String,
    val success: Boolean,
    val data: Data
)

data class Data (
    val commentInfo: List<CommentInfo>,
    val allComments: List<AllComment>
)

data class AllComment (
    val text: String,
    val rate: Long,
    val stadiumID: String,
    val createdAt: String,
    val commentID: String,
    val userAttachmentName: String,
    val userFullName: String
)

data class CommentInfo (
    val rate: String,
    val number: Long
)
