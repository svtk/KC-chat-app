import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.ChatView
import ui.ChatViewModel
import ui.WelcomeScreen
import ui.WelcomeViewModel

@Composable
@Preview
fun App(chatViewModel: ChatViewModel) {
    var loggedIn by remember { mutableStateOf(false) }
    val welcomeViewModel = WelcomeViewModel(onJoin = { loggedIn = true })
    MaterialTheme {
        if (loggedIn) {
            LaunchedEffect(true) {
                chatViewModel.connectToChat(welcomeViewModel.username.value)
            }
            ChatView(chatViewModel)
        } else {
            WelcomeScreen(welcomeViewModel)
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
