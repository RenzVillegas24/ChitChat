package com.moonbase.chitchat.data

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

data class OnlineUser(
  val id: String,
  val name: String,
  val avatarColor: Color = Color.Blue,
  val isOnline: Boolean = true
)

enum class MessageStatus {
  SENT,      // One check
  DELIVERED  // Double check
}

data class ChatItem(
  val id: String,
  val name: String,
  val lastMessage: String,
  val time: String,
  val dateTime: LocalDateTime,
  val isGroup: Boolean,
  val unreadCount: Int = 0,
  val avatarColor: Color = Color.Blue,
  val isFavorite: Boolean = false,
  val messageStatus: MessageStatus = MessageStatus.DELIVERED,
  val destination: String = "individual", // "individual", "group",  "channel"
  val isOnline: Boolean = false,
  val lastSeenTime: LocalDateTime? = null,
  val isTyping: Boolean = false
)

data class GroupedChatItems(
  val dateGroup: String,
  val chats: List<ChatItem>
)

data class Message(
  val id: String,
  val text: String,
  val timestamp: LocalDateTime,
  val isFromUser: Boolean,
  val senderName: String? = null,
  val senderColor: Color = Color.Blue
)

data class ChatDetailData(
  val id: String,
  val name: String,
  val isGroup: Boolean,
  val avatarColor: Color,
  val messages: List<Message>,
  val isOnline: Boolean = false,
  val lastSeenTime: LocalDateTime? = null,
  val isTyping: Boolean = false
)
