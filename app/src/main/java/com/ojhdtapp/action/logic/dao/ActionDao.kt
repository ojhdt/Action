package com.ojhdtapp.action.logic.dao

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

    @Query("SELECT * FROM `action_table` WHERE finished = 1")
    fun loadAllFinishedAction(): List<Action>

    @Query("SELECT * FROM `action_table` WHERE finished = 0")
    fun loadAllUnfinishedAction(): List<Action>

    @Query("SELECT * FROM `action_table`")
    fun loadAllActionLive(): LiveData<List<Action>>

    @Query("SELECT * FROM `action_table` WHERE finished = 1")
    fun loadAllFinishedActionLive(): LiveData<List<Action>>

    @Query("SELECT * FROM `action_table` WHERE finished = 0")
    fun loadAllUnfinishedActionLive(): LiveData<List<Action>>

    @Query("DELETE FROM `action_table` WHERE id = :id")
    fun deleteActionById(id: Long): Int

}