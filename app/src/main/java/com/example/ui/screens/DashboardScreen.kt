package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.data.model.User
import com.example.ui.components.CategoryProgressItem
import com.example.ui.components.CategoryProgressRow
import com.example.ui.components.DayCompletionData
import com.example.ui.components.ProgressRing
import com.example.ui.components.TaskCard
import com.example.ui.components.UserAvatar
import com.example.ui.components.WeeklyCompletionChart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
  user: User,
  tasks: List<Task>,
  streakDays: Int,
  weeklyData: List<DayCompletionData>,
  categoryProgress: List<CategoryProgressItem>,
  onToggleTask: (Task) -> Unit,
  onToggleSubtask: (Task, String) -> Unit,
  onEditTask: (Task) -> Unit,
  onDeleteTask: (Long) -> Unit,
  onNavigateToTasks: () -> Unit,
  onNavigateToAnalytics: () -> Unit,
  onAddTaskClick: () -> Unit
) {
  val now = System.currentTimeMillis()
  val calNow = Calendar.getInstance().apply { timeInMillis = now }

  // Compute Today's Tasks
  val todayTasks = tasks.filter { task ->
    val calTask = Calendar.getInstance().apply { timeInMillis = task.dueDate }
    calNow.get(Calendar.YEAR) == calTask.get(Calendar.YEAR) &&
      calNow.get(Calendar.DAY_OF_YEAR) == calTask.get(Calendar.DAY_OF_YEAR)
  }

  val todayCompletedCount = todayTasks.count { it.isCompleted }
  val targetGoal = user.dailyTaskGoal.coerceAtLeast(1)
  val todayProgressRatio = (todayCompletedCount.toFloat() / targetGoal.toFloat()).coerceIn(0f, 1f)

  val todayFocusMinutes = todayTasks.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
  val pendingTasks = todayTasks.filter { !it.isCompleted }

  val greeting = getGreeting(user.fullName)
  val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
  val todayDateString = dateFormat.format(Date(now))

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 18.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))

      // User Header Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = todayDateString.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
          )
          Text(
            text = greeting,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "${user.jobTitle} • Goal: $targetGoal/day",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        UserAvatar(avatarId = user.avatarId, size = 50.dp)
      }
    }

    // Hero Progress Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("hero_progress_card"),
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
            // Circular progress ring
            ProgressRing(
              progress = todayProgressRatio,
              size = 100.dp,
              strokeWidth = 10.dp,
              primaryColor = MaterialTheme.colorScheme.primary,
              secondaryColor = MaterialTheme.colorScheme.tertiary,
              backgroundColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "${(todayProgressRatio * 100).toInt()}%",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "$todayCompletedCount/$targetGoal",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Quick Metrics Column
            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(
                text = if (todayCompletedCount >= targetGoal) "🎉 Goal Achieved!" else "Daily Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (todayCompletedCount >= targetGoal) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
              )

              // Streak Row
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF97316).copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color(0xFFF97316),
                    modifier = Modifier.size(16.dp)
                  )
                }
                Column {
                  Text(
                    text = "$streakDays Day Streak",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = if (streakDays > 0) "Keep the momentum!" else "Complete a task today",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                  )
                }
              }

              // Focus Time Row
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                }
                Column {
                  Text(
                    text = "${todayFocusMinutes}m Focus Done",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Goal: ${user.focusGoalMinutes}m/day",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // Weekly Activity Bar Chart
    item {
      WeeklyCompletionChart(
        weeklyData = weeklyData,
        modifier = Modifier.testTag("weekly_chart")
      )
    }

    // Smart Insight Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.tertiary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.TrendingUp,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (todayCompletedCount >= targetGoal) {
                "Outstanding work!"
              } else {
                "Productivity Insight"
              },
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
              text = if (todayCompletedCount >= targetGoal) {
                "You smashed your daily goal of $targetGoal tasks! Take a well-deserved rest or plan tomorrow's wins."
              } else if (pendingTasks.isNotEmpty()) {
                "${pendingTasks.size} task${if (pendingTasks.size > 1) "s" else ""} remaining for today. Finish them to maintain your $streakDays-day streak!"
              } else {
                "No tasks scheduled for today. Tap the '+' button below to add your focus goals."
              },
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
            )
          }
        }
      }
    }

    // Category Distribution Summary
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
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Focus by Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${categoryProgress.size} active",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            categoryProgress.take(4).forEach { item ->
              CategoryProgressRow(item = item)
            }
          }
        }
      }
    }

    // Today's Priority Tasks Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Today's Tasks",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "${todayTasks.count { !it.isCompleted }} pending",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onNavigateToTasks,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("dashboard_view_all_tasks_btn")
          ) {
            Text("View All (${tasks.size})", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
          }
        }
      }
    }

    // Today's Tasks List items
    if (todayTasks.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No tasks due today",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Plan ahead and schedule tasks for today.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = onAddTaskClick,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("dashboard_empty_add_task_btn")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Add Today's First Task")
            }
          }
        }
      }
    } else {
      items(todayTasks, key = { it.id }) { task ->
        TaskCard(
          task = task,
          onToggleComplete = { onToggleTask(task) },
          onToggleSubtask = { subtaskId -> onToggleSubtask(task, subtaskId) },
          onEdit = { onEditTask(task) },
          onDelete = { onDeleteTask(task.id) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(90.dp)) // padding for bottom bar
    }
  }
}

private fun getGreeting(fullName: String): String {
  val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
  val firstName = fullName.split(" ").firstOrNull() ?: fullName
  return when (hour) {
    in 5..11 -> "Good morning, $firstName! ☀️"
    in 12..16 -> "Good afternoon, $firstName! 🌤️"
    in 17..21 -> "Good evening, $firstName! 🌙"
    else -> "Welcome back, $firstName! 🌟"
  }
}
