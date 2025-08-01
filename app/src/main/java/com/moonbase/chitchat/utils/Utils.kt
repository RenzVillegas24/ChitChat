package com.moonbase.chitchat.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun hazeStyle(
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

fun formatLastSeenTime(lastSeenTime: LocalDateTime): String {
  val now = LocalDateTime.now()
  val minutesBetween = ChronoUnit.MINUTES.between(lastSeenTime, now)
  val hoursBetween = ChronoUnit.HOURS.between(lastSeenTime, now)
  val daysBetween = ChronoUnit.DAYS.between(lastSeenTime, now)

  return when {
    minutesBetween < 60 -> "${minutesBetween}m"
    hoursBetween < 24 -> "${hoursBetween}h"
    daysBetween < 7 -> "${daysBetween}d"
    else -> "long ago"
  }
}
