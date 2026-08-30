package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

@Composable
fun TasksScreen(
  tasks: List<Task>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  selectedCategory: String?,
  onCategorySelect: (String?) -> Unit,
  sortBy: String,
  onSortChange: (String) -> Unit,
  onToggleComplete: (Task) -> Unit,
  onToggleSubtask: (Task, String) -> Unit,
  onEditTask: (Task) -> Unit,
  onDeleteTask: (Long) -> Unit
) {
  Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      // Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(16.dp))
          Text("My Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(16.dp))
          // Avatar Placeholder
          Box(
            modifier = Modifier
              .size(32.dp)
              .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
              .background(Color(0xFFF9FAFB))
          )
        }
      }

      // Filters
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(text = "Sort: Due Date", isActive = true)
        FilterChip(text = "Filter: Work", isActive = true, hasClose = true)
        FilterChip(text = "+ Add Filter", isActive = false)
      }
      
      Spacer(modifier = Modifier.height(16.dp))

      // Date Picker / Month View
      Text(
        "August 2026",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 20.dp)
      )
      Spacer(modifier = Modifier.height(12.dp))
      
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item { DateCard(date = "12", day = "Wed", isSelected = false) }
        item { DateCard(date = "13", day = "Thu", isSelected = false) }
        item { DateCard(date = "14", day = "Fri", isSelected = true) }
        item { DateCard(date = "15", day = "Sat", isSelected = false) }
        item { DateCard(date = "16", day = "Sun", isSelected = false) }
        item { DateCard(date = "17", day = "Mon", isSelected = false) }
      }

      Spacer(modifier = Modifier.height(24.dp))
      
      // Tasks List Header
      Text(
        "Work Tasks (4)",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF6B7280),
        modifier = Modifier.padding(horizontal = 20.dp)
      )
      
      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        modifier = Modifier.fillMaxWidth().weight(1f),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        // Mock items to match design
        item { MockTaskListItem("Prepare Q3 roadmap", "Today • 11:00 AM", "High", true) }
        item { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB))) }
        item { MockTaskListItem("Email client: pricing update", "Today • by 2:00 PM", null, false) }
        item { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB))) }
        item { MockTaskListItem("Review design sprint notes", "Today • 4:30 PM", "Low", false) }
        item { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB))) }
        item { MockTaskListItem("Update brand guidelines", "Tomorrow • 10:00 AM", null, false) }
        item { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB))) }
      }
    }

    // FAB
    FloatingActionButton(
      onClick = { /* Add task action */ },
      containerColor = Color.Black,
      contentColor = Color.White,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .size(56.dp)
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(24.dp))
    }
  }
}

@Composable
fun FilterChip(text: String, isActive: Boolean, hasClose: Boolean = false) {
  Row(
    modifier = Modifier
      .height(32.dp)
      .border(1.dp, if (isActive) Color.Black else Color(0xFFD1D5DB), RoundedCornerShape(16.dp))
      .background(if (isActive) Color(0xFFF3F4F6) else Color.White, RoundedCornerShape(16.dp))
      .padding(horizontal = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = text,
      color = if (isActive) Color.Black else Color(0xFF4B5563),
      fontSize = 12.sp,
      fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
    )
    if (hasClose) {
      Spacer(modifier = Modifier.width(6.dp))
      Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Black, modifier = Modifier.size(12.dp))
    }
  }
}

@Composable
fun DateCard(date: String, day: String, isSelected: Boolean) {
  Column(
    modifier = Modifier
      .width(56.dp)
      .height(72.dp)
      .border(1.dp, if (isSelected) Color.Black else Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
      .background(if (isSelected) Color.Black else Color.White, RoundedCornerShape(12.dp))
      .padding(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = date,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = if (isSelected) Color.White else Color.Black
    )
    Text(
      text = day,
      style = MaterialTheme.typography.bodySmall,
      color = if (isSelected) Color.White else Color(0xFF6B7280)
    )
  }
}

@Composable
fun MockTaskListItem(title: String, subtitle: String, tag: String?, hasImage: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(20.dp)
        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(4.dp))
        .background(Color.White)
        .padding(top = 2.dp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    
    if (hasImage) {
      Box(modifier = Modifier.size(36.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp)))
      Spacer(modifier = Modifier.width(12.dp))
    }
    
    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Black, fontWeight = FontWeight.Medium)
      Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
    }
    
    if (tag != null) {
      Text(
        text = tag,
        style = MaterialTheme.typography.labelSmall,
        color = if (tag == "High") Color(0xFFEF4444) else Color(0xFF6B7280),
        fontWeight = if (tag == "High") FontWeight.Medium else FontWeight.Normal
      )
    }
  }
}
