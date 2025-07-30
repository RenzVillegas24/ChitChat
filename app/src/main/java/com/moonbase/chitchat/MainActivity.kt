package com.moonbase.chitchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.moonbase.chitchat.ui.theme.ChitChatTheme
import com.moonbase.chitchat.ui.ChatDetailScreen
import com.moonbase.chitchat.ui.ChatDetailData
import com.moonbase.chitchat.ui.Message
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
    val destination: String = "individual" // "individual", "group",  "channel"
)

data class GroupedChatItems(
    val dateGroup: String,
    val chats: List<ChatItem>
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            ChitChatTheme {
                ChitChatApp()
            }
        }
    }
}

fun hazeStyle (
    colorScheme: Color,
    blurRadius: Dp = 16.dp,
    noiseFactor: Float = HazeDefaults.noiseFactor
): HazeStyle {
    return HazeStyle(
        backgroundColor = colorScheme,
        tints = listOf(HazeTint(colorScheme)),
        blurRadius = blurRadius,
        noiseFactor = noiseFactor
    )
}

fun formatDateForChat(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val daysBetween = ChronoUnit.DAYS.between(dateTime.toLocalDate(), now.toLocalDate())
    val yearsBetween = ChronoUnit.YEARS.between(dateTime, now)

    return when {
        daysBetween == 0L -> dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
        daysBetween == 1L -> "Yesterday"
        daysBetween <= 7L -> dateTime.format(DateTimeFormatter.ofPattern("EEEE"))
        yearsBetween == 0L -> dateTime.format(DateTimeFormatter.ofPattern("MMM d"))
        else -> dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChitChatApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "chat_list",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable("chat_list") {
            ChatListScreen(
                onChatClick = { chat ->
                    navController.navigate("chat_detail/${chat.id}")
                }
            )
        }
        
        composable(
            "chat_detail/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val chatDetailData = createChatDetailDataById(chatId)
            ChatDetailScreen(
                chatData = chatDetailData,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

// Helper function to create sample messages for chat detail by ID
fun createChatDetailDataById(chatId: String): ChatDetailData {
    val sampleChats = listOf(
        ChatItem("1", "John Doe", "Hey, how are you?", "2:30 PM", LocalDateTime.now().minusHours(2), false, 2, Color.Blue, true, MessageStatus.DELIVERED, "individual"),
        ChatItem("2", "Family Group", "Mom: Dinner at 7 PM", "1:45 PM", LocalDateTime.now().minusHours(3), true, 5, Color.Green, true, MessageStatus.SENT, "group"),
        ChatItem("3", "Sarah Wilson", "Thanks for the help!", "12:20 PM", LocalDateTime.now().minusHours(5), false, 0, Color(0xFF9C27B0), false, MessageStatus.DELIVERED, "individual"),
        ChatItem("4", "Work Team", "Meeting tomorrow at 10 AM", formatDateForChat(LocalDateTime.now().minusDays(1).minusHours(2)), LocalDateTime.now().minusDays(1).minusHours(2), true, 1, Color(0xFFFF9800), true, MessageStatus.DELIVERED, "group"),
        ChatItem("5", "Mike Johnson", "Sure, see you then", formatDateForChat(LocalDateTime.now().minusDays(1).minusHours(5)), LocalDateTime.now().minusDays(1).minusHours(5), false, 0, Color.Red, false, MessageStatus.SENT, "individual"),
        ChatItem("6", "College Friends", "Anyone up for movies?", formatDateForChat(LocalDateTime.now().minusDays(2)), LocalDateTime.now().minusDays(2), true, 3, Color(0xFF009688), false, MessageStatus.DELIVERED, "group")
    )
    
    val chatItem = sampleChats.find { it.id == chatId } ?: sampleChats.first()
    return createChatDetailData(chatItem)
}

// Helper function to create sample messages for chat detail
fun createChatDetailData(chatItem: ChatItem): ChatDetailData {
    val now = LocalDateTime.now()

    val sampleMessages = when (chatItem.id) {
        "1" -> listOf(
            Message("m1", "Hey, how are you?", now.minusHours(3), false, "John Doe", chatItem.avatarColor),
            Message("m2", "I'm doing great! How about you?", now.minusHours(2).minusMinutes(45), true),
            Message("m3", "Pretty good, just working on some projects", now.minusHours(2).minusMinutes(30), false, "John Doe", chatItem.avatarColor),
            Message("m4", "That sounds interesting! What kind of projects?", now.minusHours(2).minusMinutes(15), true),
            Message("m5", "Mostly mobile app development. I'm learning Jetpack Compose", now.minusHours(2), false, "John Doe", chatItem.avatarColor),
            Message("m6", "Oh nice! I've heard good things about it. How's it going?", now.minusHours(1).minusMinutes(45), true),
            Message("m7", "It's going well! The declarative approach is really refreshing", now.minusHours(1).minusMinutes(30), false, "John Doe", chatItem.avatarColor),
            Message("m8", "That's great to hear! If you need any help, feel free to ask", now.minusHours(1).minusMinutes(15), true),
            Message("m9", "Thanks! I might take you up on that 😊", now.minusHours(1), false, "John Doe", chatItem.avatarColor),
            Message("m10", "Sure, just let me know when you're free", now.minusMinutes(45), true),
            Message("m11", "Will do! By the way, are you free for lunch tomorrow?", now.minusMinutes(30), false, "John Doe", chatItem.avatarColor),
            Message("m12", "Yeah, that sounds good! What time?", now.minusMinutes(15), true),
            Message("m13", "How about 12:30 PM?", now, false, "John Doe", chatItem.avatarColor),
            Message("m14", "Perfect! See you then 😊", now.plusMinutes(15), true)
        )
        "2" -> listOf(
            Message("g1", "Don't forget about dinner tonight!", now.minusHours(4), false, "Mom", Color(0xFFE91E63)),
            Message("g2", "What time again?", now.minusHours(3).minusMinutes(45), false, "Dad", Color(0xFF2196F3)),
            Message("g3", "7 PM, as usual 😊", now.minusHours(3).minusMinutes(30), false, "Mom", Color(0xFFE91E63)),
            Message("g4", "I'll be there!", now.minusHours(3).minusMinutes(15), true),
            Message("g5", "Perfect! See you all then", now.minusHours(3), false, "Mom", Color(0xFFE91E63))
        )
        else -> listOf(
            Message("default1", "Hello!", now.minusHours(1), false, chatItem.name, chatItem.avatarColor),
            Message("default2", "Hi there!", now.minusMinutes(30), true),
            Message("default3", chatItem.lastMessage, now.minusMinutes(15), false, chatItem.name, chatItem.avatarColor)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatClick: (ChatItem) -> Unit
) {
    var selectedFilters by remember { mutableStateOf(setOf("all")) }
    val hazeState = remember { HazeState() }
    val onlineUsers = remember {
        listOf(
            OnlineUser("u1", "Alice", Color(0xFF9C27B0)),
            OnlineUser("u2", "Bob", Color(0xFF2196F3)),
            OnlineUser("u3", "Charlie", Color(0xFF4CAF50)),
            OnlineUser("u4", "Diana", Color(0xFFFF9800)),
            OnlineUser("u5", "Eve", Color(0xFFF44336)),
            OnlineUser("u6", "Frank", Color(0xFF795548)),
            OnlineUser("u7", "Grace", Color(0xFF607D8B))
        )
    }

    val now = LocalDateTime.now()
    val sampleChats = remember {
        listOf(
            ChatItem("1", "John Doe", "Hey, how are you?", "2:30 PM", now.minusHours(2), false, 2, Color.Blue, true, MessageStatus.DELIVERED, "individual"),
            ChatItem("2", "Family Group", "Mom: Dinner at 7 PM", "1:45 PM", now.minusHours(3), true, 5, Color.Green, true, MessageStatus.SENT, "group"),
            ChatItem("3", "Sarah Wilson", "Thanks for the help!", "12:20 PM", now.minusHours(5), false, 0, Color(0xFF9C27B0), false, MessageStatus.DELIVERED, "individual"),
            ChatItem("4", "Work Team", "Meeting tomorrow at 10 AM", formatDateForChat(now.minusDays(1).minusHours(2)), now.minusDays(1).minusHours(2), true, 1, Color(0xFFFF9800), true, MessageStatus.DELIVERED, "group"),
            ChatItem("5", "Mike Johnson", "Sure, see you then", formatDateForChat(now.minusDays(1).minusHours(5)), now.minusDays(1).minusHours(5), false, 0, Color.Red, false, MessageStatus.SENT, "individual"),
            ChatItem("6", "College Friends", "Anyone up for movies?", formatDateForChat(now.minusDays(2)), now.minusDays(2), true, 3, Color(0xFF009688), false, MessageStatus.DELIVERED, "group"),
            ChatItem("7", "Alice Brown", "Happy birthday! 🎉", formatDateForChat(now.minusDays(8)), now.minusDays(8), false, 1, Color(0xFFE91E63), false, MessageStatus.DELIVERED, "individual"),
            ChatItem("8", "Gaming Squad", "New game tonight?", formatDateForChat(now.minusDays(10)), now.minusDays(10), true, 7, Color.Cyan, false, MessageStatus.SENT, "group"),
            ChatItem("9", "Mom", "Don't forget to call", formatDateForChat(now.minusDays(15)), now.minusDays(15), false, 0, Color(0xFF8BC34A), true, MessageStatus.DELIVERED, "individual"),
            ChatItem("10", "Project Team", "Final presentation ready", formatDateForChat(now.minusMonths(1).minusDays(5)), now.minusMonths(1).minusDays(5), true, 0, Color(0xFF3F51B5), false, MessageStatus.DELIVERED, "group"),
            ChatItem("11", "Old Friend", "Long time no see!", formatDateForChat(now.minusYears(1).minusMonths(2)), now.minusYears(1).minusMonths(2), false, 0, Color(0xFF795548), false, MessageStatus.SENT, "individual"),
            ChatItem("12", "Support Channel", "Update available", formatDateForChat(now.minusYears(1).minusMonths(6)), now.minusYears(1).minusMonths(6), false, 0, Color(0xFF607D8B), false, MessageStatus.DELIVERED, "channel")
        )
    }

    val filteredChats = remember(selectedFilters, sampleChats) {
        sampleChats.filter { chat ->
            when {
                selectedFilters.contains("all") -> true
                selectedFilters.contains("unread") && chat.unreadCount > 0 -> true
                selectedFilters.contains("group") && chat.isGroup -> true
                selectedFilters.contains("chat") && !chat.isGroup -> true
                else -> false
            }
        }
    }

    val favoriteChats = filteredChats.filter { it.isFavorite }
    val regularChats = filteredChats.filter { !it.isFavorite }
    var topPad = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    // track scroll position
    val listState = rememberLazyListState()
    val isAtTop = remember { derivedStateOf { listState.firstVisibleItemIndex  == 0 && listState.firstVisibleItemScrollOffset == 0 } }

    // animate blur radius smoothly
    val hazeBlurTop by animateDpAsState(
        targetValue = if (isAtTop.value) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    val groupedChats = remember(regularChats) {
        groupChatsByDate(regularChats)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Chat List with sections
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .hazeSource(state = hazeState)
            ) {
            item {
                Spacer(modifier = Modifier.height(topPad + 48.dp))
            }

            // Online Users Section
            item {
                OnlineUsersSection(onlineUsers)
            }

            // Favorites Section
            if (favoriteChats.isNotEmpty()) {
                item {
                    SectionHeader("Favorites")
                }

                itemsIndexed(favoriteChats) { index, chat ->
                    ChatListItem(
                        chat = chat,
                        isFirst = index == 0,
                        isLast = index == favoriteChats.size - 1,
                        showFavoriteIcon = true,
                        onChatClick = onChatClick
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Grouped Messages
            groupedChats.forEach { group ->
                item {
                    SectionHeader(group.dateGroup)
                }

                itemsIndexed(group.chats) { index, chat ->
                    ChatListItem(
                        chat = chat,
                        isFirst = index == 0,
                        isLast = index == group.chats.size - 1,
                        onChatClick = { onChatClick(chat) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }


            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Haze Effect box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(topPad + 120.dp)
                .hazeEffect(
                    hazeState,
                    style = hazeStyle(
                        colorScheme = MaterialTheme.colorScheme.background,
                        blurRadius = hazeBlurTop)
                ) {
                    progressive = HazeProgressive.verticalGradient(
                        easing = EaseOutSine,
                        startIntensity = 1f,
                        endIntensity = 0f,
                    )
                }
        )

        // Top App Bar
        TopAppBar(
            colors = TopAppBarDefaults.largeTopAppBarColors(Color.Transparent),
            title = {
                Text(
                    text = "ChitChat",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = { /* Search action */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = { /* Menu action */ }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            }
        )


        // Floating Action Button
        FloatingActionButton(
            onClick = { /* New message action */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 64.dp, end = 16.dp)
                .zIndex(1f),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Message")
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .zIndex(1f),
        ) {
            // Filter Chips
            FilterChipsRow(
                selectedFilters = selectedFilters,
                onFilterChanged = { filter ->
                    selectedFilters = when {
                        filter == "all" -> setOf("all")
                        filter == "group" && selectedFilters.contains("chat") -> {
                            (selectedFilters - "chat" + filter).takeIf { "all" !in it } ?: setOf(filter)
                        }
                        filter == "chat" && selectedFilters.contains("group") -> {
                            (selectedFilters - "group" + filter).takeIf { "all" !in it } ?: setOf(filter)
                        }
                        selectedFilters.contains(filter) -> {
                            val newFilters = selectedFilters - filter
                            if (newFilters.isEmpty()) setOf("all") else newFilters
                        }
                        else -> {
                            val newFilters = selectedFilters - "all" + filter
                            newFilters.takeIf { it.isNotEmpty() } ?: setOf("all")
                        }
                    }
                }
            )
        }

    }
}

@Composable
fun OnlineUsersSection(onlineUsers: List<OnlineUser>) {
    Column(modifier = Modifier.padding(horizontal = 0.dp, vertical = 16.dp)) {
        SectionHeader("Online Users")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(modifier = Modifier.width(4.dp))
            }
            items(onlineUsers) { user ->
                OnlineUserItem(user)
            }
            item {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun OnlineUserItem(user: OnlineUser) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* User profile action */ }
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(user.avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            // Online indicator
            if (user.isOnline) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = user.name.split(" ").first(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 16.dp, top = 0.dp, bottom = 8.dp, end = 16.dp)
    )
}

@Composable
fun FilterChipsRow(
    selectedFilters: Set<String>,
    onFilterChanged: (String) -> Unit
) {
    val filters = listOf("all", "unread", "group", "chat")

    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                onClick = { onFilterChanged(filter) },
                label = {
                    Text(
                        text = filter.replaceFirstChar { it.uppercase() },
                        fontSize = 14.sp
                    )
                },
                selected = selectedFilters.contains(filter),
                modifier = Modifier.height(32.dp)
            )
        }
    }
}

@Composable
fun ChatListItem(
    chat: ChatItem,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    showFavoriteIcon: Boolean = false,
    onChatClick: (ChatItem) -> Unit = {}
) {
    val shape = when {
        isFirst && isLast -> RoundedCornerShape(16.dp)
        isFirst -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 5.dp, bottomStart = 5.dp)
        isLast -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topEnd = 5.dp, topStart = 5.dp)
        else -> RoundedCornerShape(5.dp)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 1.dp)
            .clickable { onChatClick(chat) },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with destination indicator
            Box {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(chat.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (chat.isGroup) "G" else chat.name.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Destination indicator
                val destinationIcon = when (chat.destination) {
                    "channel" -> Icons.Default.Notifications
                    else -> null
                }

                destinationIcon?.let { icon ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = chat.destination,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Chat Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = chat.name,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )

                        if (showFavoriteIcon) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Favorite",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Message status indicator (only for outgoing messages)
                        if (chat.unreadCount == 0) { // Assuming outgoing messages have no unread count
                            when (chat.messageStatus) {
                                MessageStatus.SENT -> {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Sent",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                MessageStatus.DELIVERED -> {
                                    // Double check for delivered
                                    Row {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Delivered",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp).offset(x = 2.dp)
                                        )
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Delivered",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp).offset(x = (-2).dp)
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = chat.time,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.lastMessage,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    if (chat.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun ChitChatPreview() {
    ChitChatTheme {
        ChitChatApp()
    }
}