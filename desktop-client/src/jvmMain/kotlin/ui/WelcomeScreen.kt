package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(viewModel: WelcomeViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            OutlinedTextField(
                modifier = Modifier.width(200.dp),
                value = viewModel.username.value,
                onValueChange = viewModel::onUsernameChange,
                label = { Text(text = "Username") },
            )
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                modifier = Modifier.width(200.dp),
                onClick = viewModel::onJoinClick,
                enabled = viewModel.username.value.isNotBlank(),
            ) {
                Text(text = "Join the chat")
            }
        }
    }
}