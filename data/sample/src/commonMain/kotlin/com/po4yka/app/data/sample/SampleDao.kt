package com.po4yka.app.data.sample

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SampleDao {
    @Insert
    suspend fun insert(item: SampleEntity)

    @Query("SELECT * FROM sample_items ORDER BY id DESC")
    fun getAll(): Flow<List<SampleEntity>>

    @Query("SELECT * FROM sample_items WHERE id = :id")
    suspend fun getById(id: Long): SampleEntity?

    @Query("DELETE FROM sample_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
