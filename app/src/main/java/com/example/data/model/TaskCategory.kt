package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TaskCategory(
  val displayName: String,
  val colorHex: Long,
  val iconName: String
) {
  WORK("Work", 0xFF6366F1, "business"),
  STUDY("Study", 0xFF0EA5E9, "book"),
  PERSONAL("Personal", 0xFFEC4899, "home"),
  HEALTH("Health & Fitness", 0xFF10B981, "fitness"),
  CREATIVE("Creative", 0xFFF59E0B, "brush"),
  FINANCE("Finance", 0xFF8B5CF6, "finance");

  val color: Color
    get() = Color(colorHex)

  val icon: ImageVector
    get() = when (this) {
      WORK -> Icons.Default.BusinessCenter
      STUDY -> Icons.Default.MenuBook
      PERSONAL -> Icons.Default.Home
      HEALTH -> Icons.Default.FitnessCenter
      CREATIVE -> Icons.Default.Brush
      FINANCE -> Icons.Default.Payments
    }
}
