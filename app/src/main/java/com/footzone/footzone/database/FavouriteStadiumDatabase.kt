package com.footzone.footzone.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.footzone.footzone.database.dao.FavouriteStadiumDao
import com.footzone.footzone.model.FavouriteStadium

@Database(entities = [FavouriteStadium::class], version = 1)
abstract class FavouriteStadiumDatabase : RoomDatabase() {

    abstract fun favouriteStadiumDao(): FavouriteStadiumDao

}