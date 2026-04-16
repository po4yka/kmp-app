# KMP Room Entity Conventions

## Table Naming

Use `snake_case` for table names (e.g., `sample_items`).

## Entity Naming

`XxxEntity` class with `@Entity(tableName = "xxx")`:

```kotlin
@Entity(tableName = "sample_items")
data class SampleItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // fields
)
```

- `@PrimaryKey(autoGenerate = true)` with `Long` type, default value `0`

## DAO Naming

`XxxDao` interface with `@Dao`:

```kotlin
@Dao
interface SampleItemDao {
    @Insert
    suspend fun insert(item: SampleItemEntity)

    @Query("SELECT * FROM sample_items ORDER BY id DESC")
    fun getAll(): Flow<List<SampleItemEntity>>

    @Query("SELECT * FROM sample_items WHERE id = :id")
    suspend fun getById(id: Long): SampleItemEntity?

    @Query("DELETE FROM sample_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
```

## DAO Function Rules

- All DAO functions must be `suspend` for KMP compatibility
- Exception: functions that return `Flow` must NOT be `suspend`

## Database Registration

In `AppDatabase.kt`:

1. Add entity to `@Database(entities = [..., XxxEntity::class])`
2. Increment `version`
3. Add abstract accessor: `abstract fun xxxDao(): XxxDao`

## Koin Registration

In `AppModule.kt`:

```kotlin
single { get<AppDatabase>().xxxDao() }
```

## Schema Exports

Room schemas are auto-exported to `composeApp/schemas/`.

## Driver

`BundledSQLiteDriver` is used for cross-platform consistency across Android and iOS.
