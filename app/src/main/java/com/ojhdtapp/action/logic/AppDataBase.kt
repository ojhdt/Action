package com.ojhdtapp.action.logic

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.logic.dao.AchievementDao
import com.ojhdtapp.action.logic.dao.ActionDao
import com.ojhdtapp.action.logic.dao.SuggestDao
import com.ojhdtapp.action.logic.model.Achievement
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest
import com.ojhdtapp.action.logic.network.Converters

@Database(version = 1, entities = [Action::class, Suggest::class, Achievement::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
    abstract fun suggestDao(): SuggestDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        private val instance by lazy {
            Room.databaseBuilder(BaseApplication.context, AppDataBase::class.java, "app_database")
                .build()
        }

        fun getDataBase(): AppDataBase {
            return instance
        }
    }
}