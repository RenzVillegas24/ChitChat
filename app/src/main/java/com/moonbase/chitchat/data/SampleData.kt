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
    OnlineUser("u7", "Grace", Color(0xFF607D8B)),
    OnlineUser("u8", "Henry", Color(0xFF3F51B5)),
    OnlineUser("u9", "Ivy", Color(0xFFE91E63)),
    OnlineUser("u10", "Jack", Color(0xFF009688)),
    OnlineUser("u11", "Kelly", Color(0xFFFF5722)),
    OnlineUser("u12", "Liam", Color(0xFF8BC34A)),
    OnlineUser("u13", "Maya", Color(0xFFFFEB3B)),
    OnlineUser("u14", "Noah", Color(0xFF9E9E9E)),
    OnlineUser("u15", "Olivia", Color(0xFF673AB7)),
    OnlineUser("u16", "Paul", Color(0xFFFF9800)),
    OnlineUser("u17", "Quinn", Color(0xFF4CAF50)),
    OnlineUser("u18", "Ruby", Color(0xFFE91E63)),
    OnlineUser("u19", "Sam", Color(0xFF2196F3)),
    OnlineUser("u20", "Tara", Color(0xFF9C27B0))
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
        "individual",
        isOnline = true,
        lastSeenTime = null,
        isTyping = false
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
        "group",
        isOnline = false,
        lastSeenTime = null,
        isTyping = false
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
        "individual",
        isOnline = true,
        lastSeenTime = null,
        isTyping = true // Typing + Online = Shows typing indicator
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
        "group",
        isOnline = false,
        lastSeenTime = null,
        isTyping = false
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
        "individual",
        isOnline = false,
        lastSeenTime = now.minusMinutes(21),
        isTyping = false
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
        "group",
        isOnline = false,
        lastSeenTime = null,
        isTyping = false
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
      ),
      ChatItem(
        "13",
        "Study Group",
        "Tomorrow's exam tips",
        formatDateForChat(now.minusHours(4)),
        now.minusHours(4),
        true,
        12,
        Color(0xFF673AB7),
        true,
        MessageStatus.DELIVERED,
        "group",
        isOnline = false,
        lastSeenTime = null,
        isTyping = false
      ),
      ChatItem(
        "14",
        "Emma Watson",
        "Let's catch up soon!",
        formatDateForChat(now.minusHours(6)),
        now.minusHours(6),
        false,
        1,
        Color(0xFFFF5722),
        false,
        MessageStatus.SENT,
        "individual",
        isOnline = true,
        lastSeenTime = null,
        isTyping = false
      ),
      ChatItem(
        "15",
        "Tech Meetup",
        "Next event: AI Workshop",
        formatDateForChat(now.minusDays(1)),
        now.minusDays(1),
        true,
        0,
        Color(0xFF009688),
        false,
        MessageStatus.DELIVERED,
        "group",
        isOnline = false,
        lastSeenTime = null,
        isTyping = false
      ),
      ChatItem(
        "16",
        "David Miller",
        "Thanks for the recommendation",
        formatDateForChat(now.minusDays(2)),
        now.minusDays(2),
        false,
        0,
        Color(0xFF8BC34A),
        true,
        MessageStatus.DELIVERED,
        "individual",
        isOnline = false,
        lastSeenTime = now.minusHours(5),
        isTyping = false
      ),
      ChatItem(
        "17",
        "Book Club",
        "Next book discussion",
        formatDateForChat(now.minusDays(3)),
        now.minusDays(3),
        true,
        8,
        Color(0xFFFFEB3B),
        false,
        MessageStatus.SENT,
        "group",
        isOnline = false,
        lastSeenTime = null,
        isTyping = false
      ),
      ChatItem(
        "18",
        "Lisa Parker",
        "Happy weekend!",
        formatDateForChat(now.minusDays(4)),
        now.minusDays(4),
        false,
        0,
        Color(0xFF9E9E9E),
        false,
        MessageStatus.DELIVERED,
        "individual",
        isOnline = true,
        lastSeenTime = null,
        isTyping = false
      ),
      ChatItem(
        "19",
        "Fitness Squad",
        "Morning run tomorrow?",
        formatDateForChat(now.minusDays(5)),
        now.minusDays(5),
        true,
        4,
        Color(0xFFFF9800),
        false,
        MessageStatus.DELIVERED,
        "group",
        isOnline = false,
        lastSeenTime = null,
        isTyping = false
      ),
      ChatItem(
        "20",
        "James Wilson",
        "See you at the meeting",
        formatDateForChat(now.minusWeeks(1)),
        now.minusWeeks(1),
        false,
        0,
        Color(0xFF4CAF50),
        false,
        MessageStatus.SENT,
        "individual",
        isOnline = false,
        lastSeenTime = now.minusDays(2),
        isTyping = false
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
          now.minusHours(5),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message("m2", "I'm doing great! How about you?", now.minusHours(4).minusMinutes(45), true),
        Message(
          "m3",
          "Pretty good, just working on some projects",
          now.minusHours(4).minusMinutes(30),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message(
          "m4",
          "That sounds interesting! What kind of projects?",
          now.minusHours(4).minusMinutes(15),
          true
        ),
        Message(
          "m5",
          "Mostly mobile app development. I'm learning Jetpack Compose",
          now.minusHours(4),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message(
          "m6",
          "Oh nice! I've heard good things about it. How's it going?",
          now.minusHours(3).minusMinutes(45),
          true
        ),
        Message(
          "m7",
          "It's going well! The declarative approach is really refreshing",
          now.minusHours(3).minusMinutes(30),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message(
          "m8",
          "That's great to hear! If you need any help, feel free to ask",
          now.minusHours(3).minusMinutes(15),
          true
        ),
        Message(
          "m9",
          "Thanks! I might take you up on that 😊",
          now.minusHours(3),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message("m10", "Sure, just let me know when you're free", now.minusHours(2).minusMinutes(45), true),
        Message(
          "m11",
          "Will do! By the way, are you free for lunch tomorrow?",
          now.minusHours(2).minusMinutes(30),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message("m12", "Yeah, that sounds good! What time?", now.minusHours(2).minusMinutes(15), true),
        Message("m13", "How about 12:30 PM?", now.minusHours(2), false, "John Doe", chatItem.avatarColor),
        Message("m14", "Perfect! See you then 😊", now.minusHours(1).minusMinutes(45), true),
        Message(
          "m15",
          "Actually, I just remembered I have a meeting at 12:30",
          now.minusHours(1).minusMinutes(30),
          false,
          "John Doe",
          chatItem.avatarColor
        ),
        Message("m16", "No worries! How about 1:00 PM instead?", now.minusHours(1).minusMinutes(15), true),
        Message("m17", "That works perfectly!", now.minusHours(1), false, "John Doe", chatItem.avatarColor),
        Message("m18", "Great! I know a good place downtown", now.minusMinutes(45), true),
        Message("m19", "Sounds good! Text me the address", now.minusMinutes(30), false, "John Doe", chatItem.avatarColor),
        Message("m20", "Will do! Looking forward to it", now.minusMinutes(15), true),
        Message("m21", "Me too! It's been too long", now.minusMinutes(10), false, "John Doe", chatItem.avatarColor),
        Message("m22", "Absolutely! We have a lot to catch up on", now.minusMinutes(5), true),
        Message("m23", "Definitely! See you tomorrow then", now, false, "John Doe", chatItem.avatarColor)
      )

      "2" -> listOf(
        Message(
          "g1",
          "Good morning everyone!",
          now.minusHours(6),
          false,
          "Mom",
          Color(0xFFE91E63)
        ),
        Message(
          "g2",
          "Morning Mom!",
          now.minusHours(5).minusMinutes(45),
          true
        ),
        Message(
          "g3",
          "Good morning! ☀️",
          now.minusHours(5).minusMinutes(30),
          false,
          "Dad",
          Color(0xFF2196F3)
        ),
        Message(
          "g4",
          "Don't forget about dinner tonight!",
          now.minusHours(4),
          false,
          "Mom",
          Color(0xFFE91E63)
        ),
        Message(
          "g5",
          "What time again?",
          now.minusHours(3).minusMinutes(45),
          false,
          "Dad",
          Color(0xFF2196F3)
        ),
        Message(
          "g6",
          "7 PM, as usual 😊",
          now.minusHours(3).minusMinutes(30),
          false,
          "Mom",
          Color(0xFFE91E63)
        ),
        Message("g7", "I'll be there!", now.minusHours(3).minusMinutes(15), true),
        Message("g8", "Perfect! See you all then", now.minusHours(3), false, "Mom", Color(0xFFE91E63)),
        Message(
          "g9",
          "Should I bring anything?",
          now.minusHours(2).minusMinutes(30),
          false,
          "Sister",
          Color(0xFF4CAF50)
        ),
        Message("g10", "Just yourself! I've got everything covered", now.minusHours(2), false, "Mom", Color(0xFFE91E63)),
        Message("g11", "Can't wait! What's for dinner?", now.minusHours(1).minusMinutes(30), true),
        Message("g12", "It's a surprise! 🤐", now.minusHours(1), false, "Mom", Color(0xFFE91E63)),
        Message("g13", "Ooh mysterious! I love surprises", now.minusMinutes(45), false, "Sister", Color(0xFF4CAF50)),
        Message("g14", "You'll find out soon enough! 😄", now.minusMinutes(30), false, "Mom", Color(0xFFE91E63)),
        Message("g15", "I'm getting hungry just thinking about it", now.minusMinutes(15), false, "Dad", Color(0xFF2196F3))
      )

      "3" -> listOf(
        Message(
          "s1",
          "Hi! Thanks for helping me with the project",
          now.minusHours(3),
          false,
          "Sarah Wilson",
          chatItem.avatarColor
        ),
        Message("s2", "No problem! Happy to help", now.minusHours(2).minusMinutes(45), true),
        Message(
          "s3",
          "I'm still struggling with the implementation",
          now.minusHours(2).minusMinutes(30),
          false,
          "Sarah Wilson",
          chatItem.avatarColor
        ),
        Message("s4", "Which part specifically?", now.minusHours(2).minusMinutes(15), true),
        Message(
          "s5",
          "The data binding part",
          now.minusHours(2),
          false,
          "Sarah Wilson",
          chatItem.avatarColor
        ),
        Message("s6", "Ah I see, let me send you a code example", now.minusHours(1).minusMinutes(45), true),
        Message("s7", "That would be amazing! Thank you so much", now.minusHours(1).minusMinutes(30), false, "Sarah Wilson", chatItem.avatarColor),
        Message("s8", "Here's a simple example: [code snippet]", now.minusHours(1).minusMinutes(15), true),
        Message("s9", "Oh wow, that makes so much more sense now!", now.minusHours(1), false, "Sarah Wilson", chatItem.avatarColor),
        Message("s10", "Great! Let me know if you need more help", now.minusMinutes(45), true),
        Message("s11", "Will do! You're a lifesaver 🙌", now.minusMinutes(30), false, "Sarah Wilson", chatItem.avatarColor),
        Message("s12", "Thanks for the help!", now.minusMinutes(15), false, "Sarah Wilson", chatItem.avatarColor)
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
      messages = sampleMessages,
      isOnline = when (chatItem.id) {
        "1" -> true // John Doe is online
        "3" -> true // Sarah Wilson is offline
        "5" -> false // Mike Johnson was online 41 minutes ago
        "7" -> false // Alice Brown was last seen 1 hour ago
        "9" -> true // Mom is online
        else -> kotlin.random.Random.nextBoolean() // Random for others
      },
      lastSeenTime = when (chatItem.id) {
        "5" -> now.minusMinutes(41L) // Mike Johnson was last seen 41 minutes ago
        "7" -> now.minusHours(1L) // Alice Brown was last seen 1 hour ago
        "3" -> now.minusHours(25L) // Sarah Wilson was last seen more than 24 hours ago
        "11" -> now.minusDays(2L) // Old Friend was last seen days ago
        else -> if (kotlin.random.Random.nextBoolean()) now.minusMinutes(kotlin.random.Random.nextInt(1, 1440).toLong()) else null
      },
      isTyping = when (chatItem.id) {
        "3" -> true // Sarah Wilson is typing (and online, so shows typing indicator)
        "5" -> true // Mike Johnson is offline but typing (should show offline - no indicator)
        else -> false
      }
    )
  }
}
