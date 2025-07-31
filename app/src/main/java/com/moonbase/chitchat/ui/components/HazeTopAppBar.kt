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
    val ChatHeight = 96.dp // For chat screens with avatars
    val GroupChatHeight = 156.dp // For group chat screens with avatars
    val DefaultBlurRadius = 24.dp
    val MinBlurRadius = 0.dp
    
    // Vertical alignment defaults
    val DefaultVerticalAlignment = Alignment.Top
    val CenterVerticalAlignment = Alignment.CenterVertically
    val BottomVerticalAlignment = Alignment.Bottom
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
 * @param verticalAlignment The vertical alignment of elements within the app bar
 */
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun HazeTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    hazeState: HazeState,
    blurRadius: Dp = HazeTopAppBarDefaults.DefaultBlurRadius,
    height: Dp = HazeTopAppBarDefaults.Height,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    statusBarPadding: Dp = 0.dp,
    verticalAlignment: Alignment.Vertical = HazeTopAppBarDefaults.DefaultVerticalAlignment
) {
    val totalHeight = height + statusBarPadding

    // Haze background
    Box(
        modifier = modifier
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
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(totalHeight),
        color = Color.Transparent
    ) {
        Column(
            verticalArrangement = when (verticalAlignment) {
                Alignment.Top -> Arrangement.Top
                Alignment.CenterVertically -> Arrangement.Center
                Alignment.Bottom -> Arrangement.Bottom
                else -> Arrangement.Top
            }
        ) {
            // Status bar spacer
            if (statusBarPadding > 0.dp && verticalAlignment == Alignment.Top) {
                Spacer(modifier = Modifier.height(statusBarPadding))
            }
            
            // Main app bar content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigation icon
                navigationIcon?.let { icon ->
                    Box(
                        modifier = Modifier
                            .size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        icon()
                    }
                }
                
                // Title
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurface
                    ) {
                        title()
                    }
                }
                
                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions()
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
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    hazeState: HazeState,
    isScrolled: Boolean,
    height: Dp = HazeTopAppBarDefaults.Height,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    statusBarPadding: Dp = 0.dp,
    verticalAlignment: Alignment.Vertical = HazeTopAppBarDefaults.DefaultVerticalAlignment
) {
    val animatedBlurRadius by animateDpAsState(
        targetValue = if (isScrolled) HazeTopAppBarDefaults.DefaultBlurRadius else HazeTopAppBarDefaults.MinBlurRadius,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "blur_radius_animation"
    )

    HazeTopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        hazeState = hazeState,
        blurRadius = animatedBlurRadius,
        height = height,
        backgroundColor = backgroundColor,
        statusBarPadding = statusBarPadding,
        verticalAlignment = verticalAlignment
    )
}
