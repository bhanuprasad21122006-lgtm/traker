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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.data.model.User
import com.example.ui.components.AvatarSelector
import com.example.ui.components.UserAvatar

@Composable
fun ProfileScreen(
  user: User,
  allUsers: List<User>,
  tasks: List<Task>,
  onUpdateProfile: (String, String, Int, Int, String) -> Unit,
  onQuickLogin: (User) -> Unit,
  onClearCompleted: () -> Unit,
  onLogout: () -> Unit
) {
  var fullName by remember(user) { mutableStateOf(user.fullName) }
  var jobTitle by remember(user) { mutableStateOf(user.jobTitle) }
  var selectedAvatarId by remember(user) { mutableStateOf(user.avatarId) }
  var dailyGoal by remember(user) { mutableIntStateOf(user.dailyTaskGoal) }
  var focusGoalMinutes by remember(user) { mutableIntStateOf(user.focusGoalMinutes) }
  var isSavedNoticeVisible by remember { mutableStateOf(false) }

  var showLogoutConfirm by remember { mutableStateOf(false) }
  var showClearConfirm by remember { mutableStateOf(false) }

  val totalCompleted = tasks.count { it.isCompleted }
  val totalFocusMinutes = tasks.filter { it.isCompleted }.sumOf { it.estimatedMinutes }

  if (showLogoutConfirm) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirm = false },
      title = { Text("Log Out?") },
      text = { Text("Are you sure you want to log out of ${user.fullName}'s account?") },
      confirmButton = {
        Button(
          onClick = {
            showLogoutConfirm = false
            onLogout()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
          modifier = Modifier.testTag("confirm_logout_btn")
        ) {
          Text("Log Out")
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }

  if (showClearConfirm) {
    AlertDialog(
      onDismissRequest = { showClearConfirm = false },
      title = { Text("Clear Completed Tasks?") },
      text = { Text("This will permanently remove all finished tasks for this account.") },
      confirmButton = {
        Button(
          onClick = {
            showClearConfirm = false
            onClearCompleted()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Clear All")
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 18.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Profile & Settings",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    // Profile Hero Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
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
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          UserAvatar(avatarId = selectedAvatarId, size = 72.dp)

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = user.fullName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${user.email} • @${user.username}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Account summary pills
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "$totalCompleted",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "Tasks Done",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${totalFocusMinutes / 60}h ${totalFocusMinutes % 60}m",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
              )
              Text(
                text = "Focus Logged",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${user.dailyTaskGoal}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B)
              )
              Text(
                text = "Daily Target",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Edit Profile Form Card
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
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text(
            text = "Personalize Profile",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          // Avatar Selector
          Column {
            Text(
              text = "Choose Avatar",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            AvatarSelector(
              selectedId = selectedAvatarId,
              onSelect = { selectedAvatarId = it },
              modifier = Modifier.fillMaxWidth()
            )
          }

          OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("profile_name_input")
          )

          OutlinedTextField(
            value = jobTitle,
            onValueChange = { jobTitle = it },
            label = { Text("Role / Specialty") },
            leadingIcon = {
              Icon(Icons.Default.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("profile_jobtitle_input")
          )

          // Daily Task Goal
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Daily Task Target Goal",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "$dailyGoal tasks/day",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Slider(
              value = dailyGoal.toFloat(),
              onValueChange = { dailyGoal = it.toInt() },
              valueRange = 1f..15f,
              steps = 13,
              modifier = Modifier.testTag("profile_goal_slider")
            )
          }

          // Daily Focus Goal
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Daily Focus Time Target",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "$focusGoalMinutes min/day",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Slider(
              value = focusGoalMinutes.toFloat(),
              onValueChange = { focusGoalMinutes = it.toInt() },
              valueRange = 30f..360f,
              steps = 10,
              modifier = Modifier.testTag("profile_focus_slider")
            )
          }

          Button(
            onClick = {
              onUpdateProfile(fullName, jobTitle, dailyGoal, focusGoalMinutes, selectedAvatarId)
              isSavedNoticeVisible = true
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("save_profile_btn")
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Profile Changes", fontWeight = FontWeight.Bold)
          }

          if (isSavedNoticeVisible) {
            Text(
              text = "✓ Profile updated successfully",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.tertiary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // Switch Account Card
    if (allUsers.size > 1) {
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.SwitchAccount,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "Switch Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            allUsers.filter { it.id != user.id }.forEach { otherUser ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                  .clickable { onQuickLogin(otherUser) }
                  .padding(10.dp)
                  .testTag("switch_to_${otherUser.username}"),
                verticalAlignment = Alignment.CenterVertically
              ) {
                UserAvatar(avatarId = otherUser.avatarId, size = 36.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = otherUser.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = otherUser.jobTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                  )
                }
                Icon(
                  imageVector = Icons.Default.ArrowForward,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }

    // Account Actions (Clear Completed & Logout)
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
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Account Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          OutlinedButton(
            onClick = { showClearConfirm = true },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("profile_clear_completed_btn"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clear All Completed Tasks", color = MaterialTheme.colorScheme.error)
          }

          Button(
            onClick = { showLogoutConfirm = true },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("logout_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.errorContainer,
              contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
          ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out of Session", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(90.dp))
    }
  }
}
