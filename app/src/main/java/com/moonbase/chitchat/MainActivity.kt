package com.moonbase.chitchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.ExperimentalSharedTransitionApi
import com.moonbase.chitchat.ui.theme.ChitChatTheme
import com.moonbase.chitchat.ui.screens.ChatDetailScreen
import com.moonbase.chitchat.ui.screens.ChatListScreen
import com.moonbase.chitchat.data.SampleData

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Ensure the window handles keyboard properly with the new WindowInsets API
    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      ChitChatTheme {
        ChitChatApp()
      }
    }
  }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChitChatApp() {
  val navController = rememberNavController()

  SharedTransitionLayout {
    NavHost(
      navController = navController,
      startDestination = "chat_list"
    ) {
      composable(
        "chat_list",
        enterTransition = {
          // When returning from chat detail, scale in
          scaleIn(
            initialScale = 0.95f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
          ) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
          // When going to chat detail, just scale down slightly and fade
          scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
          ) + fadeOut(animationSpec = tween(500))
        }
      ) {
        ChatListScreen(
          onChatClick = { chat ->
            navController.navigate("chat_detail/${chat.id}")
          },
          sharedTransitionScope = this@SharedTransitionLayout,
          animatedVisibilityScope = this
        )
      }

      composable(
        "chat_detail/{chatId}",
        arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
        enterTransition = {
          // When entering chat detail, just fade in (shared element handles the main transition)
          fadeIn(animationSpec = tween(500, easing = FastOutSlowInEasing))
        },
        exitTransition = {
          // When leaving chat detail, just fade out
          fadeOut(animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
      ) { backStackEntry ->
        val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
        val chatDetailData = SampleData.createChatDetailDataById(chatId)
        ChatDetailScreen(
          chatData = chatDetailData,
          onBackClick = { navController.popBackStack() },
          sharedTransitionScope = this@SharedTransitionLayout,
          animatedVisibilityScope = this
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun ChitChatPreview() {
  ChitChatTheme {
    ChitChatApp()
  }
}
