---
name: kmp-entity
description: Add a new Room entity with DAO and register in AppDatabase. Use when adding database tables or persistence.
argument-hint: [EntityName]
---

# Add Room Entity

Create a new Room entity named **$ARGUMENTS** following project conventions.

## Steps

### 1. Create the Entity

Create `composeApp/src/commonMain/kotlin/com/po4yka/app/data/local/entity/<EntityName>Entity.kt`:

```kotlin
@Entity(tableName = "<entity_snake_case>")
data class <EntityName>Entity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // TODO: Add fields
)
```

### 2. Create the DAO

Create `composeApp/src/commonMain/kotlin/com/po4yka/app/data/local/dao/<EntityName>Dao.kt`:

```kotlin
@Dao
interface <EntityName>Dao {
    @Insert
    suspend fun insert(item: <EntityName>Entity)

    @Query("SELECT * FROM <entity_snake_case> ORDER BY id DESC")
    fun getAll(): Flow<List<<EntityName>Entity>>

    @Query("SELECT * FROM <entity_snake_case> WHERE id = :id")
    suspend fun getById(id: Long): <EntityName>Entity?

    @Query("DELETE FROM <entity_snake_case> WHERE id = :id")
    suspend fun deleteById(id: Long)
}
```

All DAO functions must be `suspend` for KMP compatibility (except Flow-returning queries).

### 3. Register in AppDatabase

In `composeApp/src/commonMain/kotlin/com/po4yka/app/data/local/AppDatabase.kt`:

1. Add entity to `@Database(entities = [..., <EntityName>Entity::class])` 
2. Increment the `version` number
3. Add abstract DAO accessor: `abstract fun <entityName>Dao(): <EntityName>Dao`

### 4. Register DAO in Koin

In `composeApp/src/commonMain/kotlin/com/po4yka/app/di/AppModule.kt`, add:

```kotlin
single { get<AppDatabase>().<entityName>Dao() }
```

### 5. Verify

Run `./gradlew androidApp:assembleDebug` to confirm Android compiles.
Run `./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64` to confirm iOS links.

The Room schema will be auto-exported to `composeApp/schemas/`.
