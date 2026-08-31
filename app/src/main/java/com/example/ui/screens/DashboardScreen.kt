package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subtask
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import com.example.data.model.User
import com.example.ui.components.CategoryProgressItem
import com.example.ui.components.DayCompletionData
import com.example.ui.components.ProgressRing
import com.example.ui.theme.MyApplicationTheme
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
  onAddTaskClick: () -> Unit,
  onQuickAdd: (String) -> Unit = {}
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Starred, 2: Upcoming
  var quickAddTaskText by remember { mutableStateOf("") }

  val now = System.currentTimeMillis()

  // Filter tasks based on selected tab
  val filteredTasks = when (selectedTab) {
    0 -> tasks
    1 -> tasks.filter { it.isStarred }
    2 -> tasks.filter { it.dueDate > now && !isSameDay(it.dueDate, now) }
    else -> tasks
  }

  // Sections
  val todayTasks = filteredTasks.filter { isSameDay(it.dueDate, now) }
  val upcomingTasks = filteredTasks.filter { it.dueDate > now && !isSameDay(it.dueDate, now) }
  val overdueTasks = filteredTasks.filter { it.dueDate < now && !isSameDay(it.dueDate, now) && !it.isCompleted }

  val todayCompletedCount = todayTasks.count { it.isCompleted }
  val todayTotalCount = todayTasks.size
  val todayProgressRatio = if (todayTotalCount > 0) todayCompletedCount.toFloat() / todayTotalCount.toFloat() else 0.64f

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))

      // Overview Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Overview",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Today",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "$todayTotalCount tasks due",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Priority: High • Focus mode",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
              )
            }

            ProgressRing(
              progress = todayProgressRatio,
              size = 80.dp,
              strokeWidth = 8.dp,
              primaryColor = Color.Black,
              secondaryColor = Color.LightGray,
              backgroundColor = Color(0xFFF5F5F5)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "${(todayProgressRatio * 100).toInt()}%",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "complete",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  color = Color.Gray
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Quick Add Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              if (quickAddTaskText.isEmpty()) {
                Text(
                  text = "Quick add a task — e.g., Call",
                  style = MaterialTheme.typography.bodyMedium,
                  color = Color.LightGray
                )
              }
              BasicTextField(
                value = quickAddTaskText,
                onValueChange = { quickAddTaskText = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                  if (quickAddTaskText.isNotBlank()) {
                    onQuickAdd(quickAddTaskText)
                    quickAddTaskText = ""
                  }
                })
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (quickAddTaskText.isNotBlank()) {
                  onQuickAdd(quickAddTaskText)
                  quickAddTaskText = ""
                }
              },
              shape = RoundedCornerShape(4.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
              modifier = Modifier.height(40.dp)
            ) {
              Text("Add", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Filter Tabs
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DashboardFilterTab(
          label = "All",
          isSelected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          modifier = Modifier.weight(1f)
        )
        DashboardFilterTab(
          label = "Starred",
          isSelected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          modifier = Modifier.weight(1f)
        )
        DashboardFilterTab(
          label = "Upcoming",
          isSelected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Today Section
    if (todayTasks.isNotEmpty()) {
      item {
        DashboardSectionHeader(
          title = "Today",
          subtitle = "${todayTasks.size} tasks • ${todayTasks.sumOf { it.estimatedMinutes } / 60} hours estimated",
          onViewAll = onNavigateToTasks
        )
      }
      items(todayTasks) { task ->
        DashboardTaskItem(task = task, onToggle = { onToggleTask(task) })
      }
    }

    // Upcoming Section
    if (upcomingTasks.isNotEmpty()) {
      item {
        DashboardSectionHeader(
          title = "Upcoming (next 7 days)",
          subtitle = "Plan ahead and balance your week",
          badge = "${upcomingTasks.size} items"
        )
      }
      items(upcomingTasks) { task ->
        DashboardTaskItem(task = task, onToggle = { onToggleTask(task) })
      }
    }

    // Overdue Section
    if (overdueTasks.isNotEmpty()) {
      item {
        DashboardSectionHeader(
          title = "Overdue",
          subtitle = "${overdueTasks.size} tasks overdue",
          badge = "${overdueTasks.size}",
          badgeColor = Color(0xFFFFEBEE),
          badgeTextColor = Color.Red
        )
      }
      items(overdueTasks) { task ->
        DashboardTaskItem(task = task, onToggle = { onToggleTask(task) }, isOverdue = true)
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }
}

@Composable
fun DashboardFilterTab(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .height(36.dp)
      .clip(RoundedCornerShape(4.dp))
      .background(if (isSelected) Color.Black else Color.White)
      .border(1.dp, if (isSelected) Color.Black else Color(0xFFEEEEEE), RoundedCornerShape(4.dp))
      .clickable { onClick() },
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge,
      fontWeight = FontWeight.Medium,
      color = if (isSelected) Color.White else Color.Black
    )
  }
}

@Composable
fun DashboardSectionHeader(
  title: String,
  subtitle: String,
  badge: String? = null,
  badgeColor: Color = Color.White,
  badgeTextColor: Color = Color.Black,
  onViewAll: (() -> Unit)? = null
) {
  Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
      if (onViewAll != null) {
        Text(
          text = "View all",
          style = MaterialTheme.typography.labelLarge,
          color = Color.Gray,
          modifier = Modifier.clickable { onViewAll() }
        )
      } else if (badge != null) {
        Surface(
          color = badgeColor,
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall,
            color = badgeTextColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodySmall,
      color = Color.Gray
    )
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(color = Color(0xFFF5F5F5))
  }
}

@Composable
fun DashboardTaskItem(
  task: Task,
  onToggle: () -> Unit,
  isOverdue: Boolean = false
) {
  val dateLabel = if (isOverdue) {
    "Overdue • ${daysBetween(task.dueDate, System.currentTimeMillis())} days"
  } else {
    val sdf = SimpleDateFormat("EEE • h:mm a", Locale.getDefault())
    sdf.format(Date(task.dueDate))
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Checkbox(
      checked = task.isCompleted,
      onCheckedChange = { onToggle() },
      colors = CheckboxDefaults.colors(checkedColor = Color.Black)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = task.title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = if (task.isCompleted) Color.Gray else Color.Black
      )
      Text(
        text = dateLabel,
        style = MaterialTheme.typography.labelSmall,
        color = if (isOverdue) Color.Red else Color.Gray
      )
    }
    Text(
      text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
      style = MaterialTheme.typography.labelSmall,
      color = if (isOverdue) Color.Red else Color.Gray,
      modifier = Modifier.padding(start = 8.dp)
    )
  }
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
  val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
  val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
  return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun daysBetween(time1: Long, time2: Long): Long {
  val diff = time2 - time1
  return diff / (24 * 60 * 60 * 1000)
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
  val mockUser = User(
    id = 1,
    username = "johndoe",
    email = "john@example.com",
    passwordHash = "",
    fullName = "John Doe",
    jobTitle = "Designer",
    avatarId = "avatar_1",
    dailyTaskGoal = 5,
    focusGoalMinutes = 120
  )

  val mockTasks = listOf(
    Task(id = 1, userId = 1, title = "Prepare Q3 roadmap", priority = TaskPriority.HIGH, dueDate = System.currentTimeMillis(), isStarred = true),
    Task(id = 2, userId = 1, title = "Email client: pricing update", priority = TaskPriority.MEDIUM, dueDate = System.currentTimeMillis() + 3 * 3600000),
    Task(id = 3, userId = 1, title = "Review design sprint notes", priority = TaskPriority.LOW, dueDate = System.currentTimeMillis() + 6 * 3600000)
  )

  MyApplicationTheme {
    DashboardScreen(
      user = mockUser,
      tasks = mockTasks,
      streakDays = 5,
      weeklyData = emptyList(),
      categoryProgress = emptyList(),
      onToggleTask = {},
      onToggleSubtask = { _, _ -> },
      onEditTask = {},
      onDeleteTask = {},
      onNavigateToTasks = {},
      onNavigateToAnalytics = {},
      onAddTaskClick = {}
    )
  }
}
