import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.ChatScreen
import ui.ChatViewModel
import ui.WelcomeScreen

@Composable
@Preview
fun App(chatViewModel: ChatViewModel) {
    var loggedIn by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    MaterialTheme {
        if (loggedIn) {
            LaunchedEffect(true) {
                chatViewModel.connectToChat(username)
            }
            ChatScreen(chatViewModel)
        } else {
            WelcomeScreen(onJoinClick = { name ->
                loggedIn = true
                username = name
            })
        }
    }
}

fun main() = application {
    val scope = rememberCoroutineScope()
    val chatViewModel = ChatViewModel(scope)
    Window(
        onCloseRequest = {
            chatViewModel.disconnect()
            exitApplication()
        },
        title = "Chat",
        state = rememberWindowState(width = 300.dp, height = 600.dp),
    ) {
        App(chatViewModel)
    }
}
