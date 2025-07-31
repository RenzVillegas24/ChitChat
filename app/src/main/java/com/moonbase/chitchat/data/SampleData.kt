package com.moonbase.chitchat.data

import androidx.compose.ui.graphics.Color
import com.moonbase.chitchat.utils.formatDateForChat
import java.time.LocalDateTime

object SampleData {

  fun getOnlineUsers(): List<OnlineUser> = listOf(
    OnlineUser("u1", "Alice", Color(0xFF9C27B0)),
    OnlineUser("u2", "Bob", Color(0xFF2196F3)),
    OnlineUser("u3", "Charlie", Color(0xFF4CAF50)),
    OnlineUser("u4", "Diana", Color(0xFFFF9800)),
    OnlineUser("u5", "Eve", Color(0xFFF44336)),
    OnlineUser("u6", "Frank", Color(0xFF795548)),
    OnlineUser("u7", "Grace", Color(0xFF607D8B))
  )

  fun getSampleChats(): List<ChatItem> {
    val now = LocalDateTime.now()
    return listOf(
      ChatItem(
        "1",
        "John Doe",
        "Hey, how are you?",
        "2:30 PM",
        now.minusHours(2),
        false,
        2,
        Color.Blue,
        true,
        MessageStatus.DELIVERED,
        "individual"
      ),
      ChatItem(
        "2",
        "Family Group",
        "Mom: Dinner at 7 PM",
        "1:45 PM",
        now.minusHours(3),
        true,
        5,
        Color.Green,
        true,
        MessageStatus.SENT,
        "group"
      ),
      ChatItem(
        "3",
        "Sarah Wilson",
        "Thanks for the help!",
        "12:20 PM",
        now.minusHours(5),
        false,
        0,
        Color(0xFF9C27B0),
        false,
        MessageStatus.DELIVERED,
        "individual"
      ),
      ChatItem(
        "4",
        "Work Team",
        "Meeting tomorrow at 10 AM",
        formatDateForChat(now.minusDays(1).minusHours(2)),
        now.minusDays(1).minusHours(2),
        true,
        1,
        Color(0xFFFF9800),
        true,
        MessageStatus.DELIVERED,
        "group"
      ),
      ChatItem(
        "5",
        "Mike Johnson",
        "Sure, see you then",
        formatDateForChat(now.minusDays(1).minusHours(5)),
        now.minusDays(1).minusHours(5),
        false,
        0,
        Color.Red,
        false,
        MessageStatus.SENT,
        "individual"
      ),
      ChatItem(
        "6",
        "College Friends",
        "Anyone up for movies?",
        formatDateForChat(now.minusDays(2)),
        now.minusDays(2),
        true,
        3,
        Color(0xFF009688),
        false,
        MessageStatus.DELIVERED,
        "group"
      ),
      ChatItem(
        "7",
        "Alice Brown",
        "Happy birthday! 🎉",
        formatDateForChat(now.minusDays(8)),
        now.minusDays(8),
        false,
        1,
        Color(0xFFE91E63),
        false,
        MessageStatus.DELIVERED,
        "individual"
      ),
      ChatItem(
        "8",
        "Gaming Squad",
        "New game tonight?",
        formatDateForChat(now.minusDays(10)),
        now.minusDays(10),
        true,
        7,
        Color.Cyan,
        false,
        MessageStatus.SENT,
        "group"
      ),
      ChatItem(
        "9",
        "Mom",
        "Don't forget to call",
        formatDateForChat(now.minusDays(15)),
        now.minusDays(15),
        false,
        0,
        Color(0xFF8BC34A),
        true,
        MessageStatus.DELIVERED,
        "individual"
      ),
      ChatItem(
        "10",
        "Project Team",
        "Final presentation ready",
        formatDateForChat(now.minusMonths(1).minusDays(5)),
        now.minusMonths(1).minusDays(5),
        true,
        0,
        Color(0xFF3F51B5),
        false,
        MessageStatus.DELIVERED,
        "group"
      ),
      ChatItem(
        "11",
        "Old Friend",
        "Long time no see!",
        formatDateForChat(now.minusYears(1).minusMonths(2)),
        now.minusYears(1).minusMonths(2),
        false,
        0,
        Color(0xFF795548),
        false,
        MessageStatus.SENT,
        "individual"
      ),
      ChatItem(
        "12",
        "Support Channel",
        "Update available",
        formatDateForChat(now.minusYears(1).minusMonths(6)),
        now.minusYears(1).minusMonths(6),
        false,
        0,
        Color(0xFF607D8B),
        false,
        MessageStatus.DELIVERED,
        "channel"
      )
    )
  }

  // Helper function to create sample messages for chat detail by ID
  fun createChatDetailDataById(chatId: String): ChatDetailData {
    val sampleChats = getSampleChats()
    val chatItem = sampleChats.find { it.id == chatId } ?: sampleChats.first()
    return createChatDetailData(chatItem)
  }

  // Helper function to create sample messages for chat detail
  fun createChatDetailData(chatItem: ChatItem): ChatDetailData {
    val now = LocalDateTime.now()

    val sampleMessages = when (chatItem.id) {
      "1" -> listOf(
        Message(
          "m1",
          "Hey, how are you?",
          now.minusHours(3),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message("m2", "I'm doing great! How about you?", now.minusHours(2).minusMinutes(45), true),
        Message(
          "m3",
          "Pretty good, just working on some projects",
          now.minusHours(2).minusMinutes(30),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message(
          "m4",
          "That sounds interesting! What kind of projects?",
          now.minusHours(2).minusMinutes(15),
          true
        ),
        Message(
          "m5",
          "Mostly mobile app development. I'm learning Jetpack Compose",
          now.minusHours(2),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message(
          "m6",
          "Oh nice! I've heard good things about it. How's it going?",
          now.minusHours(1).minusMinutes(45),
          true
        ),
        Message(
          "m7",
          "It's going well! The declarative approach is really refreshing",
          now.minusHours(1).minusMinutes(30),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message(
          "m8",
          "That's great to hear! If you need any help, feel free to ask",
          now.minusHours(1).minusMinutes(15),
          true
        ),
        Message(
          "m9",
          "Thanks! I might take you up on that 😊",
          now.minusHours(1),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message("m10", "Sure, just let me know when you're free", now.minusMinutes(45), true),
        Message(
          "m11",
          "Will do! By the way, are you free for lunch tomorrow?",
          now.minusMinutes(30),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message("m12", "Yeah, that sounds good! What time?", now.minusMinutes(15), true),
        Message("m13", "How about 12:30 PM?", now, false, "John Doe", chatItem.avatarColor),
        Message("m14", "Perfect! See you then 😊", now.plusMinutes(15), true)
      )

      "2" -> listOf(
        Message(
          "g1",
          "Don't forget about dinner tonight!",
          now.minusHours(4),
          false,
          "Mom",
          Color(0xFFE91E63)
        ),
        Message(
          "g2",
          "What time again?",
          now.minusHours(3).minusMinutes(45),
          false,
          "Dad",
          Color(0xFF2196F3)
        ),
        Message(
          "g3",
          "7 PM, as usual 😊",
          now.minusHours(3).minusMinutes(30),
          false,
          "Mom",
          Color(0xFFE91E63)
        ),
        Message("g4", "I'll be there!", now.minusHours(3).minusMinutes(15), true),
        Message("g5", "Perfect! See you all then", now.minusHours(3), false, "Mom", Color(0xFFE91E63))
      )

      else -> listOf(
        Message("default1", "Hello!", now.minusHours(1), false, chatItem.name, chatItem.avatarColor),
        Message("default2", "Hi there!", now.minusMinutes(30), true),
        Message(
          "default3",
          chatItem.lastMessage,
          now.minusMinutes(15),
          false,
          chatItem.name,
          chatItem.avatarColor
        )
      )
    }

    return ChatDetailData(
      id = chatItem.id,
      name = chatItem.name,
      isGroup = chatItem.isGroup,
      avatarColor = chatItem.avatarColor,
      messages = sampleMessages
    )
  }
}
