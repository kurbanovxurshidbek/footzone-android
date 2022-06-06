package com.footzone.footzone.model.playhistory

data class PlayHistoryResponse(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
)