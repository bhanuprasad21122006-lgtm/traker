package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Task
import com.example.ui.components.UserAvatar
import com.example.ui.screens.AddEditTaskSheet
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.TaskTrackerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTrackerApp(viewModel: TaskTrackerViewModel) {
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
  val userTasks by viewModel.userTasks.collectAsStateWithLifecycle()
  val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val statusFilter by viewModel.taskStatusFilter.collectAsStateWithLifecycle()
  val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
  val authError by viewModel.authError.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  val user = currentUser
  if (user == null) {
    AuthScreen(
      allUsers = allUsers,
      authError = authError,
      isLoading = isLoading,
      onLogin = { id, pass -> viewModel.login(id, pass) },
      onSignUp = { username, email, pass, name, job, avatar, goal ->
        viewModel.signUp(username, email, pass, name, job, avatar, goal)
      },
      onQuickLogin = { u -> viewModel.quickLoginAs(u) },
      onClearError = { viewModel.clearAuthError() }
    )
    return
  }

  val streakDays = viewModel.calculateStreak(userTasks)
  val weeklyData = viewModel.calculateWeeklyData(userTasks, user.dailyTaskGoal)
  val categoryProgress = viewModel.calculateCategoryProgress(userTasks)
  val achievements = viewModel.calculateAchievements(userTasks, streakDays)

  // Sheet state for Add / Edit
  var showTaskSheet by remember { mutableStateOf(false) }
  var taskToEdit by remember { mutableStateOf<Task?>(null) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
            Text(
              text = "Task Tracker",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.setTab(NavTab.PROFILE) },
            modifier = Modifier.padding(start = 6.dp).testTag("top_bar_avatar_btn")
          ) {
            UserAvatar(avatarId = user.avatarId, size = 34.dp, borderWidth = 1.5.dp)
          }
        },
        actions = {
          // Streak pill badge
          Box(
            modifier = Modifier
              .padding(end = 12.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFF97316).copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak",
                tint = Color(0xFFF97316),
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = "$streakDays d",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF97316),
                fontSize = 12.sp
              )
            }
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        NavigationBarItem(
          selected = selectedTab == NavTab.DASHBOARD,
          onClick = { viewModel.setTab(NavTab.DASHBOARD) },
          icon = {
            Icon(
              imageVector = if (selectedTab == NavTab.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
              contentDescription = "Dashboard"
            )
          },
          label = { Text("Dashboard", fontWeight = if (selectedTab == NavTab.DASHBOARD) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_dashboard")
        )

        NavigationBarItem(
          selected = selectedTab == NavTab.TASKS,
          onClick = { viewModel.setTab(NavTab.TASKS) },
          icon = {
            Icon(
              imageVector = if (selectedTab == NavTab.TASKS) Icons.Filled.Assignment else Icons.Outlined.Assignment,
              contentDescription = "Tasks"
            )
          },
          label = { Text("Tasks", fontWeight = if (selectedTab == NavTab.TASKS) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_tasks")
        )

        NavigationBarItem(
          selected = selectedTab == NavTab.ANALYTICS,
          onClick = { viewModel.setTab(NavTab.ANALYTICS) },
          icon = {
            Icon(
              imageVector = if (selectedTab == NavTab.ANALYTICS) Icons.Filled.Insights else Icons.Outlined.Insights,
              contentDescription = "Analytics"
            )
          },
          label = { Text("Analytics", fontWeight = if (selectedTab == NavTab.ANALYTICS) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_analytics")
        )

        NavigationBarItem(
          selected = selectedTab == NavTab.PROFILE,
          onClick = { viewModel.setTab(NavTab.PROFILE) },
          icon = {
            Icon(
              imageVector = if (selectedTab == NavTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
              contentDescription = "Profile"
            )
          },
          label = { Text("Profile", fontWeight = if (selectedTab == NavTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_profile")
        )
      }
    },
    floatingActionButton = {
      if (selectedTab == NavTab.DASHBOARD || selectedTab == NavTab.TASKS) {
        FloatingActionButton(
          onClick = {
            taskToEdit = null
            showTaskSheet = true
          },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = Color.White,
          shape = CircleShape,
          modifier = Modifier
            .size(56.dp)
            .testTag("fab_add_task")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Task",
            modifier = Modifier.size(26.dp)
          )
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (selectedTab) {
        NavTab.DASHBOARD -> {
          DashboardScreen(
            user = user,
            tasks = userTasks,
            streakDays = streakDays,
            weeklyData = weeklyData,
            categoryProgress = categoryProgress,
            onToggleTask = { t -> viewModel.toggleTaskCompletion(t) },
            onToggleSubtask = { t, subId -> viewModel.toggleSubtask(t, subId) },
            onEditTask = { t ->
              taskToEdit = t
              showTaskSheet = true
            },
            onDeleteTask = { id -> viewModel.deleteTask(id) },
            onNavigateToTasks = { viewModel.setTab(NavTab.TASKS) },
            onNavigateToAnalytics = { viewModel.setTab(NavTab.ANALYTICS) },
            onAddTaskClick = {
              taskToEdit = null
              showTaskSheet = true
            }
          )
        }

        NavTab.TASKS -> {
          TasksScreen(
            tasks = filteredTasks,
            rawTasks = userTasks,
            currentStatusFilter = statusFilter,
            currentCategoryFilter = categoryFilter,
            searchQuery = searchQuery,
            sortOption = sortOption,
            onStatusFilterChange = { sf -> viewModel.setStatusFilter(sf) },
            onCategoryFilterChange = { cf -> viewModel.setCategoryFilter(cf) },
            onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
            onSortOptionChange = { s -> viewModel.setSortOption(s) },
            onToggleTask = { t -> viewModel.toggleTaskCompletion(t) },
            onToggleSubtask = { t, subId -> viewModel.toggleSubtask(t, subId) },
            onEditTask = { t ->
              taskToEdit = t
              showTaskSheet = true
            },
            onDeleteTask = { id -> viewModel.deleteTask(id) },
            onClearCompleted = { viewModel.clearCompletedTasks() },
            onAddTaskClick = {
              taskToEdit = null
              showTaskSheet = true
            }
          )
        }

        NavTab.ANALYTICS -> {
          AnalyticsScreen(
            user = user,
            tasks = userTasks,
            streakDays = streakDays,
            weeklyData = weeklyData,
            categoryProgress = categoryProgress,
            achievements = achievements
          )
        }

        NavTab.PROFILE -> {
          ProfileScreen(
            user = user,
            allUsers = allUsers,
            tasks = userTasks,
            onUpdateProfile = { name, role, goal, focus, avatar ->
              viewModel.updateProfile(name, role, goal, focus, avatar)
            },
            onQuickLogin = { u -> viewModel.quickLoginAs(u) },
            onClearCompleted = { viewModel.clearCompletedTasks() },
            onLogout = { viewModel.logout() }
          )
        }
      }
    }

    // Add / Edit Modal Bottom Sheet
    if (showTaskSheet) {
      AddEditTaskSheet(
        taskToEdit = taskToEdit,
        sheetState = sheetState,
        onDismiss = {
          scope.launch { sheetState.hide() }.invokeOnCompletion {
            showTaskSheet = false
            taskToEdit = null
          }
        },
        onSaveTask = { title, desc, cat, prio, due, focus, subs ->
          if (taskToEdit != null) {
            val updated = taskToEdit!!.copy(
              title = title,
              description = desc,
              category = cat,
              priority = prio,
              dueDate = due,
              estimatedMinutes = focus,
              subtasksRaw = com.example.data.model.Subtask.listToString(subs)
            )
            viewModel.updateTask(updated)
          } else {
            viewModel.createTask(title, desc, cat, prio, due, focus, subs)
          }
          scope.launch { sheetState.hide() }.invokeOnCompletion {
            showTaskSheet = false
            taskToEdit = null
          }
        }
      )
    }
  }
}
