package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class TaskPriority(
  val displayName: String,
  val colorHex: Long,
  val weight: Int
) {
  HIGH("High Priority", 0xFFEF4444, 3),
  MEDIUM("Medium", 0xFFF59E0B, 2),
  LOW("Low Priority", 0xFF10B981, 1);

  val color: Color
    get() = Color(colorHex)
}
