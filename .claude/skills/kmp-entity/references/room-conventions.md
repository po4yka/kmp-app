# KMP Room Entity Conventions

## Where entities and DAOs live

Every entity + DAO lives in a `:data:<domain>` module (e.g., `:data:sample`). The `@Database` itself lives in `:composeApp` so Room KSP only runs there once across the graph.

```
:data:sample/src/commonMain/kotlin/com/po4yka/app/data/sample/
├── SampleEntity.kt
└── SampleDao.kt
```

## Table Naming

Use `snake_case` for table names (e.g., `sample_items`).

## Entity

`<Entity>Entity` class with `@Entity(tableName = "…")`:

```kotlin
package com.po4yka.app.data.sample

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sample_items")
data class SampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
)
```

- `@PrimaryKey(autoGenerate = true)` with `Long`, default `0`.
- Data module applies `kmp-app.kmp-data` — it does not enable explicit API mode, so default visibility is fine for entity fields. Data classes are `public` by default.

## DAO

`<Entity>Dao` interface with `@Dao`:

```kotlin
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
```

## DAO Function Rules

- Every DAO function is `suspend` or returns `Flow` — blocking DAO calls fail the iOS linker.
- Functions returning `Flow` must NOT be `suspend`.

## Database registration (lives in `:composeApp`)

`composeApp/src/commonMain/kotlin/com/po4yka/app/data/local/AppDatabase.kt`:

1. Import the new entity + DAO from the `:data:<domain>` module.
2. Add entity to `@Database(entities = [..., <Entity>Entity::class])`.
3. Increment `version` (if not the initial schema).
4. Add abstract accessor: `abstract fun <entity>Dao(): <Entity>Dao`.

```kotlin
@Database(entities = [SampleEntity::class, /* …other entities */], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sampleDao(): SampleDao
    // abstract fun otherDao(): OtherDao
}
```

## Koin registration (also in `:composeApp`)

`composeApp/src/commonMain/kotlin/com/po4yka/app/di/AppModule.kt`, inside the private `databaseModule`:

```kotlin
private val databaseModule: Module = module {
    single<SampleDao> { get<AppDatabase>().sampleDao() }
    // single<OtherDao> { get<AppDatabase>().otherDao() }
}
```

`databaseModule` is already added to `appModules()`. Adding a new DAO binding picks it up automatically.

## Schema Exports

Room schemas are auto-exported to `composeApp/schemas/` on every build with a successful `@Database` compilation. Commit them.

## Driver

`BundledSQLiteDriver` is used across Android and iOS, wired in `composeApp`'s platform modules via `.setDriver(BundledSQLiteDriver())`. No per-platform driver work is needed when adding a new entity.

## Future: repositories over DAOs

Features should depend on a repository interface (declared in `:data:<domain>`) rather than injecting a DAO directly. The template currently wires `HomeViewModel` / `DetailViewModel` straight to `SampleDao` for simplicity; see **Data Ownership** in `AGENTS.md` for the rule that upgrades this once domains grow.
