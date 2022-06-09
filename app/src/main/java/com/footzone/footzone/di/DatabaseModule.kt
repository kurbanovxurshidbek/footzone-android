package com.footzone.footzone.di

import android.content.Context
import androidx.room.Room
import com.footzone.footzone.database.FavouriteStadiumDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun getDatabase(@ApplicationContext context: Context): FavouriteStadiumDatabase =
        Room.databaseBuilder(context, FavouriteStadiumDatabase::class.java, "booking").build()

    @Provides
    @Singleton
    fun getBasketDao(database: FavouriteStadiumDatabase) = database.favouriteStadiumDao()
}