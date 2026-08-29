package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.data.model.TaskPriority
import com.example.data.model.User
import com.example.ui.components.CategoryProgressItem
import com.example.ui.components.CategoryProgressRow
import com.example.ui.components.DayCompletionData
import com.example.ui.components.ProgressRing
import com.example.ui.components.WeeklyCompletionChart
import com.example.ui.viewmodel.AchievementBadge

@Composable
fun AnalyticsScreen(
  user: User,
  tasks: List<Task>,
  streakDays: Int,
  weeklyData: List<DayCompletionData>,
  categoryProgress: List<CategoryProgressItem>,
  achievements: List<AchievementBadge>
) {
  val totalTasks = tasks.size
  val completedTasks = tasks.count { it.isCompleted }
  val completionRate = if (totalTasks > 0) ((completedTasks.toFloat() / totalTasks.toFloat()) * 100).toInt() else 0

  val totalFocusMinutes = tasks.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
  val highPriorityTasks = tasks.filter { it.priority == TaskPriority.HIGH }
  val highPriorityCompleted = highPriorityTasks.count { it.isCompleted }

  // Productivity Score Calculation (0 - 100)
  val baseScore = (completionRate * 0.5f).toInt()
  val streakBonus = (streakDays * 5).coerceAtMost(25)
  val priorityBonus = if (highPriorityTasks.isNotEmpty()) {
    ((highPriorityCompleted.toFloat() / highPriorityTasks.size.toFloat()) * 25).toInt()
  } else 20
  val productivityScore = (baseScore + streakBonus + priorityBonus).coerceIn(0, 100)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 18.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Column {
        Text(
          text = "Progress & Analytics",
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Personalized velocity and productivity trends for ${user.fullName}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Productivity Score Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("productivity_score_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                listOf(
                  MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                  MaterialTheme.colorScheme.surface
                )
              )
            )
            .padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Insights,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "PRODUCTIVITY SCORE",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  letterSpacing = 1.sp
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "$productivityScore / 100",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )

              val ratingLabel = when {
                productivityScore >= 85 -> "🔥 Top Tier Momentum"
                productivityScore >= 70 -> "⚡ High Performance"
                productivityScore >= 50 -> "🌱 Steady Progress"
                else -> "🚀 Building Routine"
              }

              Text(
                text = ratingLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary
              )
            }

            ProgressRing(
              progress = productivityScore / 100f,
              size = 80.dp,
              strokeWidth = 8.dp,
              primaryColor = MaterialTheme.colorScheme.primary,
              secondaryColor = MaterialTheme.colorScheme.tertiary,
              backgroundColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
              )
            }
          }
        }
      }
    }

    // Key Stats Grid (4 Cards)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Stat 1: Tasks Completed
          MetricCard(
            title = "Completed",
            value = "$completedTasks / $totalTasks",
            subtitle = "$completionRate% success rate",
            icon = Icons.Default.CheckCircle,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f)
          )

          // Stat 2: Streak
          MetricCard(
            title = "Current Streak",
            value = "$streakDays Days",
            subtitle = "Consecutive active days",
            icon = Icons.Default.LocalFireDepartment,
            color = Color(0xFFF97316),
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Stat 3: Focus Time
          MetricCard(
            title = "Focus Logged",
            value = "${totalFocusMinutes / 60}h ${totalFocusMinutes % 60}m",
            subtitle = "Completed task time",
            icon = Icons.Default.Schedule,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
          )

          // Stat 4: High Priority Resolved
          MetricCard(
            title = "High Priority",
            value = "$highPriorityCompleted / ${highPriorityTasks.size}",
            subtitle = "Critical tasks cleared",
            icon = Icons.Default.Verified,
            color = Color(0xFFEF4444),
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 7-Day Completion Trends
    item {
      WeeklyCompletionChart(
        weeklyData = weeklyData,
        modifier = Modifier.testTag("analytics_weekly_chart")
      )
    }

    // Category Distribution Breakdown
    if (categoryProgress.isNotEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Text(
              text = "Category Performance",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            categoryProgress.forEach { item ->
              CategoryProgressRow(item = item)
            }
          }
        }
      }
    }

    // Achievements Trophy Shelf
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(22.dp)
              )
              Text(
                text = "Achievements & Badges",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "${achievements.count { it.isUnlocked }}/${achievements.size} Unlocked",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            achievements.forEach { badge ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(
                    if (badge.isUnlocked) {
                      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    } else {
                      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    }
                  )
                  .border(
                    width = 1.dp,
                    color = if (badge.isUnlocked) {
                      Color(0xFFF59E0B).copy(alpha = 0.4f)
                    } else {
                      MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(14.dp)
                  )
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Badge Icon Box
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                      if (badge.isUnlocked) {
                        Brush.linearGradient(
                          listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                        )
                      } else {
                        Brush.linearGradient(
                          listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                        )
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (badge.isUnlocked) {
                    Text(text = badge.emoji, fontSize = 22.sp)
                  } else {
                    Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = "Locked",
                      tint = Color(0xFF64748B),
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = badge.title,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    if (badge.isUnlocked) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(Color(0xFF10B981).copy(alpha = 0.15f))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = "UNLOCKED",
                          style = MaterialTheme.typography.labelSmall,
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF059669)
                        )
                      }
                    }
                  }
                  Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                  )
                }

                Text(
                  text = badge.progress,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (badge.isUnlocked) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(90.dp))
    }
  }
}

@Composable
fun MetricCard(
  title: String,
  value: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
        Box(
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(15.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp
      )
    }
  }
}
