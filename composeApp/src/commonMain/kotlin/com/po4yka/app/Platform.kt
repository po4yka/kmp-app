package com.po4yka.app

import androidx.room.RoomDatabase
import com.po4yka.app.data.local.AppDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
