package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users ORDER BY id ASC")
  fun getAllUsers(): Flow<List<User>>

  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  fun getUserById(userId: Long): Flow<User?>

  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  suspend fun getUserByIdSync(userId: Long): User?

  @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:identifier) OR LOWER(username) = LOWER(:identifier) LIMIT 1")
  suspend fun findByIdentifier(identifier: String): User?

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertUser(user: User): Long

  @Update
  suspend fun updateUser(user: User)

  @Query("DELETE FROM users WHERE id = :userId")
  suspend fun deleteUser(userId: Long)

  @Query("SELECT COUNT(*) FROM users")
  suspend fun getUserCount(): Int
}
