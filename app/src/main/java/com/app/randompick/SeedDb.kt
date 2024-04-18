package com.app.randompick

import android.content.Context
import android.util.Log
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Database(
    entities = [Seed::class, SeedGroup::class],
    version = 1,
    exportSchema = false
)
abstract class SeedDb : RoomDatabase() {
    companion object {
        private var INSTANCE: SeedDb? = null

        fun getInstance(context: Context): SeedDb {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                SeedDb::class.java,
                "seed_db"
            ).build()
        }
    }

    abstract fun getDao(): SeedDao
}

@Dao
interface SeedDao {
    @Query("select * from table_seed")
    fun queryAll(): List<Seed>

    @Transaction
    @Query("select * from table_seed_group")
    fun queryGroups(): List<SeedGroups>

    @Transaction
    @Query("select * from table_seed_group")
    fun queryGroupsAsFlow(): Flow<List<SeedGroups>>

    @Insert
    fun addSeed(seed: Seed)

    @Delete
    fun deleteSeed(seed: Seed)

    @Insert
    fun addSeedGroup(group: SeedGroup)

    @Delete
    fun deleteSeedGroup(group: SeedGroup)
}