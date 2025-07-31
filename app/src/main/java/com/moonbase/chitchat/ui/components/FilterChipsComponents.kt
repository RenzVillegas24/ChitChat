package com.moonbase.chitchat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
