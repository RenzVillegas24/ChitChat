package com.moonbase.chitchat.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.chrisbanes.haze.*
import com.moonbase.chitchat.data.ChatItem
import com.moonbase.chitchat.data.SampleData
import com.moonbase.chitchat.ui.components.*
import com.moonbase.chitchat.utils.groupChatsByDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ChatListScreen(
  onChatClick: (ChatItem) -> Unit,
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope
) {
  var selectedFilters by remember { mutableStateOf(setOf("all")) }
  val hazeState = remember { HazeState() }
  val onlineUsers = remember { SampleData.getOnlineUsers() }
  val sampleChats = remember { SampleData.getSampleChats() }

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
  val topPad = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  // track scroll position
  val listState = rememberLazyListState()
  val isAtTop =
    remember { derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 } }

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
        Spacer(modifier = Modifier.height(topPad + 64.dp))
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
            onChatClick = onChatClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
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
            onChatClick = { onChatClick(chat) },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
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

    // Custom Haze Top App Bar
    AnimatedHazeTopAppBar(
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
      },
      hazeState = hazeState,
      isScrolled = !isAtTop.value,
      statusBarPadding = topPad
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
