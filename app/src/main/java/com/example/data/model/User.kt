package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val username: String,
  val email: String,
  val passwordHash: String,
  val fullName: String,
  val jobTitle: String = "Productivity Enthusiast",
  val avatarId: String = "avatar_1",
  val dailyTaskGoal: Int = 5,
  val focusGoalMinutes: Int = 120,
  val createdAt: Long = System.currentTimeMillis()
)
