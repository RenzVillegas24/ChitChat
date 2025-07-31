package com.moonbase.chitchat.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
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
