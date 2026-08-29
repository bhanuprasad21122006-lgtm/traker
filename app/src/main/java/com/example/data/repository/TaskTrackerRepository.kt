package com.example.data.repository

import com.example.data.db.TaskDao
import com.example.data.db.UserDao
import com.example.data.model.Subtask
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import com.example.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.Calendar

class TaskTrackerRepository(
  private val userDao: UserDao,
  private val taskDao: TaskDao
) {
  val allUsers: Flow<List<User>> = userDao.getAllUsers()

  fun getUserById(userId: Long): Flow<User?> = userDao.getUserById(userId)

  fun getTasksForUser(userId: Long): Flow<List<Task>> = taskDao.getTasksForUser(userId)

  suspend fun registerUser(
    username: String,
    email: String,
    password: String,
    fullName: String,
    jobTitle: String = "Productivity Enthusiast",
    avatarId: String = "avatar_1",
    dailyTaskGoal: Int = 5,
    focusGoalMinutes: Int = 120
  ): Result<User> = withContext(Dispatchers.IO) {
    val cleanUsername = username.trim().lowercase()
    val cleanEmail = email.trim().lowercase()

    val existingUser = userDao.findByIdentifier(cleanUsername) ?: userDao.findByIdentifier(cleanEmail)
    if (existingUser != null) {
      return@withContext Result.failure(IllegalArgumentException("An account with this username or email already exists."))
    }

    val passwordHash = hashPassword(password)
    val newUser = User(
      username = cleanUsername,
      email = cleanEmail,
      passwordHash = passwordHash,
      fullName = fullName.trim(),
      jobTitle = jobTitle.trim().ifEmpty { "Productivity Enthusiast" },
      avatarId = avatarId,
      dailyTaskGoal = dailyTaskGoal.coerceIn(1, 20),
      focusGoalMinutes = focusGoalMinutes.coerceIn(15, 600)
    )
    val newId = userDao.insertUser(newUser)
    val created = newUser.copy(id = newId)

    // Seed welcoming starter tasks for new users
    seedTasksForNewUser(newId)

    Result.success(created)
  }

  suspend fun loginUser(identifier: String, password: String): Result<User> = withContext(Dispatchers.IO) {
    val cleanIdentifier = identifier.trim().lowercase()
    val user = userDao.findByIdentifier(cleanIdentifier)
      ?: return@withContext Result.failure(IllegalArgumentException("No account found with this username or email."))

    val passwordHash = hashPassword(password)
    if (user.passwordHash != passwordHash) {
      return@withContext Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
    }

    Result.success(user)
  }

  suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
    userDao.updateUser(user)
  }

  suspend fun insertTask(task: Task): Long = withContext(Dispatchers.IO) {
    taskDao.insertTask(task)
  }

  suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
    taskDao.updateTask(task)
  }

  suspend fun deleteTask(task: Task) = withContext(Dispatchers.IO) {
    taskDao.deleteTask(task)
  }

  suspend fun deleteTaskById(taskId: Long) = withContext(Dispatchers.IO) {
    taskDao.deleteTaskById(taskId)
  }

  suspend fun clearCompletedTasks(userId: Long) = withContext(Dispatchers.IO) {
    taskDao.clearCompletedTasks(userId)
  }

  suspend fun toggleTaskCompletion(task: Task): Task = withContext(Dispatchers.IO) {
    val updatedCompleted = !task.isCompleted
    val updatedTask = task.copy(
      isCompleted = updatedCompleted,
      completedAt = if (updatedCompleted) System.currentTimeMillis() else null
    )
    taskDao.updateTask(updatedTask)
    updatedTask
  }

  suspend fun toggleSubtask(task: Task, subtaskId: String): Task = withContext(Dispatchers.IO) {
    val updatedSubtasks = task.subtaskList.map { subtask ->
      if (subtask.id == subtaskId) {
        subtask.copy(isDone = !subtask.isDone)
      } else {
        subtask
      }
    }
    val allDone = updatedSubtasks.isNotEmpty() && updatedSubtasks.all { it.isDone }
    val updatedTask = task.copy(
      subtasksRaw = Subtask.listToString(updatedSubtasks),
      isCompleted = if (allDone) true else task.isCompleted,
      completedAt = if (allDone && task.completedAt == null) System.currentTimeMillis() else task.completedAt
    )
    taskDao.updateTask(updatedTask)
    updatedTask
  }

  suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
    if (userDao.getUserCount() > 0) return@withContext

    val alexHash = hashPassword("password123")
    val alex = User(
      username = "alexchen",
      email = "alex.chen@example.com",
      passwordHash = alexHash,
      fullName = "Alex Chen",
      jobTitle = "Senior Product Designer",
      avatarId = "avatar_1",
      dailyTaskGoal = 6,
      focusGoalMinutes = 180
    )
    val alexId = userDao.insertUser(alex)

    val mayaHash = hashPassword("password123")
    val maya = User(
      username = "mayapatel",
      email = "maya.patel@example.com",
      passwordHash = mayaHash,
      fullName = "Maya Patel",
      jobTitle = "Android Software Engineer",
      avatarId = "avatar_2",
      dailyTaskGoal = 5,
      focusGoalMinutes = 150
    )
    val mayaId = userDao.insertUser(maya)

    val jordanHash = hashPassword("password123")
    val jordan = User(
      username = "jordanlee",
      email = "jordan.lee@example.com",
      passwordHash = jordanHash,
      fullName = "Jordan Lee",
      jobTitle = "Grad Student & Researcher",
      avatarId = "avatar_3",
      dailyTaskGoal = 4,
      focusGoalMinutes = 120
    )
    val jordanId = userDao.insertUser(jordan)

    seedRealisticTasksForUser(alexId)
    seedRealisticTasksForUser(mayaId)
    seedRealisticTasksForUser(jordanId)
  }

  private suspend fun seedTasksForNewUser(userId: Long) {
    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance()

    val initialTasks = listOf(
      Task(
        userId = userId,
        title = "Complete onboarding & setup dashboard",
        description = "Configure daily task goals, personalize avatar, and explore tracking tools.",
        category = TaskCategory.PERSONAL,
        priority = TaskPriority.HIGH,
        dueDate = now + 2 * 3600 * 1000L,
        estimatedMinutes = 15,
        isCompleted = true,
        completedAt = now - 1800 * 1000L,
        subtasksRaw = Subtask.listToString(
          listOf(
            Subtask(title = "Pick custom avatar", isDone = true),
            Subtask(title = "Set daily target to 5 tasks", isDone = true),
            Subtask(title = "Review priority categories", isDone = true)
          )
        )
      ),
      Task(
        userId = userId,
        title = "Plan weekly high-priority deliverables",
        description = "Outline key milestones and set estimated focus time for each task item.",
        category = TaskCategory.WORK,
        priority = TaskPriority.HIGH,
        dueDate = now + 4 * 3600 * 1000L,
        estimatedMinutes = 45,
        isCompleted = false,
        subtasksRaw = Subtask.listToString(
          listOf(
            Subtask(title = "List top 3 must-dos", isDone = false),
            Subtask(title = "Schedule deep focus blocks", isDone = false)
          )
        )
      ),
      Task(
        userId = userId,
        title = "30-minute cardio & stretch workout",
        description = "Evening interval run or core mobility session to hit daily wellness goal.",
        category = TaskCategory.HEALTH,
        priority = TaskPriority.MEDIUM,
        dueDate = now + 8 * 3600 * 1000L,
        estimatedMinutes = 30,
        isCompleted = false
      )
    )

    initialTasks.forEach { taskDao.insertTask(it) }
  }

  private suspend fun seedRealisticTasksForUser(userId: Long) {
    val now = System.currentTimeMillis()
    val dayMillis = 24 * 60 * 60 * 1000L

    val sampleTasks = listOf(
      // Completed Today
      Task(
        userId = userId,
        title = "Review Q3 Design System Tokens",
        description = "Verify color contrast ratios and elevation tokens across M3 components.",
        category = TaskCategory.WORK,
        priority = TaskPriority.HIGH,
        dueDate = now - 2 * 3600 * 1000L,
        estimatedMinutes = 45,
        isCompleted = true,
        completedAt = now - 3 * 3600 * 1000L,
        subtasksRaw = Subtask.listToString(
          listOf(
            Subtask(title = "Check light & dark contrast tokens", isDone = true),
            Subtask(title = "Export typography styles", isDone = true)
          )
        )
      ),
      Task(
        userId = userId,
        title = "Morning 5K run & hydration log",
        description = "Maintain 7-day health streak with 5K morning pace session.",
        category = TaskCategory.HEALTH,
        priority = TaskPriority.MEDIUM,
        dueDate = now - 5 * 3600 * 1000L,
        estimatedMinutes = 35,
        isCompleted = true,
        completedAt = now - 6 * 3600 * 1000L
      ),
      Task(
        userId = userId,
        title = "Read Chapter 4: Distributed State Systems",
        description = "Take structured notes on event sourcing and local offline caching.",
        category = TaskCategory.STUDY,
        priority = TaskPriority.MEDIUM,
        dueDate = now - 1 * 3600 * 1000L,
        estimatedMinutes = 40,
        isCompleted = true,
        completedAt = now - 1 * 3600 * 1000L
      ),
      // Active Today
      Task(
        userId = userId,
        title = "Finalize Product Dashboard UX Wireframes",
        description = "Prepare interactive prototype slides for cross-functional sprint review.",
        category = TaskCategory.WORK,
        priority = TaskPriority.HIGH,
        dueDate = now + 3 * 3600 * 1000L,
        estimatedMinutes = 60,
        isCompleted = false,
        subtasksRaw = Subtask.listToString(
          listOf(
            Subtask(title = "Complete velocity chart component", isDone = true),
            Subtask(title = "Link interactive navigation states", isDone = false),
            Subtask(title = "Export Figma shareable presentation", isDone = false)
          )
        )
      ),
      Task(
        userId = userId,
        title = "Budget & Expense Reconciliation",
        description = "Review monthly subscriptions and categorize utility expenses.",
        category = TaskCategory.FINANCE,
        priority = TaskPriority.LOW,
        dueDate = now + 6 * 3600 * 1000L,
        estimatedMinutes = 25,
        isCompleted = false
      ),
      // Past Completed Tasks (for 7-day analytics chart)
      Task(
        userId = userId,
        title = "Sprint retrospective and action items",
        category = TaskCategory.WORK,
        priority = TaskPriority.HIGH,
        dueDate = now - 1 * dayMillis,
        estimatedMinutes = 45,
        isCompleted = true,
        completedAt = now - 1 * dayMillis
      ),
      Task(
        userId = userId,
        title = "Grocery restocking for meal prep",
        category = TaskCategory.PERSONAL,
        priority = TaskPriority.LOW,
        dueDate = now - 1 * dayMillis,
        estimatedMinutes = 30,
        isCompleted = true,
        completedAt = now - 1 * dayMillis
      ),
      Task(
        userId = userId,
        title = "Compose architecture diagram for new API",
        category = TaskCategory.CREATIVE,
        priority = TaskPriority.HIGH,
        dueDate = now - 2 * dayMillis,
        estimatedMinutes = 50,
        isCompleted = true,
        completedAt = now - 2 * dayMillis
      ),
      Task(
        userId = userId,
        title = "Yoga mobility & posture session",
        category = TaskCategory.HEALTH,
        priority = TaskPriority.MEDIUM,
        dueDate = now - 2 * dayMillis,
        estimatedMinutes = 30,
        isCompleted = true,
        completedAt = now - 2 * dayMillis
      ),
      Task(
        userId = userId,
        title = "Refactor database migration scripts",
        category = TaskCategory.WORK,
        priority = TaskPriority.HIGH,
        dueDate = now - 3 * dayMillis,
        estimatedMinutes = 60,
        isCompleted = true,
        completedAt = now - 3 * dayMillis
      ),
      Task(
        userId = userId,
        title = "Kotlin Coroutines Deep Dive webinar",
        category = TaskCategory.STUDY,
        priority = TaskPriority.MEDIUM,
        dueDate = now - 4 * dayMillis,
        estimatedMinutes = 60,
        isCompleted = true,
        completedAt = now - 4 * dayMillis
      ),
      Task(
        userId = userId,
        title = "Clean studio desk & cable management",
        category = TaskCategory.PERSONAL,
        priority = TaskPriority.LOW,
        dueDate = now - 5 * dayMillis,
        estimatedMinutes = 20,
        isCompleted = true,
        completedAt = now - 5 * dayMillis
      ),
      // Upcoming Tasks
      Task(
        userId = userId,
        title = "User Research Interviews (Cohort B)",
        description = "Conduct 4 qualitative user sessions exploring mobile notification fatigue.",
        category = TaskCategory.WORK,
        priority = TaskPriority.HIGH,
        dueDate = now + 1 * dayMillis + 3600 * 1000L,
        estimatedMinutes = 90,
        isCompleted = false
      ),
      Task(
        userId = userId,
        title = "Design illustration system for empty states",
        description = "Draft playful isometric vector icons in brand pastel colors.",
        category = TaskCategory.CREATIVE,
        priority = TaskPriority.MEDIUM,
        dueDate = now + 2 * dayMillis,
        estimatedMinutes = 45,
        isCompleted = false
      )
    )

    sampleTasks.forEach { taskDao.insertTask(it) }
  }

  private fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
  }
}
