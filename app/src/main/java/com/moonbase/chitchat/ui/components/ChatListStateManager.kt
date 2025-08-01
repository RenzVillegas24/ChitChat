package com.moonbase.chitchat.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data class representing corner radius values for each corner of a chat item
 */
@Stable
data class CornerRadiusState(
    val topStart: Dp = 0.dp,
    val topEnd: Dp = 0.dp,
    val bottomStart: Dp = 0.dp,
    val bottomEnd: Dp = 0.dp
) {
    companion object {
        fun allRounded(radius: Dp) = CornerRadiusState(radius, radius, radius, radius)
        fun topRounded(radius: Dp) = CornerRadiusState(radius, radius, 8.dp, 8.dp)
        fun bottomRounded(radius: Dp) = CornerRadiusState(8.dp, 8.dp, radius, radius)
        fun sharp() = CornerRadiusState(8.dp, 8.dp, 8.dp, 8.dp)
    }
}

/**
 * State manager for chat list items that handles selection states and corner radius calculations
 */
@Stable
class ChatListStateManager(
    private val itemCount: Int,
    private val defaultCornerRadius: Dp = 16.dp,
    private val innerCornerRadius: Dp = 5.dp,
    private val hoverCornerRadius: Dp = 12.dp
) {
    private var _selectedIndex by mutableStateOf(-1)
    private var _hoveredIndex by mutableStateOf(-1)
    private var _pressedIndex by mutableStateOf(-1)

    val selectedIndex: Int get() = _selectedIndex
    val hoveredIndex: Int get() = _hoveredIndex
    val pressedIndex: Int get() = _pressedIndex

    /**
     * Update the selected item index
     */
    fun selectItem(index: Int) {
        _selectedIndex = index
    }

    /**
     * Clear selection
     */
    fun clearSelection() {
        _selectedIndex = -1
    }

    /**
     * Update the hovered item index
     */
    fun setHoveredItem(index: Int) {
        _hoveredIndex = index
    }

    /**
     * Clear hover state
     */
    fun clearHover() {
        _hoveredIndex = -1
    }

    /**
     * Update the pressed item index
     */
    fun setPressedItem(index: Int) {
        _pressedIndex = index
    }

    /**
     * Clear pressed state
     */
    fun clearPressed() {
        _pressedIndex = -1
    }

    /**
     * Calculate the corner radius state for a specific item based on its position and selection/press state
     */
    fun getCornerRadiusState(
        itemIndex: Int,
        isFirst: Boolean,
        isLast: Boolean,
        isHovered: Boolean = false
    ): CornerRadiusState {
        // If item is hovered, override with hover radius
        if (isHovered) {
            return CornerRadiusState.allRounded(hoverCornerRadius)
        }

        // Use pressed state as the active state for corner radius calculation
        val activeIndex = if (_pressedIndex != -1) _pressedIndex else _selectedIndex

        // Calculate base corner radius based on position and active state (pressed or selected)
        return when {
            // No item active - use default list behavior
            activeIndex == -1 -> getDefaultCornerRadius(isFirst, isLast)
            
            // This item is active (pressed or selected)
            itemIndex == activeIndex -> getSelectedItemCornerRadius(isFirst, isLast)
            
            // This item is adjacent to active item
            itemIndex == activeIndex - 1 -> getAboveSelectedCornerRadius(isFirst, isLast)
            itemIndex == activeIndex + 1 -> getBelowSelectedCornerRadius(isFirst, isLast)
            
            // This item is not related to active state
            else -> getDefaultCornerRadius(isFirst, isLast)
        }
    }

    /**
     * Get default corner radius when no selection is active
     */
    private fun getDefaultCornerRadius(isFirst: Boolean, isLast: Boolean): CornerRadiusState {
        return when {
            isFirst && isLast -> CornerRadiusState.allRounded(defaultCornerRadius)
            isFirst -> CornerRadiusState(
                topStart = defaultCornerRadius,
                topEnd = defaultCornerRadius,
                bottomStart = innerCornerRadius,
                bottomEnd = innerCornerRadius
            )
            isLast -> CornerRadiusState(
                topStart = innerCornerRadius,
                topEnd = innerCornerRadius,
                bottomStart = defaultCornerRadius,
                bottomEnd = defaultCornerRadius
            )
            else -> CornerRadiusState.allRounded(innerCornerRadius)
        }
    }

    /**
     * Get corner radius for the active item (pressed or selected)
     */
    private fun getSelectedItemCornerRadius(isFirst: Boolean, isLast: Boolean): CornerRadiusState {
        return when {
            // First item selected - maintain top corners, sharp bottom corners
            isFirst -> CornerRadiusState.topRounded(defaultCornerRadius)
            // Last item selected - maintain bottom corners, sharp top corners
            isLast -> CornerRadiusState.bottomRounded(defaultCornerRadius)
            // Middle item selected - all corners sharp
            else -> CornerRadiusState.sharp()
        }
    }

    /**
     * Get corner radius for item above active item (pressed or selected)
     */
    private fun getAboveSelectedCornerRadius(isFirst: Boolean, isLast: Boolean): CornerRadiusState {
        return when {
            isFirst && isLast -> CornerRadiusState.allRounded(defaultCornerRadius)
            isFirst -> CornerRadiusState(
                topStart = defaultCornerRadius,
                topEnd = defaultCornerRadius,
                bottomStart = defaultCornerRadius, // Rounded to create gap
                bottomEnd = defaultCornerRadius    // Rounded to create gap
            )
            isLast -> CornerRadiusState(
                topStart = innerCornerRadius,
                topEnd = innerCornerRadius,
                bottomStart = defaultCornerRadius,
                bottomEnd = defaultCornerRadius
            )
            else -> CornerRadiusState(
                topStart = innerCornerRadius,
                topEnd = innerCornerRadius,
                bottomStart = defaultCornerRadius, // Rounded to create gap
                bottomEnd = defaultCornerRadius    // Rounded to create gap
            )
        }
    }

    /**
     * Get corner radius for item below active item (pressed or selected)
     */
    private fun getBelowSelectedCornerRadius(isFirst: Boolean, isLast: Boolean): CornerRadiusState {
        return when {
            isFirst && isLast -> CornerRadiusState.allRounded(defaultCornerRadius)
            isFirst -> CornerRadiusState(
                topStart = defaultCornerRadius, // Rounded to create gap
                topEnd = defaultCornerRadius,   // Rounded to create gap
                bottomStart = innerCornerRadius,
                bottomEnd = innerCornerRadius
            )
            isLast -> CornerRadiusState(
                topStart = defaultCornerRadius, // Rounded to create gap
                topEnd = defaultCornerRadius,   // Rounded to create gap
                bottomStart = defaultCornerRadius,
                bottomEnd = defaultCornerRadius
            )
            else -> CornerRadiusState(
                topStart = defaultCornerRadius, // Rounded to create gap
                topEnd = defaultCornerRadius,   // Rounded to create gap
                bottomStart = innerCornerRadius,
                bottomEnd = innerCornerRadius
            )
        }
    }

    /**
     * Check if an item is currently hovered
     */
    fun isItemHovered(index: Int): Boolean = _hoveredIndex == index

    /**
     * Check if an item is currently pressed
     */
    fun isItemPressed(index: Int): Boolean = _pressedIndex == index

    /**
     * Check if an item is currently selected
     */
    fun isItemSelected(index: Int): Boolean = _selectedIndex == index
}

/**
 * Remember a ChatListStateManager instance
 */
@Composable
fun rememberChatListStateManager(
    itemCount: Int,
    defaultCornerRadius: Dp = 16.dp,
    innerCornerRadius: Dp = 5.dp,
    hoverCornerRadius: Dp = 12.dp
): ChatListStateManager {
    return remember(itemCount) {
        ChatListStateManager(
            itemCount = itemCount,
            defaultCornerRadius = defaultCornerRadius,
            innerCornerRadius = innerCornerRadius,
            hoverCornerRadius = hoverCornerRadius
        )
    }
}
