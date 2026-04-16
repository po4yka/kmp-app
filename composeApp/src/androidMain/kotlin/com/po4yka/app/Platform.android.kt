package com.po4yka.app

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.po4yka.app.data.local.AppDatabase

lateinit var appContext: Application

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = appContext.getDatabasePath("app.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
