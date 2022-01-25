package com.ojhdtapp.action.logic.dao

import android.hardware.lights.LightState
import androidx.lifecycle.LiveData
import androidx.room.*
import com.ojhdtapp.action.logic.model.Action

@Dao
interface ActionDao {
    @Insert
    fun insertAction(action: Action): Long

    @Delete
    fun deleteAction(action: Action)

    @Update
    fun updateAction(newAction: Action)

    @Query("SELECT * FROM `action_table`")
    fun loadAllAction(): List<Action>

    @Query("SELECT * FROM `action_table` WHERE history IS NOT NULL AND history != ''")
    fun loadActivatedAction(): List<Action>

    @Query("SELECT * FROM `action_table` WHERE isActivating = 1")
    fun loadAllActivatingAction(): List<Action>

    @Query("SELECT * FROM `action_table`")
    fun loadAllActionLive(): LiveData<List<Action>>

    @Query("SELECT * FROM `action_table` WHERE history IS NOT NULL AND history != ''")
    fun loadActivatedActionLive(): LiveData<List<Action>>

    @Query("SELECT * FROM `action_table` WHERE isActivating = 1")
    fun loadAllActivatingActionLive(): LiveData<List<Action>>

    @Query("DELETE FROM `action_table` WHERE id = :id")
    fun deleteActionById(id: Long): Int


    @Query("SELECT * FROM action_table WHERE isActivating = 0 AND (:currentTime - lastTriggered) >= 43200000  AND activityStateTrigger = :activityStateTrigger AND lightStateTrigger = :lightStateTrigger AND locationStateTrigger =:locationStateTrigger AND timeStateTrigger = :timeStateTrigger AND weatherStateTrigger = :weatherStateTrigger ORDER BY weight DESC")
    fun loadAvailableActionByConditions(
        activityStateTrigger: Int = -1,
        lightStateTrigger: Int = -1,
        locationStateTrigger: Int = -1,
        timeStateTrigger: Int = -1,
        weatherStateTrigger: Int = -1,
        currentTime: Long = System.currentTimeMillis()
    ): List<Action>

    @Query("SELECT 1 FROM action_table WHERE objectId = :objectId LIMIT 1")
    fun isStored(objectId: String): Boolean
}