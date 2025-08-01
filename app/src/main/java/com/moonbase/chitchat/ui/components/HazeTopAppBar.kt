package com.moonbase.chitchat.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.chrisbanes.haze.*
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

/**
 * Constants for HazeTopAppBar configuration
 */
object HazeTopAppBarDefaults {
  val Height = 56.dp
  val ChatHeight = 128.dp // For chat screens with avatars
  val GroupChatHeight = 156.dp // For group chat screens with avatars
  val DefaultBlurRadius = 24.dp
  val MinBlurRadius = 0.dp
  val DefaultVerticalAlignment = Alignment.Top
  val DefaultNavigationIconPadding = PaddingValues(0.dp)
  val DefaultTitlePadding = PaddingValues(horizontal = 8.dp)
  val DefaultActionsPadding = PaddingValues(0.dp)
  val DefaultPadding = PaddingValues(8.dp)
}

/**
 * Custom TopAppBar with integrated Haze effect
 *
 * @param title The title content of the app bar
 * @param modifier Modifier to be applied to the app bar
 * @param navigationIcon Optional navigation icon (usually back button)
 * @param actions Row of action icons/buttons
 * @param hazeState The HazeState for the blur effect
 * @param blurRadius The blur radius for the haze effect
 * @param height The height of the app bar (default is 56.dp)
 * @param backgroundColor The background color of the app bar
 * @param statusBarPadding Additional padding for status bar
 * @param titleVerticalAlignment Vertical alignment for the title content
 * @param navigationIconVerticalAlignment Vertical alignment for the navigation icon
 * @param actionsVerticalAlignment Vertical alignment for the actions
 * @param titlePadding Padding values for the title content
 * @param navigationIconPadding Padding values for the navigation icon
 * @param actionsPadding Padding values for the actions
 * @param padding Overall padding for the entire app bar
 */
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun HazeTopAppBar(
  title: @Composable () -> Unit,
  hazeModifier: Modifier = Modifier,
  contentModifier: Modifier = Modifier,
  navigationIcon: @Composable (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
  hazeState: HazeState,
  blurRadius: Dp = HazeTopAppBarDefaults.DefaultBlurRadius,
  height: Dp = HazeTopAppBarDefaults.Height,
  backgroundColor: Color = MaterialTheme.colorScheme.background,
  statusBarPadding: Dp = 0.dp,
  titleVerticalAlignment: Alignment.Vertical = HazeTopAppBarDefaults.DefaultVerticalAlignment,
  navigationIconVerticalAlignment: Alignment.Vertical = HazeTopAppBarDefaults.DefaultVerticalAlignment,
  actionsVerticalAlignment: Alignment.Vertical = HazeTopAppBarDefaults.DefaultVerticalAlignment,
  titlePadding: PaddingValues = HazeTopAppBarDefaults.DefaultTitlePadding,
  navigationIconPadding: PaddingValues = HazeTopAppBarDefaults.DefaultNavigationIconPadding,
  actionsPadding: PaddingValues = HazeTopAppBarDefaults.DefaultActionsPadding,
  padding: PaddingValues = HazeTopAppBarDefaults.DefaultPadding
) {
  val totalHeight = height + statusBarPadding

  // Haze background
  Box(
    modifier = hazeModifier
      .fillMaxWidth()
      .requiredHeight(totalHeight + 64.dp) // Extra height for better gradient effect
      .hazeSource(hazeState, zIndex = 1f)
      .hazeEffect(
        hazeState,
        style = HazeStyle(
          backgroundColor = backgroundColor,
          tints = listOf(HazeTint(backgroundColor)),
          blurRadius = blurRadius,
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

  // App bar content
  Surface(
    modifier = contentModifier
      .fillMaxWidth()
      .requiredHeight(totalHeight)
      .padding(padding), // Apply overall padding to the entire app bar
    color = Color.Transparent
  ) {
    Column {
      // Status bar spacer
      if (statusBarPadding > 0.dp) {
        Spacer(modifier = Modifier.height(statusBarPadding))
      }


      // Main app bar content
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(height)
      ) {
        // Navigation icon with custom alignment and padding
        navigationIcon?.let { icon ->
          Box(
            modifier = Modifier
              .align(navigationIconVerticalAlignment)
              .padding(navigationIconPadding)
          ) {
            Box(
              modifier = Modifier.size(48.dp),
              contentAlignment = Alignment.Center
            ) {
              CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface
              ) {
                icon()
              }
            }
          }
        }

        // Title with custom alignment and padding
        Box(
          modifier = Modifier
            .weight(1f)
            .align(titleVerticalAlignment)
            .padding(titlePadding)
        ) {
          CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
          ) {
            title()
          }
        }

        // Actions with custom alignment and padding
        Box(
          modifier = Modifier
            .align(actionsVerticalAlignment)
            .padding(actionsPadding)
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CompositionLocalProvider(
              LocalContentColor provides MaterialTheme.colorScheme.onSurface
            ) {
              actions()
            }
          }
        }
      }
    }
  }
}

/**
 * Animated version of HazeTopAppBar that responds to scroll state
 */
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun AnimatedHazeTopAppBar(
  title: @Composable () -> Unit,
  hazeModifier: Modifier = Modifier,
  contentModifier: Modifier = Modifier,
  navigationIcon: @Composable (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
  hazeState: HazeState,
  isScrolled: Boolean,
  height: Dp = HazeTopAppBarDefaults.Height,
  backgroundColor: Color = MaterialTheme.colorScheme.background,
  statusBarPadding: Dp = 0.dp,
  titleVerticalAlignment: Alignment.Vertical = HazeTopAppBarDefaults.DefaultVerticalAlignment,
  navigationIconVerticalAlignment: Alignment.Vertical = HazeTopAppBarDefaults.DefaultVerticalAlignment,
  actionsVerticalAlignment: Alignment.Vertical = HazeTopAppBarDefaults.DefaultVerticalAlignment,
  titlePadding: PaddingValues = HazeTopAppBarDefaults.DefaultTitlePadding,
  navigationIconPadding: PaddingValues = HazeTopAppBarDefaults.DefaultNavigationIconPadding,
  actionsPadding: PaddingValues = HazeTopAppBarDefaults.DefaultActionsPadding,
  padding: PaddingValues = HazeTopAppBarDefaults.DefaultPadding
) {
  val animatedBlurRadius by animateDpAsState(
    targetValue = if (isScrolled) HazeTopAppBarDefaults.DefaultBlurRadius else HazeTopAppBarDefaults.MinBlurRadius,
    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
    label = "blur_radius_animation"
  )

  HazeTopAppBar(
    title = title,
    hazeModifier = hazeModifier,
    contentModifier = contentModifier,
    navigationIcon = navigationIcon,
    actions = actions,
    hazeState = hazeState,
    blurRadius = animatedBlurRadius,
    height = height,
    backgroundColor = backgroundColor,
    statusBarPadding = statusBarPadding,
    titleVerticalAlignment = titleVerticalAlignment,
    navigationIconVerticalAlignment = navigationIconVerticalAlignment,
    actionsVerticalAlignment = actionsVerticalAlignment,
    titlePadding = titlePadding,
    navigationIconPadding = navigationIconPadding,
    actionsPadding = actionsPadding,
    padding = padding
  )
}
