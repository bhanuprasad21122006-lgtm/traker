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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.data.model.User
import com.example.ui.components.CategoryProgressItem
import com.example.ui.components.DayCompletionData
import com.example.ui.components.ProgressRing
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
  onNavigateToAnalytics: () -> Unit, // Keeping for API compatibility
  onAddTaskClick: () -> Unit
) {
  val now = System.currentTimeMillis()
  val calNow = Calendar.getInstance().apply { timeInMillis = now }

  val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
  val todayDateString = dateFormat.format(Date(now))

  val todayTasks = tasks.filter { task ->
    val calTask = Calendar.getInstance().apply { timeInMillis = task.dueDate }
    calNow.get(Calendar.YEAR) == calTask.get(Calendar.YEAR) &&
      calNow.get(Calendar.DAY_OF_YEAR) == calTask.get(Calendar.DAY_OF_YEAR)
  }

  val todayCompletedCount = todayTasks.count { it.isCompleted }
  val targetGoal = user.dailyTaskGoal.coerceAtLeast(1)
  val todayProgressRatio = (todayCompletedCount.toFloat() / targetGoal.toFloat()).coerceIn(0f, 1f)
  val estimatedHours = todayTasks.sumOf { it.estimatedMinutes } / 60

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))
      
      // Top Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Menu, contentDescription = "Menu", modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Text("TaskFlow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        
        Text(todayDateString, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF4B5563))
        
        // Avatar Placeholder (Empty square like in screenshot)
        Box(
          modifier = Modifier
            .size(32.dp)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
            .background(Color(0xFFF9FAFB))
        )
      }
      
      Spacer(modifier = Modifier.height(16.dp))
      Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
    }

    // Overview Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(0.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Text("Overview", style = MaterialTheme.typography.labelMedium, color = Color(0xFF6B7280))
          Spacer(modifier = Modifier.height(4.dp))
          
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column {
              Text("Today", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Normal)
              Spacer(modifier = Modifier.height(8.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text("${todayTasks.size} tasks due", fontSize = 12.sp, color = Color(0xFF1E3A8A))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Priority: High • Focus mode", fontSize = 11.sp, color = Color(0xFF6B7280))
              }
            }
            
            // Progress Ring
            ProgressRing(
              progress = todayProgressRatio,
              size = 80.dp,
              strokeWidth = 6.dp,
              primaryColor = Color(0xFF111827),
              secondaryColor = Color.Transparent,
              backgroundColor = Color(0xFFE5E7EB)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(todayProgressRatio * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("complete", fontSize = 10.sp, color = Color(0xFF6B7280))
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Quick Add Input
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = "",
              onValueChange = {},
              placeholder = { Text("Quick add a task — e.g., Call...", fontSize = 13.sp, color = Color(0xFF9CA3AF)) },
              singleLine = true,
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.weight(1f).height(48.dp),
              colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFF9CA3AF),
                focusedBorderColor = Color.Black
              )
            )
            Button(
              onClick = { },
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.height(48.dp).width(72.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
              Text("Add", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Tabs
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TabButton(text = "All", isSelected = true, modifier = Modifier.weight(1f))
        TabButton(text = "Starred", isSelected = false, modifier = Modifier.weight(1f))
        TabButton(text = "Upcoming", isSelected = false, modifier = Modifier.weight(1f))
      }
    }

    // Today Section
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(0.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Today", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
              Text("${todayTasks.size} tasks • $estimatedHours hours estimated", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
            }
            Text("View all", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4B5563), modifier = Modifier.clickable { onNavigateToTasks() })
          }

          // Mock Today Tasks based on design
          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
          MockTaskItem("Prepare Q3 roadmap", "Today • 11:00 AM", "High", true)
          MockTaskItem("Email client: pricing update", "Today • by 2:00 PM", null, false)
          MockTaskItem("Review design sprint notes", "Today • 4:30 PM", "Low", false)
        }
      }
    }

    // Upcoming Section
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(0.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Upcoming (next 7 days)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
              Text("Plan ahead and balance your week", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
            }
            Text("2 items", style = MaterialTheme.typography.bodySmall, color = Color(0xFF111827))
          }

          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
          MockUpcomingTaskItem("Sprint retrospective", "Thu • 9:00 AM", "Thu")
          MockUpcomingTaskItem("Design handoff", "Sat • 10:30 AM", "Sat")
          
          // Illustration Placeholder
          Box(
            modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0xFFF9FAFB)),
            contentAlignment = Alignment.Center
          ) {
            Text("Illustration Placeholder", fontSize = 12.sp, color = Color(0xFF9CA3AF))
          }
        }
      }
    }

    // Overdue Section
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(0.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Overdue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
              Text("2 tasks overdue", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
            }
            Box(
              modifier = Modifier.background(Color(0xFFFEE2E2), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("2", style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
            }
          }

          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
          MockTaskItem("Submit expense report", "Overdue • 2 days", "High", false, isOverdue = true)
          MockTaskItem("Follow up: vendor invoice", "Overdue • 1 day", null, false, isOverdue = true)
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(90.dp))
    }
  }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .height(40.dp)
      .border(1.dp, if (isSelected) Color.Black else Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
      .background(if (isSelected) Color.Black else Color.White, RoundedCornerShape(4.dp))
      .padding(horizontal = 16.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = if (isSelected) Color.White else Color(0xFF4B5563),
      fontSize = 13.sp,
      fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
    )
  }
}

@Composable
fun MockTaskItem(title: String, subtitle: String, tag: String?, hasImage: Boolean = false, isOverdue: Boolean = false) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(20.dp)
        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(4.dp))
        .background(Color.White)
        .padding(top = 2.dp) // Just to give some space
    )
    Spacer(modifier = Modifier.width(12.dp))
    
    // Image placeholder if true
    if (hasImage) {
      Box(modifier = Modifier.size(32.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp)))
      Spacer(modifier = Modifier.width(12.dp))
    }
    
    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
      Text(subtitle, style = MaterialTheme.typography.bodySmall, color = if (isOverdue) Color(0xFFEF4444) else Color(0xFF6B7280))
    }
    
    if (tag != null) {
      Text(
        text = tag,
        style = MaterialTheme.typography.labelSmall,
        color = if (tag == "High" || isOverdue) Color(0xFFEF4444) else Color(0xFF6B7280),
        fontWeight = if (tag == "High" || isOverdue) FontWeight.Medium else FontWeight.Normal,
        modifier = Modifier.padding(top = 2.dp)
      )
    }
  }
}

@Composable
fun MockUpcomingTaskItem(title: String, subtitle: String, dayTag: String) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(20.dp)
        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(4.dp))
        .background(Color.White)
    )
    Spacer(modifier = Modifier.width(12.dp))
    
    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
      Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
    }
    
    Box(
      modifier = Modifier.background(Color(0xFFEFF6FF), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Text(dayTag, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1E3A8A))
    }
  }
}
