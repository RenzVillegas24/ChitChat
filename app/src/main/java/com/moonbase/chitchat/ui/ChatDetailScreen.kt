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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    // Track keyboard height for responsive scrolling
    val imeHeight = WindowInsets.ime.getBottom(density)
    val previousImeHeight = remember { mutableStateOf(0) }
    
    // Track previous scroll position
    val previousScrollOffset = remember { mutableStateOf(0) }

    // Handle keyboard responsive scrolling
    LaunchedEffect(imeHeight) {
        val imeHeightDelta = imeHeight - previousImeHeight.value
        
        if (imeHeightDelta != 0) {
            // Get current scroll position
            val currentScrollOffset = listState.firstVisibleItemScrollOffset
            val currentScrollIndex = listState.firstVisibleItemIndex
            
            // Calculate new scroll position relative to keyboard height change
            val newScrollOffset = currentScrollOffset + imeHeightDelta
            
            // Scroll immediately to maintain position relative to keyboard
            coroutineScope.launch {
                listState.scrollToItem(
                    index = currentScrollIndex,
                    scrollOffset = maxOf(0, newScrollOffset)
                )
            }
        }
        
        previousImeHeight.value = imeHeight
    }

    // Scroll to bottom when new messages are added
    LaunchedEffect(chatData.messages.size) {
        if (chatData.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatData.messages.size - 1)
        }
    }

    // Use WindowInsets for proper keyboard handling
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Messages List - This will be compressed by keyboard instead of being pushed up
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                top = statusBarPadding + 80.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatData.messages) { message ->
                MessageBubble(
                    message = message,
                    isGroup = chatData.isGroup
                )
            }
            item {
                Spacer(modifier = Modifier
                    .windowInsetsPadding(WindowInsets.ime)
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)))
            }
        }

        // Top App Bar with Haze Effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(statusBarPadding + 120.dp)
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



        // Message Input Area with Haze Effect - This will move smoothly with keyboard
        Box(
            modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .offset(y = WindowInsets.navigationBars.only(WindowInsetsSides.Bottom).asPaddingValues().calculateBottomPadding())
            .windowInsetsPadding(WindowInsets.ime)
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
            .requiredHeight(statusBarPadding + 120.dp)
            .hazeSource(hazeState, zIndex = 1f)
            .hazeEffect(
                hazeState,
                style = HazeStyle(
                backgroundColor = MaterialTheme.colorScheme.background,
                tints = listOf(HazeTint(MaterialTheme.colorScheme.background)),
                blurRadius = 8.dp,
                noiseFactor = HazeDefaults.noiseFactor
                ),
            ) {
                progressive = HazeProgressive.verticalGradient(
                easing = EaseOutSine,
                startIntensity = 0f,
                endIntensity = 0.5f
                )
            }
        )

        // Message Input Area - This will move smoothly with keyboard
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .windowInsetsPadding(WindowInsets.ime)
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
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
                )
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
                    keyboardOptions = KeyboardOptions(
                        imeAction = if (messageText.isNotBlank()) ImeAction.Send else ImeAction.Default
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (messageText.isNotBlank()) {
                                // Handle send message
                                messageText = ""
                                keyboardController?.hide()
                            }
                        }
                    )
                )

                // Send button
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            // Handle send message
                            messageText = ""
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .padding(4.dp),
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = if (messageText.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
