package ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class WelcomeViewModel(
    private val onJoin: () -> Unit
) {
    private val _usernameText = mutableStateOf("")
    val username: State<String> = _usernameText

    fun onUsernameChange(username: String) {
        _usernameText.value = username
    }

    fun onJoinClick() {
        onJoin()
    }
}