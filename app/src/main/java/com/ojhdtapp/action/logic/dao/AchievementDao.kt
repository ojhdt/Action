package com.ojhdtapp.action.logic.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ojhdtapp.action.logic.model.Achievement

@Dao
interface AchievementDao {
    @Insert
    fun insertAchievement(achievement: Achievement): Long

    @Delete
    fun deleteAchievement(achievement: Achievement)

    @Update
    fun updateAchievement(newAchievement: Achievement)

    @Query("SELECT * FROM achievement_table")
    fun loadAllAchievement(): List<Achievement>

    @Query("SELECT * FROM achievement_table")
    fun loadAllAchievementLive(): LiveData<List<Achievement>>
}