package com.moonbase.chitchat.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moonbase.chitchat.data.OnlineUser

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
  UserAvatarWithStatus(
    name = user.name,
    avatarColor = user.avatarColor,
    isOnline = user.isOnline,
    lastSeenTime = null, // OnlineUser doesn't have lastSeenTime, so it's always online or offline
    isTyping = false,
    showName = true,
    size = 56.dp,
    onClick = { /* User profile action */ }
  )
}

enum class UserStatus {
  ONLINE,
  LAST_SEEN,
  TYPING,
  OFFLINE
}

@Composable
fun UserAvatarWithStatus(
  name: String,
  avatarColor: Color,
  modifier: Modifier = Modifier,
  isOnline: Boolean = false,
  lastSeenTime: String? = null, // Formatted time like "4h", "41m"
  isTyping: Boolean = false,
  showName: Boolean = false,
  size: Dp = 48.dp,
  onClick: (() -> Unit)? = null
) {
  val clickableModifier = if (onClick != null) {
    Modifier.clickable { onClick() }
  } else {
    Modifier
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier.then(clickableModifier)
  ) {
    Box {
      // Avatar
      Box(
        modifier = Modifier
          .size(size)
          .clip(CircleShape)
          .background(avatarColor),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = name.first().uppercase(),
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = (size.value * 0.35f).sp
        )
      }

      // Status indicator
      when {
        // Priority 1: If typing and online, show typing indicator
        isTyping && isOnline -> {
          // Typing indicator - pulsing animation
          val infiniteTransition = rememberInfiniteTransition(label = "typing")
          val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
              animation = tween(1000, easing = EaseInOut),
              repeatMode = RepeatMode.Reverse
            ),
            label = "typing_alpha"
          )
          
          Box(
            modifier = Modifier
              .size((size.value * 0.3f).dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
              .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
              .align(Alignment.BottomEnd)
          )
        }
        
        // Priority 2: If online but not typing, show online indicator
        isOnline -> {
          // Online indicator - secondary color dot
          Box(
            modifier = Modifier
              .size((size.value * 0.28f).dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.tertiary)
              .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
              .align(Alignment.BottomEnd)
          )
        }
        
        // Priority 3: If not online but has last seen time, show last seen indicator
        lastSeenTime != null -> {
          // Last seen indicator - gray dot with time text inside
          val minSize = (size.value * 0.42f).dp
          val textLength = lastSeenTime.length
          val dynamicWidth = when {
            textLength <= 2 -> minSize
            textLength <= 3 -> (size.value * 0.58f).dp
            else -> (size.value * 0.5f).dp
          }

          Box(
            modifier = Modifier
              .height(minSize)
              .width(dynamicWidth)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
              .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
              .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
          ) {
            // Time text inside the indicator
            if (size >= 48.dp) { // Only show time text for larger avatars
              Text(
                text = lastSeenTime,
                fontSize = 6.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = 6.sp
              )
            }
          }
        }
        // Priority 4: Offline users with no last seen time - show nothing
      }
    }

    if (showName) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = name.split(" ").first(),
        fontSize = (size.value * 0.21f).sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
      )
    }
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
