package com.footzone.footzone.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.footzone.footzone.model.FavouriteStadium

@Dao
interface FavouriteStadiumDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addToFavouriteStadiums(favouriteStadium: FavouriteStadium)

    @Query("SELECT * FROM FavouriteStadiums")
    suspend fun getFavouriteStadiumsDB(): List<FavouriteStadium>
}