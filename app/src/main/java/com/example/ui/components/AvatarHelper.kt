package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AvatarInfo(
  val id: String,
  val emoji: String,
  val label: String,
  val gradientStart: Color,
  val gradientEnd: Color
)

object AvatarRegistry {
  val avatars = listOf(
    AvatarInfo("avatar_1", "🦊", "Fox", Color(0xFFF97316), Color(0xFFEF4444)),
    AvatarInfo("avatar_2", "⚡", "Spark", Color(0xFF6366F1), Color(0xFF8B5CF6)),
    AvatarInfo("avatar_3", "🚀", "Rocket", Color(0xFF0EA5E9), Color(0xFF06B6D4)),
    AvatarInfo("avatar_4", "🌿", "Sprout", Color(0xFF10B981), Color(0xFF059669)),
    AvatarInfo("avatar_5", "🎯", "Target", Color(0xFFEC4899), Color(0xFFF43F5E)),
    AvatarInfo("avatar_6", "🦁", "Lion", Color(0xFFF59E0B), Color(0xFFD97706))
  )

  fun getAvatar(id: String): AvatarInfo {
    return avatars.find { it.id == id } ?: avatars[0]
  }
}

@Composable
fun UserAvatar(
  avatarId: String,
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  borderWidth: Dp = 2.dp,
  showBorder: Boolean = true
) {
  val avatar = AvatarRegistry.getAvatar(avatarId)
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .then(
        if (showBorder) {
          Modifier.border(
            width = borderWidth,
            color = MaterialTheme.colorScheme.surface,
            shape = CircleShape
          )
        } else Modifier
      )
      .background(
        Brush.linearGradient(
          colors = listOf(avatar.gradientStart, avatar.gradientEnd)
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = avatar.emoji,
      fontSize = (size.value * 0.48).sp
    )
  }
}

@Composable
fun AvatarSelector(
  selectedId: String,
  onSelect: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AvatarRegistry.avatars.forEach { avatar ->
      val isSelected = avatar.id == selectedId
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .border(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            shape = CircleShape
          )
          .background(
            Brush.linearGradient(listOf(avatar.gradientStart, avatar.gradientEnd))
          )
          .clickable { onSelect(avatar.id) }
          .testTag("avatar_select_${avatar.id}"),
        contentAlignment = Alignment.Center
      ) {
        Text(text = avatar.emoji, fontSize = 22.sp)
        if (isSelected) {
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .size(16.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Selected",
              tint = Color.White,
              modifier = Modifier.size(12.dp)
            )
          }
        }
      }
    }
  }
}
