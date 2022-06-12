package com.footzone.footzone.model

import okhttp3.MultipartBody

data class EditStadiumPhotoRequest(
    val photoId: String,
    val file: MultipartBody.Part? = null,
    val changed: Boolean
)