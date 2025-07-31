package com.moonbase.chitchat.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moonbase.chitchat.data.ChatItem

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChatListItem(
  chat: ChatItem,
  isFirst: Boolean = false,
  isLast: Boolean = false,
  showFavoriteIcon: Boolean = false,
  onChatClick: (ChatItem) -> Unit = {},
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope
) {
  val shape = when {
    isFirst && isLast -> RoundedCornerShape(16.dp)
    isFirst -> RoundedCornerShape(
      topStart = 16.dp,
      topEnd = 16.dp,
      bottomEnd = 5.dp,
      bottomStart = 5.dp
    )

    isLast -> RoundedCornerShape(
      bottomStart = 16.dp,
      bottomEnd = 16.dp,
      topEnd = 5.dp,
      topStart = 5.dp
    )

    else -> RoundedCornerShape(5.dp)
  }

  with(sharedTransitionScope) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(vertical = 1.dp)
        .sharedElement(
          sharedContentState = rememberSharedContentState(key = "chat-${chat.id}"),
          animatedVisibilityScope = animatedVisibilityScope,
          boundsTransform = { _, _ ->
            tween(durationMillis = 500, easing = FastOutSlowInEasing)
          }
        )
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
