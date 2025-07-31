package com.moonbase.chitchat.utils

import com.moonbase.chitchat.data.ChatItem
import com.moonbase.chitchat.data.GroupedChatItems
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun groupChatsByDate(chats: List<ChatItem>): List<GroupedChatItems> {
  val now = LocalDateTime.now()

  return chats.groupBy { chat ->
    val daysBetween = ChronoUnit.DAYS.between(chat.dateTime.toLocalDate(), now.toLocalDate())
    val weeksBetween = ChronoUnit.WEEKS.between(chat.dateTime, now)
    val monthsBetween = ChronoUnit.MONTHS.between(chat.dateTime, now)
    val yearsBetween = ChronoUnit.YEARS.between(chat.dateTime, now)

    when {
      daysBetween == 0L -> "Today"
      daysBetween == 1L -> "Yesterday"
      daysBetween <= 7L -> "This Week"
      weeksBetween == 1L -> "Last Week"
      monthsBetween == 0L -> "This Month"
      monthsBetween == 1L -> "Last Month"
      yearsBetween == 0L -> "This Year"
      yearsBetween == 1L -> "Last Year"
      else -> "Older"
    }
  }.map { (dateGroup, chats) ->
    GroupedChatItems(dateGroup, chats.sortedByDescending { it.dateTime })
  }.sortedBy { group ->
    when (group.dateGroup) {
      "Today" -> 0
      "Yesterday" -> 1
      "This Week" -> 2
      "Last Week" -> 3
      "This Month" -> 4
      "Last Month" -> 5
      "This Year" -> 6
      "Last Year" -> 7
      else -> 8
    }
  }
}
