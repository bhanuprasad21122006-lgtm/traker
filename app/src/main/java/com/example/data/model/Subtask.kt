package com.example.data.model

import java.util.UUID

data class Subtask(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val isDone: Boolean = false
) {
  fun serialize(): String {
    // Escape delimiter if present
    val safeTitle = title.replace(":::", "---").replace("|||", "---")
    return "$id:::$safeTitle:::$isDone"
  }

  companion object {
    fun deserialize(raw: String): Subtask? {
      val parts = raw.split(":::")
      if (parts.size >= 3) {
        return Subtask(
          id = parts[0],
          title = parts[1],
          isDone = parts[2].toBooleanStrictOrNull() ?: false
        )
      }
      return null
    }

    fun listToString(list: List<Subtask>): String {
      return list.joinToString("|||") { it.serialize() }
    }

    fun stringToList(raw: String): List<Subtask> {
      if (raw.isBlank()) return emptyList()
      return raw.split("|||").mapNotNull { deserialize(it) }
    }
  }
}
