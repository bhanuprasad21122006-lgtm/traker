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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User

@Composable
fun ProfileScreen(
  user: User,
  streakDays: Int,
  completedTasksCount: Int,
  onLogout: () -> Unit,
  onUpdateUser: (User) -> Unit, // Kept for API compatibility
  onNavigateBack: () -> Unit
) {
  var isDarkMode by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
      // Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(24.dp).clickable { onNavigateBack() })
        Text("Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.size(24.dp))
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Header (Avatar + Info)
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier.size(80.dp),
          contentAlignment = Alignment.BottomEnd
        ) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .border(1.dp, Color(0xFFE5E7EB), CircleShape)
              .background(Color(0xFFF9FAFB), CircleShape)
          )
          
          // Edit Badge
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(Color.Black)
              .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(12.dp))
          }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Text(user.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(user.jobTitle, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B7280))
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Stats
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        StatCard(title = "Completed", value = completedTasksCount.toString(), modifier = Modifier.weight(1f))
        StatCard(title = "Current streak", value = "$streakDays Days", modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Account Settings
      Text(
        "Account Settings",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF6B7280),
        modifier = Modifier.padding(horizontal = 20.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))
      SettingsItem(icon = Icons.Default.PersonOutline, title = "Personal info", onClick = {})
      SettingsItem(icon = Icons.Default.NotificationsNone, title = "Notification preferences", onClick = {})
      SettingsItem(icon = Icons.Default.WorkOutline, title = "Workspaces", onClick = {})

      Spacer(modifier = Modifier.height(24.dp))

      // App Settings
      Text(
        "App Settings",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF6B7280),
        modifier = Modifier.padding(horizontal = 20.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))
      
      // Dark Mode Switch
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isDarkMode = !isDarkMode }
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.DarkMode, contentDescription = null, tint = Color(0xFF4B5563), modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(16.dp))
          Text("Dark mode", style = MaterialTheme.typography.bodyMedium, color = Color.Black, fontWeight = FontWeight.Medium)
        }
        Switch(
          checked = isDarkMode,
          onCheckedChange = { isDarkMode = it },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color.Black
          )
        )
      }

      SettingsItem(icon = Icons.Default.Link, title = "App integrations", onClick = {})
      SettingsItem(icon = Icons.Default.HelpOutline, title = "Help & support", onClick = {})

      Spacer(modifier = Modifier.height(48.dp))

      // Logout
      Button(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
      ) {
        Text("Log out", fontWeight = FontWeight.Medium)
      }

      Spacer(modifier = Modifier.height(100.dp)) // space for bottom nav
    }
  }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
    elevation = CardDefaults.cardElevation(0.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
      Spacer(modifier = Modifier.height(4.dp))
      Text(title, style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
    }
  }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 20.dp, vertical = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(icon, contentDescription = null, tint = Color(0xFF4B5563), modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(16.dp))
      Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Black, fontWeight = FontWeight.Medium)
    }
    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
  }
}
