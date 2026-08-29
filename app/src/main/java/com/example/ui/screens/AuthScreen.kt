package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.components.AvatarSelector
import com.example.ui.components.UserAvatar

@Composable
fun AuthScreen(
  allUsers: List<User>,
  authError: String?,
  isLoading: Boolean,
  onLogin: (String, String) -> Unit,
  onSignUp: (String, String, String, String, String, String, Int) -> Unit,
  onQuickLogin: (User) -> Unit,
  onClearError: () -> Unit
) {
  var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up

  // Sign In fields
  var loginIdentifier by remember { mutableStateOf("alexchen") }
  var loginPassword by remember { mutableStateOf("password123") }
  var showLoginPassword by remember { mutableStateOf(false) }

  // Sign Up fields
  var signUpUsername by remember { mutableStateOf("") }
  var signUpEmail by remember { mutableStateOf("") }
  var signUpPassword by remember { mutableStateOf("") }
  var signUpFullName by remember { mutableStateOf("") }
  var signUpJobTitle by remember { mutableStateOf("Designer") }
  var signUpAvatarId by remember { mutableStateOf("avatar_1") }
  var signUpDailyGoal by remember { mutableIntStateOf(5) }
  var showSignUpPassword by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 28.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(24.dp))

      // App Branding Header
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(
            Brush.linearGradient(
              listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Logo",
          tint = Color.White,
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Task Tracker",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Text(
        text = "Your personalized focus & progress companion",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Auth Card Container
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
            .padding(20.dp)
        ) {
          // Tab Row
          TabRow(
            selectedTabIndex = selectedAuthTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
          ) {
            Tab(
              selected = selectedAuthTab == 0,
              onClick = {
                selectedAuthTab = 0
                onClearError()
              },
              text = {
                Text(
                  "Sign In",
                  fontWeight = if (selectedAuthTab == 0) FontWeight.Bold else FontWeight.Medium
                )
              },
              modifier = Modifier.testTag("tab_sign_in")
            )
            Tab(
              selected = selectedAuthTab == 1,
              onClick = {
                selectedAuthTab = 1
                onClearError()
              },
              text = {
                Text(
                  "Create Account",
                  fontWeight = if (selectedAuthTab == 1) FontWeight.Bold else FontWeight.Medium
                )
              },
              modifier = Modifier.testTag("tab_sign_up")
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Error banner
          AnimatedVisibility(
            visible = authError != null,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
                .padding(12.dp)
            ) {
              Text(
                text = authError ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
              )
            }
          }

          if (selectedAuthTab == 0) {
            // --- SIGN IN FORM ---
            OutlinedTextField(
              value = loginIdentifier,
              onValueChange = {
                loginIdentifier = it
                onClearError()
              },
              label = { Text("Username or Email") },
              leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_identifier_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = loginPassword,
              onValueChange = {
                loginPassword = it
                onClearError()
              },
              label = { Text("Password") },
              leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              trailingIcon = {
                IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                  Icon(
                    imageVector = if (showLoginPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showLoginPassword) "Hide password" else "Show password"
                  )
                }
              },
              visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
              keyboardActions = KeyboardActions(onDone = { onLogin(loginIdentifier, loginPassword) }),
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = { onLogin(loginIdentifier, loginPassword) },
              enabled = !isLoading && loginIdentifier.isNotBlank() && loginPassword.isNotBlank(),
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("sign_in_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
              )
            ) {
              if (isLoading) {
                CircularProgressIndicator(
                  modifier = Modifier.size(22.dp),
                  color = Color.White,
                  strokeWidth = 2.dp
                )
              } else {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Text("Sign In to Dashboard", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                  Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
              }
            }
          } else {
            // --- SIGN UP FORM ---
            OutlinedTextField(
              value = signUpFullName,
              onValueChange = { signUpFullName = it },
              label = { Text("Full Name") },
              leadingIcon = {
                Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_fullname_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = signUpUsername,
              onValueChange = { signUpUsername = it },
              label = { Text("Username") },
              leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_username_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = signUpEmail,
              onValueChange = { signUpEmail = it },
              label = { Text("Email Address") },
              leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_email_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = signUpJobTitle,
              onValueChange = { signUpJobTitle = it },
              label = { Text("Role / Goal (e.g. Software Dev, Student)") },
              leadingIcon = {
                Icon(Icons.Default.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_jobtitle_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = signUpPassword,
              onValueChange = { signUpPassword = it },
              label = { Text("Password (min 4 chars)") },
              leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              trailingIcon = {
                IconButton(onClick = { showSignUpPassword = !showSignUpPassword }) {
                  Icon(
                    imageVector = if (showSignUpPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                  )
                }
              },
              visualTransformation = if (showSignUpPassword) VisualTransformation.None else PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_password_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Avatar picker
            Text(
              text = "Choose Avatar",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AvatarSelector(
              selectedId = signUpAvatarId,
              onSelect = { signUpAvatarId = it },
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Target Goal Slider
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Daily Task Target",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "$signUpDailyGoal tasks / day",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Slider(
              value = signUpDailyGoal.toFloat(),
              onValueChange = { signUpDailyGoal = it.toInt() },
              valueRange = 1f..15f,
              steps = 13,
              modifier = Modifier.testTag("signup_goal_slider")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = {
                onSignUp(
                  signUpUsername,
                  signUpEmail,
                  signUpPassword,
                  signUpFullName,
                  signUpJobTitle,
                  signUpAvatarId,
                  signUpDailyGoal
                )
              },
              enabled = !isLoading && signUpUsername.isNotBlank() && signUpEmail.isNotBlank() && signUpPassword.isNotBlank(),
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("create_account_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
              )
            ) {
              if (isLoading) {
                CircularProgressIndicator(
                  modifier = Modifier.size(22.dp),
                  color = Color.White,
                  strokeWidth = 2.dp
                )
              } else {
                Text("Create Account & Enter", fontWeight = FontWeight.Bold, fontSize = 15.sp)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Quick Demo Profiles Picker
      if (allUsers.isNotEmpty()) {
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
              .padding(16.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.TrackChanges,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "Instant 1-Tap Demo Login",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "Explore the personalized dashboard with pre-loaded tasks and streaks:",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              allUsers.take(3).forEach { user ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                      width = 1.dp,
                      color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                      shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onQuickLogin(user) }
                    .padding(10.dp)
                    .testTag("quick_login_${user.username}"),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  UserAvatar(avatarId = user.avatarId, size = 36.dp)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = user.fullName,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "${user.jobTitle} • Goal: ${user.dailyTaskGoal}/day",
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

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
