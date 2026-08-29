package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subtask
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskSheet(
  taskToEdit: Task?,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSaveTask: (
    title: String,
    description: String,
    category: TaskCategory,
    priority: TaskPriority,
    dueDate: Long,
    estimatedMinutes: Int,
    subtasks: List<Subtask>
  ) -> Unit
) {
  val isEditing = taskToEdit != null

  var title by remember(taskToEdit) { mutableStateOf(taskToEdit?.title ?: "") }
  var description by remember(taskToEdit) { mutableStateOf(taskToEdit?.description ?: "") }
  var category by remember(taskToEdit) { mutableStateOf(taskToEdit?.category ?: TaskCategory.WORK) }
  var priority by remember(taskToEdit) { mutableStateOf(taskToEdit?.priority ?: TaskPriority.MEDIUM) }
  var estimatedMinutes by remember(taskToEdit) { mutableIntStateOf(taskToEdit?.estimatedMinutes ?: 30) }

  val defaultDueDate = remember(taskToEdit) {
    taskToEdit?.dueDate ?: (System.currentTimeMillis() + 4 * 3600 * 1000L)
  }
  var dueDate by remember(taskToEdit) { mutableLongStateOf(defaultDueDate) }

  val subtasks = remember(taskToEdit) {
    mutableStateListOf<Subtask>().apply {
      if (taskToEdit != null) {
        addAll(taskToEdit.subtaskList)
      }
    }
  }

  var newSubtaskText by remember { mutableStateOf("") }
  var titleError by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()
  val categoryScrollState = rememberScrollState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Sheet Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isEditing) "Edit Task" else "Create New Task",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Organize goals, priority, and focus timeline",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      // Title Input
      OutlinedTextField(
        value = title,
        onValueChange = {
          title = it
          if (it.isNotBlank()) titleError = false
        },
        label = { Text("Task Title *") },
        placeholder = { Text("e.g., Finalize design review presentation") },
        isError = titleError,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_title_input")
      )
      if (titleError) {
        Text(
          text = "Please enter a task title.",
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall
        )
      }

      // Description Input
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description & Notes (optional)") },
        placeholder = { Text("Add key links, details, or acceptance criteria...") },
        maxLines = 3,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_desc_input")
      )

      // Category Selector
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Category",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(categoryScrollState),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TaskCategory.values().forEach { cat ->
            val isSelected = cat == category
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isSelected) cat.color.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .clickable { category = cat }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("select_category_${cat.name.lowercase()}"),
              contentAlignment = Alignment.Center
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = cat.icon,
                  contentDescription = null,
                  tint = if (isSelected) cat.color else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = cat.displayName,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) cat.color else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // Priority Selector
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(
            text = "Priority Level",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TaskPriority.values().forEach { p ->
            val isSelected = p == priority
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isSelected) p.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .clickable { priority = p }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = p.displayName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) p.color else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Due Date Presets
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(
            text = "Schedule Due Date",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
        }

        val now = System.currentTimeMillis()
        val duePresets = listOf(
          "Today" to (now + 4 * 3600 * 1000L),
          "Tomorrow" to (now + 24 * 3600 * 1000L),
          "In 3 Days" to (now + 3 * 24 * 3600 * 1000L),
          "Next Week" to (now + 7 * 24 * 3600 * 1000L)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          duePresets.forEach { (label, timestamp) ->
            val isSelected = Math.abs(dueDate - timestamp) < 12 * 3600 * 1000L
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .clickable { dueDate = timestamp }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Focus Time Duration
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(
            text = "Estimated Focus Time",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
        }
        val durations = listOf(15, 30, 45, 60, 90)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          durations.forEach { mins ->
            val isSelected = estimatedMinutes == mins
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .clickable { estimatedMinutes = mins }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${mins}m",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Checklist / Subtasks Builder
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(
            text = "Subtasks Checklist (${subtasks.size})",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
        }

        // Subtask input
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = newSubtaskText,
            onValueChange = { newSubtaskText = it },
            placeholder = { Text("Add a step / subtask...", fontSize = 13.sp) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("subtask_input")
          )
          Button(
            onClick = {
              if (newSubtaskText.isNotBlank()) {
                subtasks.add(Subtask(title = newSubtaskText.trim()))
                newSubtaskText = ""
              }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("add_subtask_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = "Add subtask")
          }
        }

        // Existing Subtasks List
        subtasks.forEachIndexed { index, item ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
              .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "• ${item.title}",
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.weight(1f)
            )
            IconButton(
              onClick = { subtasks.removeAt(index) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove subtask",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Save Button
      Button(
        onClick = {
          if (title.isBlank()) {
            titleError = true
          } else {
            onSaveTask(
              title,
              description,
              category,
              priority,
              dueDate,
              estimatedMinutes,
              subtasks.toList()
            )
          }
        },
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("save_task_submit_btn"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isEditing) "Save Task Changes" else "Create Task",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
      }
    }
  }
}
