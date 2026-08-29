package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.ui.components.TaskCard
import com.example.ui.viewmodel.TaskSortOption
import com.example.ui.viewmodel.TaskStatusFilter

@Composable
fun TasksScreen(
  tasks: List<Task>,
  rawTasks: List<Task>,
  currentStatusFilter: TaskStatusFilter,
  currentCategoryFilter: TaskCategory?,
  searchQuery: String,
  sortOption: TaskSortOption,
  onStatusFilterChange: (TaskStatusFilter) -> Unit,
  onCategoryFilterChange: (TaskCategory?) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onSortOptionChange: (TaskSortOption) -> Unit,
  onToggleTask: (Task) -> Unit,
  onToggleSubtask: (Task, String) -> Unit,
  onEditTask: (Task) -> Unit,
  onDeleteTask: (Long) -> Unit,
  onClearCompleted: () -> Unit,
  onAddTaskClick: () -> Unit
) {
  var showSortMenu by remember { mutableStateOf(false) }
  val categoryScrollState = rememberScrollState()
  val statusScrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header & Search Area
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Tasks & Goals",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "${tasks.size} task${if (tasks.size == 1) "" else "s"} found",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Sort Selector
        Box {
          TextButton(
            onClick = { showSortMenu = true },
            modifier = Modifier.testTag("sort_dropdown_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Sort,
              contentDescription = "Sort",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = sortOption.label,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
          ) {
            TaskSortOption.values().forEach { option ->
              DropdownMenuItem(
                text = {
                  Text(
                    option.label,
                    fontWeight = if (option == sortOption) FontWeight.Bold else FontWeight.Normal
                  )
                },
                onClick = {
                  onSortOptionChange(option)
                  showSortMenu = false
                }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search by task title or details...", fontSize = 14.sp) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChange("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear search")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_tasks_input")
      )
    }

    // Status Filter Chips Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(statusScrollState)
        .padding(horizontal = 18.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      TaskStatusFilter.values().forEach { filter ->
        val isSelected = filter == currentStatusFilter
        FilterChip(
          selected = isSelected,
          onClick = { onStatusFilterChange(filter) },
          label = {
            Text(
              filter.label,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("status_filter_${filter.name.lowercase()}")
        )
      }
    }

    // Category Filter Chips Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(categoryScrollState)
        .padding(horizontal = 18.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // "All Categories" chip
      val isAllSelected = currentCategoryFilter == null
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(
            if (isAllSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          )
          .clickable { onCategoryFilterChange(null) }
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("cat_filter_all")
      ) {
        Text(
          text = "All Categories",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
          color = if (isAllSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      TaskCategory.values().forEach { category ->
        val isSelected = currentCategoryFilter == category
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
              if (isSelected) category.color.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable { onCategoryFilterChange(if (isSelected) null else category) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("cat_filter_${category.name.lowercase()}")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = category.icon,
              contentDescription = null,
              tint = if (isSelected) category.color else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = category.displayName,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) category.color else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Clear completed tasks button when in completed filter
    if (currentStatusFilter == TaskStatusFilter.COMPLETED && tasks.isNotEmpty()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(
          onClick = onClearCompleted,
          modifier = Modifier.testTag("clear_completed_btn")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            "Clear Completed Tasks",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall
          )
        }
      }
    }

    // Task List LazyColumn
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 18.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
      }

      if (tasks.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            )
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(60.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.TaskAlt,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(32.dp)
                )
              }
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = if (searchQuery.isNotEmpty()) "No matching tasks" else "No tasks in this view",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (searchQuery.isNotEmpty()) {
                  "Try adjusting your search query or filters."
                } else {
                  "You're all caught up! Create a new task to stay organized."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
              )
              Spacer(modifier = Modifier.height(16.dp))
              Button(
                onClick = onAddTaskClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("empty_add_task_btn")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create New Task")
              }
            }
          }
        }
      } else {
        items(tasks, key = { it.id }) { task ->
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
        Spacer(modifier = Modifier.height(90.dp)) // space for bottom navigation & fab
      }
    }
  }
}
