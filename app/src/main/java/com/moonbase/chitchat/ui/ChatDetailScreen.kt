package com.moonbase.chitchat.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.chrisbanes.haze.*
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    val messages: List<Message>
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun ChatDetailScreen(
    chatData: ChatDetailData,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val hazeState = remember { HazeState() }
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages are added
    LaunchedEffect(chatData.messages.size) {
        if (chatData.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatData.messages.size - 1)
        }
    }

    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // Track scroll position for haze effect
    val isAtTop = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    val hazeBlurTop by animateDpAsState(
        targetValue = if (isAtTop.value) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                top = topPadding + 80.dp,
                bottom = bottomPadding + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatData.messages) { message ->
                MessageBubble(
                    message = message,
                    isGroup = chatData.isGroup
                )
            }
        }

        // Top App Bar with Haze Effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(topPadding + 120.dp)
                .hazeSource(hazeState, zIndex = 1f)
                .hazeEffect(
                    hazeState,
                    style = HazeStyle(
                        backgroundColor = MaterialTheme.colorScheme.background,
                        tints = listOf(HazeTint(MaterialTheme.colorScheme.background)),
                        blurRadius = hazeBlurTop,
                        noiseFactor = HazeDefaults.noiseFactor
                    )
                ) {
                    progressive = HazeProgressive.verticalGradient(
                        easing = EaseOutSine,
                        startIntensity = 1f,
                        endIntensity = 0f,
                    )
                }
        )

        // Top App Bar Content
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Open chat info */ }
                ) {
                    // Chat Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(chatData.avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (chatData.isGroup) "G" else chatData.name.first().toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = chatData.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (chatData.isGroup) {
                            Text(
                                text = "Group • ${chatData.messages.map { it.senderName }.distinct().size} members",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Online",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Voice call */ }) {
                    Icon(Icons.Default.Call, contentDescription = "Voice Call")
                }
                IconButton(onClick = { /* Video call */ }) {
                    Icon(Icons.Default.Call, contentDescription = "Video Call")
                }
                IconButton(onClick = { /* More options */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }
        )

        // Message Input Area  with Haze Effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .requiredHeight(topPadding + 150.dp)
                .hazeEffect(
                    hazeState,
                    style = HazeStyle(
                        backgroundColor = MaterialTheme.colorScheme.background,
                        tints = listOf(HazeTint(MaterialTheme.colorScheme.background)),
                        blurRadius = 8.dp,
                    )
                ) {
                    progressive = HazeProgressive.verticalGradient(
                        easing = EaseInSine,
                        startIntensity = 0f,
                        endIntensity = 0.5f,
                    )
                }
        )

        // Message Input Area
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
                .zIndex(1f)
                .hazeSource(hazeState, zIndex = 2f)
                .clip(RoundedCornerShape(48.dp))
                .hazeEffect(
                    hazeState,
                    style = HazeStyle(
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 1f),
                        tints = listOf(HazeTint(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))),
                        blurRadius = 32.dp,
                    )
                ) {
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attachment button
                IconButton(
                    onClick = { /* Attachment */ },
                    modifier = Modifier
                        .size(60.dp)
                        .padding(4.dp)
                    ,
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Attach File",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Message input field
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f),
                    placeholder = { Text("Type a message...") },
                    maxLines = 4,
                    shape = RoundedCornerShape(32.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                )

//                // Send button
//                IconButton(
//                    onClick = {
//                        if (messageText.isNotBlank()) {
//                            // Handle send message
//                            messageText = ""
//                        }
//                    },
//                    modifier = Modifier.size(40.dp)
//                ) {
//                    Icon(
//                        Icons.AutoMirrored.Filled.Send,
//                        contentDescription = "Send Message",
//                        tint = if (messageText.isNotBlank())
//                            MaterialTheme.colorScheme.primary
//                        else
//                            MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isGroup: Boolean
) {
    val isFromUser = message.isFromUser

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start
    ) {
        // Show sender name for group chats (non-user messages)
        if (isGroup && !isFromUser && message.senderName != null) {
            Text(
                text = message.senderName,
                fontSize = 12.sp,
                color = message.senderColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(
                    start = if (isFromUser) 0.dp else 16.dp,
                    end = if (isFromUser) 16.dp else 0.dp,
                    bottom = 2.dp
                )
            )
        }

        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (isFromUser) 16.dp else 4.dp,
                topEnd = if (isFromUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isFromUser)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (isFromUser) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.timestamp.format(DateTimeFormatter.ofPattern("h:mm a")),
                    fontSize = 10.sp,
                    color = if (isFromUser)
                        Color.White.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
