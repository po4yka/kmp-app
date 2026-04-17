---
name: kmp-entity
description: Add a Room entity + DAO to a :data:<domain> module and register it in AppDatabase. Use when adding database tables or persistence.
argument-hint: [EntityName] [domain]
---

# Add Room Entity

Add a new Room entity **$ARGUMENTS** to the appropriate `:data:<domain>` module and wire it into `:composeApp/…/AppDatabase.kt`.

## Decide where it goes

- **Extending an existing domain** (e.g., adding `UserPreferenceEntity` to `:data:user`): drop the new files next to the existing entities — skip to step 3.
- **New business domain** (e.g., `:data:catalog`): follow all steps from 1.

## Steps

### 1. (New domain only) Create `data/<domain>/build.gradle.kts`

```kotlin
plugins {
    id("kmp-app.kmp-data")
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.data.<domain>"
    }
}
```

### 2. (New domain only) Register the module in `settings.gradle.kts`

```kotlin
include(":data:<domain>")
```

### 3. Entity — `data/<domain>/src/commonMain/kotlin/com/po4yka/app/data/<domain>/<Entity>Entity.kt`

```kotlin
package com.po4yka.app.data.<domain>

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "<entity_snake_case>")
data class <Entity>Entity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // TODO: Add fields
)
```

### 4. DAO — `data/<domain>/src/commonMain/kotlin/com/po4yka/app/data/<domain>/<Entity>Dao.kt`

```kotlin
package com.po4yka.app.data.<domain>

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface <Entity>Dao {
    @Insert
    suspend fun insert(item: <Entity>Entity)

    @Query("SELECT * FROM <entity_snake_case> ORDER BY id DESC")
    fun getAll(): Flow<List<<Entity>Entity>>

    @Query("SELECT * FROM <entity_snake_case> WHERE id = :id")
    suspend fun getById(id: Long): <Entity>Entity?

    @Query("DELETE FROM <entity_snake_case> WHERE id = :id")
    suspend fun deleteById(id: Long)
}
```

All DAO functions must be `suspend` or return `Flow` (iOS linker rule — see AGENTS.md).

### 5. (New domain only) Add the project dep to `:composeApp`

In `composeApp/build.gradle.kts`:

```kotlin
implementation(project(":data:<domain>"))
```

### 6. Register in `:composeApp`'s `AppDatabase`

Edit `composeApp/src/commonMain/kotlin/com/po4yka/app/data/local/AppDatabase.kt`:

1. Import the new entity and DAO:
   ```kotlin
   import com.po4yka.app.data.<domain>.<Entity>Entity
   import com.po4yka.app.data.<domain>.<Entity>Dao
   ```
2. Add entity to `@Database(entities = [..., <Entity>Entity::class])`.
3. Increment `version` if this is not the initial schema.
4. Add accessor: `abstract fun <entity>Dao(): <Entity>Dao`.

### 7. Bind the DAO in `:composeApp`'s Koin graph

Edit `composeApp/src/commonMain/kotlin/com/po4yka/app/di/AppModule.kt`. Inside the private `databaseModule`:

```kotlin
single<<Entity>Dao> { get<AppDatabase>().<entity>Dao() }
```

Features inject the DAO via constructor. If the feature is new, add the relevant `implementation(project(":data:<domain>"))` line to its `:impl` build file.

### 8. Verify

Run the `kmp-build` skill, or:

```bash
./gradlew detekt
./gradlew androidApp:assembleDebug
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
./gradlew composeApp:allTests
```

Room schema is exported to `composeApp/schemas/` on successful build.
