package com.example.practicotres.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.practicotres.data.dao.TagDao
import com.example.practicotres.data.dao.TaskDao
import com.example.practicotres.data.dao.TaskTagDao
import com.example.practicotres.data.entities.Tag
import com.example.practicotres.data.entities.Priority
import com.example.practicotres.data.entities.Task
import com.example.practicotres.data.entities.TaskTagCrossRef
import java.util.Date

@Database(
    entities = [Task::class, Tag::class, TaskTagCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun tagDao(): TagDao
    abstract fun taskTagDao(): TaskTagDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = Priority.valueOf(priority)
}