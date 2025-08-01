package com.moonbase.chitchat.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moonbase.chitchat.data.ChatItem
import com.moonbase.chitchat.utils.formatLastSeenTime

/**
 * Custom shape for dynamic corner radius
 */
private fun createCustomShape(cornerRadiusState: CornerRadiusState): Shape {
    return RoundedCornerShape(
        topStart = cornerRadiusState.topStart,
        topEnd = cornerRadiusState.topEnd,
        bottomStart = cornerRadiusState.bottomStart,
        bottomEnd = cornerRadiusState.bottomEnd
    )
}

/**
 * Dynamic ChatListItem component with advanced state management
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DynamicChatListItem(
    chat: ChatItem,
    itemIndex: Int,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    showFavoriteIcon: Boolean = false,
    stateManager: ChatListStateManager,
    onChatClick: (ChatItem) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope
) {
    // Interaction states
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    // Update state manager with hover state
    LaunchedEffect(isHovered) {
        if (isHovered) {
            stateManager.setHoveredItem(itemIndex)
        } else if (stateManager.hoveredIndex == itemIndex) {
            stateManager.clearHover()
        }
    }

    // Get current states
    val isItemHovered = stateManager.isItemHovered(itemIndex)
    val isItemPressed = stateManager.isItemPressed(itemIndex)
    val isItemSelected = stateManager.isItemSelected(itemIndex)

    // Calculate corner radius state
    val cornerRadiusState = stateManager.getCornerRadiusState(
        itemIndex = itemIndex,
        isFirst = isFirst,
        isLast = isLast,
        isHovered = isItemHovered
    )

    // Animated values
    val animatedTopStartRadius by animateDpAsState(
        targetValue = cornerRadiusState.topStart,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "topStartRadius"
    )
    val animatedTopEndRadius by animateDpAsState(
        targetValue = cornerRadiusState.topEnd,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "topEndRadius"
    )
    val animatedBottomStartRadius by animateDpAsState(
        targetValue = cornerRadiusState.bottomStart,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "bottomStartRadius"
    )
    val animatedBottomEndRadius by animateDpAsState(
        targetValue = cornerRadiusState.bottomEnd,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "bottomEndRadius"
    )

    // Scale animation for press state
    val scale by animateFloatAsState(
        targetValue = if (isItemPressed) 0.975f else 1.0f,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "scale"
    )

    // Create dynamic shape with animated corner radius
    val animatedShape = remember(
        animatedTopStartRadius,
        animatedTopEndRadius,
        animatedBottomStartRadius,
        animatedBottomEndRadius
    ) {
        RoundedCornerShape(
            topStart = animatedTopStartRadius,
            topEnd = animatedTopEndRadius,
            bottomStart = animatedBottomStartRadius,
            bottomEnd = animatedBottomEndRadius
        )
    }

    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 1.dp)
                .scale(scale)
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = "chat-${chat.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        tween(durationMillis = 500, easing = FastOutSlowInEasing)
                    }
                )
                .hoverable(interactionSource = interactionSource)
                .pointerInput(itemIndex) {
                    detectTapGestures(
                        onPress = {
                            stateManager.setPressedItem(itemIndex)
                            try {
                                awaitRelease()
                            } finally {
                                stateManager.clearPressed()
                            }
                        },
                        onTap = {
                            stateManager.selectItem(itemIndex)
                            onChatClick(chat)
                        }
                    )
                },
            shape = animatedShape,
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
                    if (chat.isGroup) {
                        // For group chats, use simple avatar without status
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(chat.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        // For individual chats, use UserAvatarWithStatus
                        val lastSeenTimeFormatted = if (chat.lastSeenTime != null) {
                            formatLastSeenTime(chat.lastSeenTime)
                        } else null
                        
                        UserAvatarWithStatus(
                            name = chat.name,
                            avatarColor = chat.avatarColor,
                            isOnline = chat.isOnline,
                            lastSeenTime = lastSeenTimeFormatted,
                            isTyping = chat.isTyping,
                            showName = false,
                            size = 48.dp
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
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                icon,
                                contentDescription = "Channel",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
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
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (showFavoriteIcon) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Favorite",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFFFD700)
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Message status indicator
                            when (chat.messageStatus) {
                                com.moonbase.chitchat.data.MessageStatus.SENT -> {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Sent",
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                com.moonbase.chitchat.data.MessageStatus.DELIVERED -> {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Delivered",
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (chat.unreadCount > 0) {
                            Badge(
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Container component that manages multiple DynamicChatListItem components
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DynamicChatListContainer(
    chats: List<ChatItem>,
    showFavoriteIcon: Boolean = false,
    onChatClick: (ChatItem) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope
) {
    val stateManager = rememberChatListStateManager(itemCount = chats.size)

    chats.forEachIndexed { index, chat ->
        DynamicChatListItem(
            chat = chat,
            itemIndex = index,
            isFirst = index == 0,
            isLast = index == chats.size - 1,
            showFavoriteIcon = showFavoriteIcon,
            stateManager = stateManager,
            onChatClick = onChatClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}
