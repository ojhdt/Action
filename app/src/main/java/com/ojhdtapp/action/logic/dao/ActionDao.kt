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

    @Query("SELECT * FROM `action_table` WHERE isActivating = 1")
    fun loadAllActivatingAction(): List<Action>

    @Query("SELECT * FROM `action_table`")
    fun loadAllActionLive(): LiveData<List<Action>>

    @Query("SELECT * FROM `action_table` WHERE isActivating = 1")
    fun loadAllActivatingActionLive(): LiveData<List<Action>>

    @Query("DELETE FROM `action_table` WHERE id = :id")
    fun deleteActionById(id: Long): Int

}