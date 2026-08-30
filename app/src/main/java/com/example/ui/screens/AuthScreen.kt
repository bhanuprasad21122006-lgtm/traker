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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User

@Composable
fun AuthScreen(
  allUsers: List<User>,
  authError: String?,
  isLoading: Boolean,
  onLogin: (String, String) -> Unit,
  onSignUp: (String, String, String, String, String, String, Int) -> Unit, // Keeping for API compatibility
  onQuickLogin: (User) -> Unit,
  onClearError: () -> Unit
) {
  var loginEmail by remember { mutableStateOf("you@domain.com") }
  var loginPassword by remember { mutableStateOf("Enter your password") }

  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Bar (TaskFlow Logo + Skip)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(Color.Black),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text("TaskFlow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Secure", fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
          }
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            "Skip",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable {
              if (allUsers.isNotEmpty()) onQuickLogin(allUsers.first())
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Header Texts
      Text(
        text = "Welcome to TaskFlow",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Organize your day, stay on track",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF6B7280)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Placeholder for Carousel Image
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFFF3F4F6)),
        contentAlignment = Alignment.Center
      ) {
        Text("Illustration Placeholder", color = Color(0xFF9CA3AF))
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Quick Add",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Capture tasks instantly with one-tap addition and\nnatural language input for due dates.",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF6B7280),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Dots Indicator
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.width(16.dp).height(6.dp).clip(CircleShape).background(Color.Black))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFD1D5DB)))
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Login Card / Form area
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(0.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp)
        ) {
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
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFEE2E2))
                .padding(12.dp)
            ) {
              Text(
                text = authError ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFEF4444),
                fontWeight = FontWeight.Medium
              )
            }
          }

          Text("Email", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = loginEmail,
            onValueChange = {
              loginEmail = it
              onClearError()
            },
            singleLine = true,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("login_email_input"),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedBorderColor = Color(0xFFD1D5DB),
              focusedBorderColor = Color.Black
            )
          )

          Spacer(modifier = Modifier.height(16.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Password", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            Text("Forgot password?", style = MaterialTheme.typography.labelSmall, color = Color(0xFF4B5563))
          }
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = loginPassword,
            onValueChange = {
              loginPassword = it
              onClearError()
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onLogin(loginEmail, loginPassword) }),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("login_password_input"),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedBorderColor = Color(0xFFD1D5DB),
              focusedBorderColor = Color.Black
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "Securely encrypted • Two-factor available",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF6B7280),
              fontSize = 11.sp
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = { onLogin(loginEmail, loginPassword) },
            enabled = !isLoading && loginEmail.isNotBlank() && loginPassword.isNotBlank(),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("sign_in_button"),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
          ) {
            if (isLoading) {
              CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
              Text("Log in", fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Or sign in with
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE5E7EB)))
            Text(
              "Or sign in with",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF9CA3AF),
              modifier = Modifier.padding(horizontal = 12.dp)
            )
            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE5E7EB)))
          }

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            SocialCircle()
            Spacer(modifier = Modifier.width(16.dp))
            SocialCircle()
            Spacer(modifier = Modifier.width(16.dp))
            SocialCircle()
          }

          Spacer(modifier = Modifier.height(24.dp))

          OutlinedButton(
            onClick = {
              if (allUsers.isNotEmpty()) onQuickLogin(allUsers.first())
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text("Continue as guest", color = Color.Black, fontWeight = FontWeight.Medium)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { /* MOCK: Trigger QuickLogin for demo purposes */
              if (allUsers.isNotEmpty()) onQuickLogin(allUsers.first())
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
          ) {
            Text("Create account", fontWeight = FontWeight.Medium)
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "By continuing you agree to TaskFlow's Terms & Privacy.\nData is stored securely and encrypted.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B7280),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Text("Need help?", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4B5563), fontSize = 11.sp)
        Text("  •  ", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD1D5DB), fontSize = 11.sp)
        Text("Contact support", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4B5563), fontSize = 11.sp)
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text("© 2026 TaskFlow · Terms · Privacy", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9CA3AF), fontSize = 11.sp)
      
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun SocialCircle() {
  Box(
    modifier = Modifier
      .size(44.dp)
      .clip(CircleShape)
      .border(1.dp, Color(0xFFE5E7EB), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    // Placeholder for social icon
    Box(modifier = Modifier.size(20.dp).background(Color(0xFFF3F4F6)))
  }
}
