package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority

class Converters {
  @TypeConverter
  fun fromTaskCategory(category: TaskCategory): String = category.name

  @TypeConverter
  fun toTaskCategory(value: String): TaskCategory {
    return try {
      TaskCategory.valueOf(value)
    } catch (e: Exception) {
      TaskCategory.WORK
    }
  }

  @TypeConverter
  fun fromTaskPriority(priority: TaskPriority): String = priority.name

  @TypeConverter
  fun toTaskPriority(value: String): TaskPriority {
    return try {
      TaskPriority.valueOf(value)
    } catch (e: Exception) {
      TaskPriority.MEDIUM
    }
  }
}
