package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueDate ASC, priority DESC, id DESC")
  fun getTasksForUser(userId: Long): Flow<List<Task>>

  @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
  fun getTaskById(taskId: Long): Flow<Task?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: Task): Long

  @Update
  suspend fun updateTask(task: Task)

  @Delete
  suspend fun deleteTask(task: Task)

  @Query("DELETE FROM tasks WHERE id = :taskId")
  suspend fun deleteTaskById(taskId: Long)

  @Query("DELETE FROM tasks WHERE userId = :userId AND isCompleted = 1")
  suspend fun clearCompletedTasks(userId: Long)

  @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId")
  suspend fun getTaskCount(userId: Long): Int
}
