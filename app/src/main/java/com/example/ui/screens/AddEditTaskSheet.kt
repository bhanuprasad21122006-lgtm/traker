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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task

@Composable
fun AddEditTaskSheet(
  taskToEdit: Task?,
  onDismiss: () -> Unit,
  onSave: (String, String, Long, Int, Int, String) -> Unit
) {
  var title by remember { mutableStateOf(taskToEdit?.title ?: "") }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Default.Close,
          contentDescription = "Close",
          modifier = Modifier.size(24.dp).clickable { onDismiss() }
        )
        Text(
          text = if (taskToEdit == null) "Add task" else "Edit task",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
        Button(
          onClick = { 
            // In a real app we'd map all properties here. Mocking the rest for now.
            onSave(title, taskToEdit?.description ?: "", taskToEdit?.dueDate ?: System.currentTimeMillis(), taskToEdit?.priority ?: 1, taskToEdit?.estimatedMinutes ?: 60, taskToEdit?.category ?: "Work")
          },
          shape = RoundedCornerShape(4.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
          modifier = Modifier.height(36.dp)
        ) {
          Text("Save", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Title Input
      Text("Title", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.Black)
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        placeholder = { Text("Task title...", color = Color(0xFF9CA3AF)) },
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedBorderColor = Color(0xFFD1D5DB),
          focusedBorderColor = Color.Black
        )
      )

      Spacer(modifier = Modifier.height(32.dp))

      // Properties
      PropertyRow(icon = Icons.Default.CalendarToday, label = "Date", value = "Today")
      Spacer(modifier = Modifier.height(16.dp))
      PropertyRow(icon = Icons.Default.Schedule, label = "Time", value = "11:00 AM - 1:00 PM")
      Spacer(modifier = Modifier.height(16.dp))
      PropertyRow(icon = Icons.Default.Flag, label = "Priority", value = "High")
      Spacer(modifier = Modifier.height(16.dp))
      PropertyRow(icon = Icons.Default.Notifications, label = "Reminder", value = "15 min before")

      Spacer(modifier = Modifier.height(32.dp))

      // Subtasks
      Text("Subtasks", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.Black)
      Spacer(modifier = Modifier.height(16.dp))
      
      // Mock subtasks
      SubtaskItem("Draft email copy", true)
      SubtaskItem("Review with marketing", false)
      SubtaskItem("Schedule delivery", false)

      Spacer(modifier = Modifier.height(16.dp))
      
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
        Icon(Icons.Default.Add, contentDescription = "Add subtask", tint = Color.Black, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add subtask", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
      }

      Spacer(modifier = Modifier.height(48.dp))

      // Delete Button
      if (taskToEdit != null) {
        TextButton(
          onClick = { /* Delete action */ },
          modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
          Text("Delete task", color = Color(0xFFEF4444), fontWeight = FontWeight.Medium)
        }
      }
      
      Spacer(modifier = Modifier.height(48.dp))
    }
  }
}

@Composable
fun PropertyRow(icon: ImageVector, label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(icon, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(12.dp))
      Text(label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF4B5563))
    }
    
    Box(
      modifier = Modifier
        .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Text(value, fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    }
  }
}

@Composable
fun SubtaskItem(title: String, isCompleted: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(20.dp)
        .border(1.dp, if (isCompleted) Color.Black else Color(0xFFD1D5DB), RoundedCornerShape(4.dp))
        .background(if (isCompleted) Color.Black else Color.White),
      contentAlignment = Alignment.Center
    ) {
      if (isCompleted) {
        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp)) // Mocking a checkmark with an Add icon for simplicity, or ideally Icons.Default.Check
      }
    }
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.bodyMedium,
      color = if (isCompleted) Color(0xFF9CA3AF) else Color.Black,
      textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
    )
  }
}
