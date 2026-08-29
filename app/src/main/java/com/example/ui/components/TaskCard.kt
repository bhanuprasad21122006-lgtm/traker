package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.data.model.TaskPriority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TaskCard(
  task: Task,
  onToggleComplete: () -> Unit,
  onToggleSubtask: (String) -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var expandedSubtasks by remember { mutableStateOf(false) }
  var showMenu by remember { mutableStateOf(false) }

  val subtasks = task.subtaskList
  val hasSubtasks = subtasks.isNotEmpty()

  val cardBorderColor by animateColorAsState(
    targetValue = if (task.isCompleted) {
      MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    } else {
      task.category.color.copy(alpha = 0.35f)
    },
    animationSpec = tween(300),
    label = "border_color"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("task_card_${task.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (task.isCompleted) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (task.isCompleted) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f) else cardBorderColor
    ),
    elevation = CardDefaults.cardElevation(
      defaultElevation = if (task.isCompleted) 0.dp else 2.dp
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Top Row: Checkbox, Title & Action Menu
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        // Custom Animated Checkbox
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
              if (task.isCompleted) {
                MaterialTheme.colorScheme.primary
              } else {
                Color.Transparent
              }
            )
            .border(
              width = 2.dp,
              color = if (task.isCompleted) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.outline
              },
              shape = RoundedCornerShape(8.dp)
            )
            .clickable { onToggleComplete() }
            .testTag("task_checkbox_${task.id}"),
          contentAlignment = Alignment.Center
        ) {
          if (task.isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Completed",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Description
        Column(
          modifier = Modifier
            .weight(1f)
            .clickable { onToggleComplete() }
        ) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (task.isCompleted) {
              MaterialTheme.colorScheme.onSurfaceVariant
            } else {
              MaterialTheme.colorScheme.onSurface
            },
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )

          if (task.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = task.description,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // More options dropdown
        Box {
          IconButton(
            onClick = { showMenu = true },
            modifier = Modifier
              .size(32.dp)
              .testTag("task_menu_${task.id}")
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Task options",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            DropdownMenuItem(
              text = { Text("Edit Task") },
              leadingIcon = {
                Icon(Icons.Default.Edit, contentDescription = null)
              },
              onClick = {
                showMenu = false
                onEdit()
              },
              modifier = Modifier.testTag("edit_task_btn_${task.id}")
            )
            DropdownMenuItem(
              text = { Text("Delete Task", color = MaterialTheme.colorScheme.error) },
              leadingIcon = {
                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
              },
              onClick = {
                showMenu = false
                onDelete()
              },
              modifier = Modifier.testTag("delete_task_btn_${task.id}")
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Badges Row: Category, Priority, Due Date, Focus Time
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Category Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(task.category.color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = task.category.icon,
              contentDescription = null,
              tint = task.category.color,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = task.category.displayName,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = task.category.color,
              fontSize = 11.sp
            )
          }
        }

        // Priority Badge (only if HIGH or MEDIUM)
        if (task.priority == TaskPriority.HIGH) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(task.priority.color.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = task.priority.color,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = "High",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = task.priority.color,
                fontSize = 11.sp
              )
            }
          }
        }

        // Due date badge
        val dateInfo = formatDueDate(task.dueDate)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
              if (dateInfo.isOverdue && !task.isCompleted) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
              } else {
                MaterialTheme.colorScheme.surfaceVariant
              }
            )
            .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = if (dateInfo.isOverdue && !task.isCompleted) {
                MaterialTheme.colorScheme.error
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
              modifier = Modifier.size(11.dp)
            )
            Text(
              text = dateInfo.label,
              style = MaterialTheme.typography.labelSmall,
              color = if (dateInfo.isOverdue && !task.isCompleted) {
                MaterialTheme.colorScheme.error
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
              fontSize = 11.sp
            )
          }
        }

        // Focus time estimate
        if (task.estimatedMinutes > 0) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(11.dp)
              )
              Text(
                text = "${task.estimatedMinutes}m",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Subtasks toggle trigger
        if (hasSubtasks) {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .clickable { expandedSubtasks = !expandedSubtasks }
              .padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Checklist,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = "${task.completedSubtaskCount}/${task.totalSubtaskCount}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              fontSize = 11.sp
            )
            Icon(
              imageVector = if (expandedSubtasks) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // Expandable Subtasks Checklist
      AnimatedVisibility(
        visible = expandedSubtasks && hasSubtasks,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "Checklist (${task.completedSubtaskCount}/${task.totalSubtaskCount})",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          subtasks.forEach { subtask ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable { onToggleSubtask(subtask.id) }
                .padding(vertical = 4.dp, horizontal = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = if (subtask.isDone) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (subtask.isDone) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = subtask.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (subtask.isDone) {
                  MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                  MaterialTheme.colorScheme.onSurface
                },
                textDecoration = if (subtask.isDone) TextDecoration.LineThrough else TextDecoration.None
              )
            }
          }
        }
      }
    }
  }
}

data class DueDateInfo(
  val label: String,
  val isOverdue: Boolean
)

fun formatDueDate(timestamp: Long): DueDateInfo {
  val now = System.currentTimeMillis()
  val calNow = Calendar.getInstance().apply { timeInMillis = now }
  val calDue = Calendar.getInstance().apply { timeInMillis = timestamp }

  val isToday = calNow.get(Calendar.YEAR) == calDue.get(Calendar.YEAR) &&
    calNow.get(Calendar.DAY_OF_YEAR) == calDue.get(Calendar.DAY_OF_YEAR)

  val calTomorrow = Calendar.getInstance().apply {
    timeInMillis = now
    add(Calendar.DAY_OF_YEAR, 1)
  }
  val isTomorrow = calTomorrow.get(Calendar.YEAR) == calDue.get(Calendar.YEAR) &&
    calTomorrow.get(Calendar.DAY_OF_YEAR) == calDue.get(Calendar.DAY_OF_YEAR)

  val isOverdue = timestamp < now && !isToday

  val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
  val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

  val label = when {
    isToday -> "Today ${timeFormat.format(Date(timestamp))}"
    isTomorrow -> "Tomorrow"
    isOverdue -> "Overdue (${dateFormat.format(Date(timestamp))})"
    else -> dateFormat.format(Date(timestamp))
  }

  return DueDateInfo(label, isOverdue)
}
