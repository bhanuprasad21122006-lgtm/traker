package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.User
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
  var isSignUpMode by remember { mutableStateOf(false) }

  // Sign In fields
  var loginIdentifier by remember { mutableStateOf("") }
  var loginPassword by remember { mutableStateOf("") }
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
      .background(Color.White)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // --- HEADER ---
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color.Black),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "TaskFlow",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFFF3F4F6),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(end = 12.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Secure",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black
              )
            }
          }
          Text(
            text = "Skip",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.clickable { /* Handle Skip */ }
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      if (!isSignUpMode) {
        // --- ONBOARDING SECTION ---
        OnboardingCarousel()

        Spacer(modifier = Modifier.height(40.dp))

        // --- LOGIN CARD ---
        LoginCard(
          identifier = loginIdentifier,
          onIdentifierChange = { loginIdentifier = it },
          password = loginPassword,
          onPasswordChange = { loginPassword = it },
          showPassword = showLoginPassword,
          onTogglePassword = { showLoginPassword = !showLoginPassword },
          authError = authError,
          isLoading = isLoading,
          onLogin = { onLogin(loginIdentifier, loginPassword) },
          onSignUpClick = { isSignUpMode = true },
          allUsers = allUsers,
          onQuickLogin = onQuickLogin
        )
      } else {
        // --- SIGN UP SECTION ---
        SignUpSection(
          fullName = signUpFullName,
          onFullNameChange = { signUpFullName = it },
          username = signUpUsername,
          onUsernameChange = { signUpUsername = it },
          email = signUpEmail,
          onEmailChange = { signUpEmail = it },
          password = signUpPassword,
          onPasswordChange = { signUpPassword = it },
          showPassword = showSignUpPassword,
          onTogglePassword = { showSignUpPassword = !showSignUpPassword },
          isLoading = isLoading,
          onSignUp = {
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
          onBackToLogin = { isSignUpMode = false }
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      // --- FOOTER ---
      Row(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Need help?", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(" • ", color = Color.Gray)
        Text("Contact support", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
      }
      Text(
        text = "© 2026 TaskFlow · Terms · Privacy",
        style = MaterialTheme.typography.labelSmall,
        color = Color.Gray
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun OnboardingCarousel() {
  val pages = listOf(
    OnboardingPage(
      title = "Welcome to TaskFlow",
      description = "Organize your day, stay on track",
      imageRes = R.drawable.onboarding_1
    ),
    OnboardingPage(
      title = "Quick Add",
      description = "Capture tasks instantly with one-tap addition and natural language input for due dates.",
      imageRes = R.drawable.onboarding_2
    ),
    OnboardingPage(
      title = "Reminders & Projects",
      description = "Smart reminders and project organization keep your focus where it matters most.",
      imageRes = R.drawable.onboarding_3
    )
  )

  val pagerState = rememberPagerState(pageCount = { pages.size })

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    HorizontalPager(
      state = pagerState,
      modifier = Modifier.fillMaxWidth()
    ) { pageIndex ->
      val page = pages[pageIndex]
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        Text(
          text = page.title,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
        Text(
          text = page.description,
          style = MaterialTheme.typography.bodyMedium,
          color = Color.Gray,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Image Container
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF3F4F6)),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Pager Indicator
    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      repeat(pages.size) { i ->
        val color = if (pagerState.currentPage == i) Color.Black else Color.LightGray
        val width = if (pagerState.currentPage == i) 24.dp else 8.dp
        Box(
          modifier = Modifier
            .padding(2.dp)
            .size(width = width, height = 8.dp)
            .clip(CircleShape)
            .background(color)
        )
      }
    }
  }
}

data class OnboardingPage(val title: String, val description: String, val imageRes: Int)

@Composable
fun LoginCard(
  identifier: String,
  onIdentifierChange: (String) -> Unit,
  password: (String),
  onPasswordChange: (String) -> Unit,
  showPassword: (Boolean),
  onTogglePassword: () -> Unit,
  authError: String?,
  isLoading: Boolean,
  onLogin: () -> Unit,
  onSignUpClick: () -> Unit,
  allUsers: List<User>,
  onQuickLogin: (User) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(4.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
  ) {
    Column(modifier = Modifier.padding(24.dp)) {
      Text(
        text = "Email",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
      )
      OutlinedTextField(
        value = identifier,
        onValueChange = onIdentifierChange,
        placeholder = { Text("you@domain.com", color = Color.LightGray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedBorderColor = Color(0xFFDDDDDD),
          focusedBorderColor = Color.Black,
          cursorColor = Color.Black,
          focusedLabelColor = Color.Black,
          unfocusedLabelColor = Color.Gray
        )
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Password",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Forgot password?",
          style = MaterialTheme.typography.labelSmall,
          color = Color.Gray,
          modifier = Modifier.clickable { /* Handle Forgot */ }
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text("Enter your password", color = Color.LightGray) },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedBorderColor = Color(0xFFDDDDDD),
          focusedBorderColor = Color.Black,
          cursorColor = Color.Black,
          focusedLabelColor = Color.Black,
          unfocusedLabelColor = Color.Gray
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          modifier = Modifier.size(14.dp),
          tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Securely encrypted • Two-factor available",
          style = MaterialTheme.typography.labelSmall,
          color = Color.Gray
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = onLogin,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
      ) {
        if (isLoading) {
          CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
        } else {
          Text("Log in", fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(color = Color(0xFFEEEEEE))
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Or sign in with",
        style = MaterialTheme.typography.labelSmall,
        color = Color.Gray,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        SocialIconPlaceholder()
        Spacer(modifier = Modifier.width(20.dp))
        SocialIconPlaceholder()
        Spacer(modifier = Modifier.width(20.dp))
        SocialIconPlaceholder()
      }

      Spacer(modifier = Modifier.height(24.dp))

      OutlinedButton(
        onClick = { /* Guest Login */ },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD))
      ) {
        Text("Continue as guest", color = Color.Black)
      }

      Spacer(modifier = Modifier.height(12.dp))

      Button(
        onClick = onSignUpClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111))
      ) {
        Text("Create account", fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "By continuing you agree to TaskFlow's Terms & Privacy.\nData is stored securely and encrypted.",
        style = MaterialTheme.typography.labelSmall,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }

  // Demo Profiles
  if (allUsers.isNotEmpty()) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = "Demo Login",
      style = MaterialTheme.typography.labelMedium,
      color = Color.Gray,
      modifier = Modifier.padding(start = 8.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      allUsers.take(3).forEach { user ->
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(1.dp, Color.LightGray, CircleShape)
            .clickable { onQuickLogin(user) },
          contentAlignment = Alignment.Center
        ) {
          UserAvatar(avatarId = user.avatarId, size = 32.dp)
        }
      }
    }
  }
}

@Composable
fun SocialIconPlaceholder(icon: ImageVector? = null) {
  Box(
    modifier = Modifier
      .size(56.dp)
      .clip(CircleShape)
      .border(1.dp, Color(0xFFEEEEEE), CircleShape)
      .clickable { /* Social Login */ },
    contentAlignment = Alignment.Center
  ) {
    if (icon != null) {
      Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
    }
  }
}

@Composable
fun SignUpSection(
  fullName: String,
  onFullNameChange: (String) -> Unit,
  username: String,
  onUsernameChange: (String) -> Unit,
  email: String,
  onEmailChange: (String) -> Unit,
  password: (String),
  onPasswordChange: (String) -> Unit,
  showPassword: (Boolean),
  onTogglePassword: () -> Unit,
  isLoading: Boolean,
  onSignUp: () -> Unit,
  onBackToLogin: () -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Create your account",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(24.dp))

    // Full Name
    Text("Full Name", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    OutlinedTextField(
      value = fullName,
      onValueChange = onFullNameChange,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(4.dp),
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = Color(0xFFDDDDDD),
        focusedBorderColor = Color.Black,
        cursorColor = Color.Black,
        focusedLabelColor = Color.Black
      )
    )
    Spacer(modifier = Modifier.height(12.dp))

    // Username
    Text("Username", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    OutlinedTextField(
      value = username,
      onValueChange = onUsernameChange,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(4.dp),
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = Color(0xFFDDDDDD),
        focusedBorderColor = Color.Black,
        cursorColor = Color.Black,
        focusedLabelColor = Color.Black
      )
    )
    Spacer(modifier = Modifier.height(12.dp))

    // Email
    Text("Email Address", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    OutlinedTextField(
      value = email,
      onValueChange = onEmailChange,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(4.dp),
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = Color(0xFFDDDDDD),
        focusedBorderColor = Color.Black,
        cursorColor = Color.Black,
        focusedLabelColor = Color.Black
      )
    )
    Spacer(modifier = Modifier.height(12.dp))

    // Password
    Text("Password", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    OutlinedTextField(
      value = password,
      onValueChange = onPasswordChange,
      visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(4.dp),
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = Color(0xFFDDDDDD),
        focusedBorderColor = Color.Black,
        cursorColor = Color.Black,
        focusedLabelColor = Color.Black
      )
    )
    Spacer(modifier = Modifier.height(24.dp))

    Button(
      onClick = onSignUp,
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      shape = RoundedCornerShape(4.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
    ) {
      if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
      } else {
        Text("Create account", fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    TextButton(
      onClick = onBackToLogin,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Already have an account? Log in", color = Color.Gray)
    }
  }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
  AuthScreen(
    allUsers = emptyList(),
    authError = null,
    isLoading = false,
    onLogin = { _, _ -> },
    onSignUp = { _, _, _, _, _, _, _ -> },
    onQuickLogin = {},
    onClearError = {}
  )
}
