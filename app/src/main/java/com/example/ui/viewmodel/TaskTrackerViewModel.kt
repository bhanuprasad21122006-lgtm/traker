package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Subtask
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import com.example.data.model.User
import com.example.data.repository.TaskTrackerRepository
import com.example.ui.components.CategoryProgressItem
import com.example.ui.components.DayCompletionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class NavTab(val label: String) {
  DASHBOARD("Dashboard"),
  TASKS("Tasks"),
  ANALYTICS("Analytics"),
  PROFILE("Profile")
}

enum class TaskStatusFilter(val label: String) {
  ALL("All"),
  TODAY("Today"),
  UPCOMING("Upcoming"),
  OVERDUE("Overdue"),
  COMPLETED("Completed")
}

enum class TaskSortOption(val label: String) {
  DUE_DATE("Due Date"),
  PRIORITY("Priority"),
  TITLE("Title"),
  NEWEST("Newest")
}

data class AchievementBadge(
  val id: String,
  val title: String,
  val description: String,
  val emoji: String,
  val isUnlocked: Boolean,
  val progress: String
)

class TaskTrackerViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: TaskTrackerRepository

  val allUsers: StateFlow<List<User>>

  private val _currentUserId = MutableStateFlow<Long?>(null)
  val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

  val currentUser: StateFlow<User?>

  private val _selectedTab = MutableStateFlow(NavTab.DASHBOARD)
  val selectedTab: StateFlow<NavTab> = _selectedTab.asStateFlow()

  private val _taskStatusFilter = MutableStateFlow(TaskStatusFilter.ALL)
  val taskStatusFilter: StateFlow<TaskStatusFilter> = _taskStatusFilter.asStateFlow()

  private val _categoryFilter = MutableStateFlow<TaskCategory?>(null)
  val categoryFilter: StateFlow<TaskCategory?> = _categoryFilter.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _sortOption = MutableStateFlow(TaskSortOption.DUE_DATE)
  val sortOption: StateFlow<TaskSortOption> = _sortOption.asStateFlow()

  private val _authError = MutableStateFlow<String?>(null)
  val authError: StateFlow<String?> = _authError.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  // Raw user tasks
  val userTasks: StateFlow<List<Task>>

  // Filtered & Sorted tasks for display
  val filteredTasks: StateFlow<List<Task>>

  init {
    val db = AppDatabase.getDatabase(application)
    repository = TaskTrackerRepository(db.userDao(), db.taskDao())

    allUsers = repository.allUsers.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    currentUser = _currentUserId.flatMapLatest { id ->
      if (id != null) repository.getUserById(id) else flowOf(null)
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = null
    )

    userTasks = _currentUserId.flatMapLatest { id ->
      if (id != null) repository.getTasksForUser(id) else flowOf(emptyList())
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    filteredTasks = combine(
      userTasks,
      _taskStatusFilter,
      _categoryFilter,
      _searchQuery,
      _sortOption
    ) { tasks, statusFilter, catFilter, query, sort ->
      val now = System.currentTimeMillis()
      val calNow = Calendar.getInstance().apply { timeInMillis = now }

      var result = tasks

      // Search Query filter
      if (query.isNotBlank()) {
        val q = query.trim().lowercase()
        result = result.filter {
          it.title.lowercase().contains(q) || it.description.lowercase().contains(q)
        }
      }

      // Category filter
      if (catFilter != null) {
        result = result.filter { it.category == catFilter }
      }

      // Status filter
      result = when (statusFilter) {
        TaskStatusFilter.ALL -> result
        TaskStatusFilter.TODAY -> result.filter { task ->
          val calTask = Calendar.getInstance().apply { timeInMillis = task.dueDate }
          calNow.get(Calendar.YEAR) == calTask.get(Calendar.YEAR) &&
            calNow.get(Calendar.DAY_OF_YEAR) == calTask.get(Calendar.DAY_OF_YEAR)
        }
        TaskStatusFilter.UPCOMING -> result.filter { task ->
          task.dueDate > now && !task.isCompleted
        }
        TaskStatusFilter.OVERDUE -> result.filter { task ->
          task.dueDate < now && !task.isCompleted && !isSameDay(task.dueDate, now)
        }
        TaskStatusFilter.COMPLETED -> result.filter { it.isCompleted }
      }

      // Sort
      when (sort) {
        TaskSortOption.DUE_DATE -> result.sortedWith(
          compareBy<Task> { it.isCompleted }
            .thenBy { it.dueDate }
            .thenByDescending { it.priority.weight }
        )
        TaskSortOption.PRIORITY -> result.sortedWith(
          compareBy<Task> { it.isCompleted }
            .thenByDescending { it.priority.weight }
            .thenBy { it.dueDate }
        )
        TaskSortOption.TITLE -> result.sortedWith(
          compareBy<Task> { it.isCompleted }
            .thenBy { it.title.lowercase() }
        )
        TaskSortOption.NEWEST -> result.sortedWith(
          compareBy<Task> { it.isCompleted }
            .thenByDescending { it.createdAt }
        )
      }
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    // Seed sample data and auto-select primary demo user
    viewModelScope.launch {
      repository.seedInitialDataIfEmpty()
      allUsers.collect { users ->
        if (users.isNotEmpty() && _currentUserId.value == null) {
          _currentUserId.value = users.first().id
        }
      }
    }
  }

  fun setTab(tab: NavTab) {
    _selectedTab.value = tab
  }

  fun setStatusFilter(filter: TaskStatusFilter) {
    _taskStatusFilter.value = filter
  }

  fun setCategoryFilter(category: TaskCategory?) {
    _categoryFilter.value = category
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSortOption(option: TaskSortOption) {
    _sortOption.value = option
  }

  fun clearAuthError() {
    _authError.value = null
  }

  // Auth Operations
  fun login(identifier: String, pass: String, onSuccess: () -> Unit = {}) {
    if (identifier.isBlank() || pass.isBlank()) {
      _authError.value = "Please enter both username/email and password."
      return
    }
    viewModelScope.launch {
      _isLoading.value = true
      _authError.value = null
      val result = repository.loginUser(identifier, pass)
      _isLoading.value = false
      result.onSuccess { user ->
        _currentUserId.value = user.id
        _selectedTab.value = NavTab.DASHBOARD
        onSuccess()
      }.onFailure { err ->
        _authError.value = err.message ?: "Authentication failed."
      }
    }
  }

  fun signUp(
    username: String,
    email: String,
    pass: String,
    fullName: String,
    jobTitle: String,
    avatarId: String,
    dailyGoal: Int,
    onSuccess: () -> Unit = {}
  ) {
    if (username.isBlank() || email.isBlank() || pass.isBlank() || fullName.isBlank()) {
      _authError.value = "All fields are required."
      return
    }
    if (pass.length < 4) {
      _authError.value = "Password must be at least 4 characters long."
      return
    }
    viewModelScope.launch {
      _isLoading.value = true
      _authError.value = null
      val result = repository.registerUser(
        username = username,
        email = email,
        password = pass,
        fullName = fullName,
        jobTitle = jobTitle,
        avatarId = avatarId,
        dailyTaskGoal = dailyGoal,
        focusGoalMinutes = 120
      )
      _isLoading.value = false
      result.onSuccess { user ->
        _currentUserId.value = user.id
        _selectedTab.value = NavTab.DASHBOARD
        onSuccess()
      }.onFailure { err ->
        _authError.value = err.message ?: "Account registration failed."
      }
    }
  }

  fun quickLoginAs(user: User) {
    _currentUserId.value = user.id
    _selectedTab.value = NavTab.DASHBOARD
    _authError.value = null
  }

  fun logout() {
    _currentUserId.value = null
    _authError.value = null
  }

  fun updateProfile(
    fullName: String,
    jobTitle: String,
    dailyGoal: Int,
    focusGoalMinutes: Int,
    avatarId: String
  ) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val updated = user.copy(
        fullName = fullName.trim().ifBlank { user.fullName },
        jobTitle = jobTitle.trim().ifBlank { user.jobTitle },
        dailyTaskGoal = dailyGoal.coerceIn(1, 20),
        focusGoalMinutes = focusGoalMinutes.coerceIn(15, 600),
        avatarId = avatarId
      )
      repository.updateUser(updated)
    }
  }

  // Task Operations
  fun toggleTaskCompletion(task: Task) {
    viewModelScope.launch {
      repository.toggleTaskCompletion(task)
    }
  }

  fun toggleSubtask(task: Task, subtaskId: String) {
    viewModelScope.launch {
      repository.toggleSubtask(task, subtaskId)
    }
  }

  fun createTask(
    title: String,
    description: String,
    category: TaskCategory,
    priority: TaskPriority,
    dueDate: Long,
    estimatedMinutes: Int,
    subtasks: List<Subtask>
  ) {
    val userId = _currentUserId.value ?: return
    if (title.isBlank()) return

    viewModelScope.launch {
      val newTask = Task(
        userId = userId,
        title = title.trim(),
        description = description.trim(),
        category = category,
        priority = priority,
        dueDate = dueDate,
        estimatedMinutes = estimatedMinutes,
        isCompleted = false,
        subtasksRaw = Subtask.listToString(subtasks)
      )
      repository.insertTask(newTask)
    }
  }

  fun updateTask(task: Task) {
    viewModelScope.launch {
      repository.updateTask(task)
    }
  }

  fun deleteTask(taskId: Long) {
    viewModelScope.launch {
      repository.deleteTaskById(taskId)
    }
  }

  fun clearCompletedTasks() {
    val userId = _currentUserId.value ?: return
    viewModelScope.launch {
      repository.clearCompletedTasks(userId)
    }
  }

  // Analytics & Dashboard Metrics Helpers
  fun calculateStreak(tasks: List<Task>): Int {
    if (tasks.isEmpty()) return 0
    val completedDays = tasks
      .filter { it.isCompleted && it.completedAt != null }
      .map { getDayEpoch(it.completedAt!!) }
      .distinct()
      .sortedDescending()

    if (completedDays.isEmpty()) return 0

    val todayEpoch = getDayEpoch(System.currentTimeMillis())
    val yesterdayEpoch = todayEpoch - 1

    // If no task completed today or yesterday, streak is broken
    if (completedDays.first() < yesterdayEpoch) return 0

    var streak = 0
    var expectedDay = completedDays.first()

    for (day in completedDays) {
      if (day == expectedDay) {
        streak++
        expectedDay--
      } else {
        break
      }
    }
    return streak
  }

  fun calculateWeeklyData(tasks: List<Task>, dailyTarget: Int): List<DayCompletionData> {
    val calendar = Calendar.getInstance()
    val dayNames = SimpleDateFormat("EEE", Locale.getDefault())
    val result = mutableListOf<DayCompletionData>()

    for (i in 6 downTo 0) {
      val cal = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -i)
      }
      val targetDayOfYear = cal.get(Calendar.DAY_OF_YEAR)
      val targetYear = cal.get(Calendar.YEAR)

      val completedCount = tasks.count { task ->
        if (task.isCompleted && task.completedAt != null) {
          val completedCal = Calendar.getInstance().apply { timeInMillis = task.completedAt }
          completedCal.get(Calendar.YEAR) == targetYear &&
            completedCal.get(Calendar.DAY_OF_YEAR) == targetDayOfYear
        } else false
      }

      result.add(
        DayCompletionData(
          dayLabel = dayNames.format(cal.time),
          completedCount = completedCount,
          targetCount = dailyTarget,
          isToday = i == 0
        )
      )
    }
    return result
  }

  fun calculateCategoryProgress(tasks: List<Task>): List<CategoryProgressItem> {
    return TaskCategory.values().map { category ->
      val catTasks = tasks.filter { it.category == category }
      CategoryProgressItem(
        category = category,
        total = catTasks.size,
        completed = catTasks.count { it.isCompleted }
      )
    }.filter { it.total > 0 }
  }

  fun calculateAchievements(tasks: List<Task>, streak: Int): List<AchievementBadge> {
    val completedCount = tasks.count { it.isCompleted }
    val totalFocus = tasks.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
    val highPriorityDone = tasks.count { it.isCompleted && it.priority == TaskPriority.HIGH }

    return listOf(
      AchievementBadge(
        id = "first_step",
        title = "First Step",
        description = "Complete your first task",
        emoji = "🌱",
        isUnlocked = completedCount >= 1,
        progress = "${completedCount.coerceAtMost(1)}/1"
      ),
      AchievementBadge(
        id = "streak_starter",
        title = "Streak Starter",
        description = "Maintain a 3-day completion streak",
        emoji = "🔥",
        isUnlocked = streak >= 3,
        progress = "${streak.coerceAtMost(3)}/3 days"
      ),
      AchievementBadge(
        id = "task_crusher",
        title = "Task Crusher",
        description = "Complete 5 or more total tasks",
        emoji = "🎯",
        isUnlocked = completedCount >= 5,
        progress = "${completedCount.coerceAtMost(5)}/5"
      ),
      AchievementBadge(
        id = "high_impact",
        title = "High Impact Hero",
        description = "Finish 3 high priority tasks",
        emoji = "⚡",
        isUnlocked = highPriorityDone >= 3,
        progress = "${highPriorityDone.coerceAtMost(3)}/3"
      ),
      AchievementBadge(
        id = "focus_master",
        title = "Deep Focus Master",
        description = "Log over 120 minutes of focus time",
        emoji = "⏱️",
        isUnlocked = totalFocus >= 120,
        progress = "$totalFocus/120m"
      )
    )
  }

  private fun getDayEpoch(timeMs: Long): Long {
    val cal = Calendar.getInstance().apply {
      timeInMillis = timeMs
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis / (24 * 60 * 60 * 1000L)
  }

  private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
      cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
  }
}
