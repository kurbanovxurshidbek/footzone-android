package com.footzone.footzone.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavouriteStadiums")
data class FavouriteStadium(
    @PrimaryKey
    val id: String,
    val name: String
)
