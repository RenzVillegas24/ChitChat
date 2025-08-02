package com.moonbase.chitchat.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.*
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import dev.chrisbanes.haze.*
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import com.moonbase.chitchat.ui.components.AnimatedHazeTopAppBar
import com.moonbase.chitchat.ui.components.HazeTopAppBarDefaults
import com.moonbase.chitchat.ui.components.UserAvatarWithStatus
import com.moonbase.chitchat.data.Message
import com.moonbase.chitchat.data.ChatDetailData
import com.moonbase.chitchat.utils.formatLastSeenTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ChatDetailScreen(
	chatData: ChatDetailData,
	onBackClick: () -> Unit,
	modifier: Modifier = Modifier,
	sharedTransitionScope: SharedTransitionScope,
	animatedVisibilityScope: AnimatedVisibilityScope
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
	val isAtBottom = remember {
		derivedStateOf {
			listState.canScrollForward.not()
		}
	}

	val hazeBlurBottom by animateDpAsState(
		targetValue = if (isAtBottom.value) 0.dp else 8.dp,
		animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
	)

	with(sharedTransitionScope) {
		Card(
			modifier = modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
				.sharedElement(
					sharedContentState = rememberSharedContentState(key = "chat-${chatData.id}"),
					animatedVisibilityScope = animatedVisibilityScope,
					boundsTransform = { _, _ ->
						tween(durationMillis = 500, easing = FastOutSlowInEasing)
					}
				)
		) {

			Box {
				// Messages List - This will be compressed by keyboard instead of being pushed up
				LazyColumn(
					state = listState,
					modifier = Modifier
						.fillMaxSize()
						.background(MaterialTheme.colorScheme.background)
						.hazeSource(state = hazeState)
						.padding(horizontal = 16.dp),
					contentPadding = PaddingValues(
						top = statusBarPadding + HazeTopAppBarDefaults.ChatHeight + 16.dp, // Match custom app bar height + padding
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

				// Custom Haze Top App Bar
				AnimatedHazeTopAppBar(
					title = {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally,
							modifier = Modifier
								.fillMaxWidth()
								.clickable { /* Open chat info */ }
						) {
							if (chatData.isGroup) {
								// For group chats, show a simple avatar without status
								Box(
									modifier = Modifier
										.size(40.dp)
										.clip(CircleShape)
										.background(chatData.avatarColor),
									contentAlignment = Alignment.Center
								) {
									Text(
										text = "G",
										fontWeight = FontWeight.Bold,
										fontSize = 16.sp
									)
								}
							} else {
								// For individual chats, use the new UserAvatarWithStatus component
								val lastSeenTimeFormatted = chatData.lastSeenTime?.let { lastSeen ->
									val now = LocalDateTime.now()
									val hoursBetween = ChronoUnit.HOURS.between(lastSeen, now)
									if (hoursBetween <= 24) formatLastSeenTime(lastSeen) else null
								}

								UserAvatarWithStatus(
									name = chatData.name,
									avatarColor = chatData.avatarColor,
									isOnline = chatData.isOnline,
									lastSeenTime = lastSeenTimeFormatted,
									isTyping = chatData.isTyping,
									showName = false,
									size = 48.dp
								)
							}

							Spacer(modifier = Modifier.height(4.dp))

							// Chat Name
							Text(
								text = chatData.name,
								fontSize = 18.sp,
								fontWeight = FontWeight.Medium
							)

							// Group info only for group chats
							if (chatData.isGroup) {
								Text(
									text = "Group • ${chatData.messages.map { it.senderName }.distinct().size} members",
									fontSize = 12.sp,
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
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
					},
					hazeState = hazeState,
					isScrolled = !isAtTop.value,
					height = HazeTopAppBarDefaults.ChatHeight, // Custom height for chat header
					statusBarPadding = statusBarPadding,
					hazeModifier = Modifier.zIndex(3f),
					contentModifier = Modifier.zIndex(4f),
					titlePadding = PaddingValues(top = 8.dp),
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
								tints = listOf(HazeTint(MaterialTheme.colorScheme.background.copy(alpha = 0f))),
								blurRadius = hazeBlurBottom,
								noiseFactor = 0f
							),
						) {
							progressive = HazeProgressive.verticalGradient(
								easing = EaseOutSine,
								startIntensity = 0f,
								endIntensity = 1f
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
