package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "tasks",
  foreignKeys = [
    ForeignKey(
      entity = User::class,
      parentColumns = ["id"],
      childColumns = ["userId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["userId"]),
    Index(value = ["dueDate"]),
    Index(value = ["isCompleted"])
  ]
)
data class Task(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val userId: Long,
  val title: String,
  val description: String = "",
  val category: TaskCategory = TaskCategory.WORK,
  val priority: TaskPriority = TaskPriority.MEDIUM,
  val dueDate: Long = System.currentTimeMillis(),
  val estimatedMinutes: Int = 30,
  val isCompleted: Boolean = false,
  val isStarred: Boolean = false,
  val completedAt: Long? = null,
  val subtasksRaw: String = "",
  val createdAt: Long = System.currentTimeMillis()
) {
  val subtaskList: List<Subtask>
    get() = Subtask.stringToList(subtasksRaw)

  val completedSubtaskCount: Int
    get() = subtaskList.count { it.isDone }

  val totalSubtaskCount: Int
    get() = subtaskList.size
}
