package com.ojhdtapp.action.logic.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ojhdtapp.action.logic.model.Suggest

@Dao
interface SuggestDao {
    @Insert
    fun insertSuggest(suggest: Suggest): Long

    @Delete
    fun deleteSuggest(suggest: Suggest)

    @Update
    fun updateSuggest(newSuggest: Suggest)

    @Query("SELECT * FROM suggest_table")
    fun loadAllSuggest(): List<Suggest>

    @Query("SELECT * FROM suggest_table WHERE archived = 0 AND read = 0")
    fun loadAllDisplaySuggest(): List<Suggest>

    @Query("SELECT * FROM suggest_table WHERE archived = 1")
    fun loadAllArchivedSuggest(): List<Suggest>

    @Query("SELECT * FROM suggest_table WHERE read = 1")
    fun loadAllReadSuggest(): List<Suggest>

    @Query("SELECT * FROM suggest_table")
    fun loadAllSuggestLive(): LiveData<List<Suggest>>

    @Query("SELECT * FROM suggest_table WHERE archived = 0 AND read = 0")
    fun loadAllDisplaySuggestLive(): LiveData<List<Suggest>>

    @Query("SELECT * FROM suggest_table WHERE archived = 1")
    fun loadAllArchivedSuggestLive(): LiveData<List<Suggest>>

    @Query("SELECT * FROM suggest_table WHERE read = 1")
    fun loadAllReadSuggestLive(): LiveData<List<Suggest>>

    @Query("DELETE FROM action_table WHERE id = :id")
    fun deleteSuggestById(id: Long): Int

    @Query("SELECT 1 FROM action_table WHERE id = :id LIMIT 1")
    fun isStored(id: Long): Boolean
}