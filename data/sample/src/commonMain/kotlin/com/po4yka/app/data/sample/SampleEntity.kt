package com.po4yka.app.data.sample

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sample_items")
data class SampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
)
