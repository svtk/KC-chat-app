package ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kcchatapp.model.*

@Composable
fun ChatView(chatViewModel: ChatViewModel) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        MessageListView(
            chatEvents = chatViewModel.eventFlow.collectAsState(listOf()).value,
            username = chatViewModel.username.value,
        )
        Column {
            TypingUsersView(
                typingUsers = chatViewModel.typingUsers.collectAsState(setOf()).value,
            )
            CreateMessageView(
                chatViewModel::sendMessage,
                chatViewModel::startTyping,
            )
        }
    }
}

@Composable
fun MessageListView(
    chatEvents: List<ChatEvent>,
    username: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .padding(10.dp),
    ) {
        val state = rememberLazyListState()
        LazyColumn(
            state = state,
            reverseLayout = true,
        ) {
            items(
                items = chatEvents
            ) { event ->
                when (event) {
                    is MessageEvent -> MessageView(event, username)
                    is UserEvent -> UserEventView(event)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = state)
        )
    }
}

@Composable
private fun UserEventView(userEvent: UserEvent) {
    val change = when (userEvent.statusChange) {
        UserStatusChange.USER_JOINED -> "joined"
        UserStatusChange.USER_LEFT -> "left"
    }
    val systemText = "${userEvent.username} $change"
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card {
            Text(
                modifier = Modifier.padding(4.dp),
                text = systemText,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.overline
            )
        }
    }
}

@Composable
fun TypingUsersView(typingUsers: Set<String>) {
    val text = if (typingUsers.isEmpty()) {
        ""
    } else if (typingUsers.size == 1) {
        "${typingUsers.single()} is typing"
    } else {
        "${typingUsers.joinToString()} are typing"
    }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = text,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
            style = MaterialTheme.typography.overline
        )
    }
}

@Composable
private fun MessageView(
    event: MessageEvent,
    username: String?
) {
    val isOwnMessage = event.username == username
    Box(
        contentAlignment = if (isOwnMessage) {
            Alignment.CenterEnd
        } else Alignment.CenterStart,
        modifier = Modifier
            .run {
                if (isOwnMessage) {
                    padding(start = 60.dp, end = 20.dp)
                } else {
                    padding(end = 60.dp)
                }
            }
            .fillMaxWidth()
    ) {
        Card {
            Column(
                modifier = Modifier
                    .background(
                        color = if (isOwnMessage)
                            MaterialTheme.colors.secondary.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colors.onPrimary
                    )
            ) {
                Text(
                    text = event.username,
                    style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 5.dp, start = 5.dp, end = 15.dp),
                )
                Text(
                    text = event.message.text,
                    modifier = Modifier.padding(top = 2.dp, start = 5.dp, end = 15.dp)
                )
                Text(
                    text = event.message.timeText(),
                    modifier = Modifier.align(Alignment.End).padding(start = 20.dp),
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.overline,
                )
            }
        }
    }
}

@Composable
fun CreateMessageView(
    onMessageSent: (String) -> Unit,
    onUserIsTyping: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    Card(Modifier.padding(10.dp)) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.9f),
                value = message,
                onValueChange = {
                    message = it
                    onUserIsTyping()
                },
                label = { Text("Message") },
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                val enabled = message.isNotBlank()
                IconButton(
                    onClick = {
                        onMessageSent(message)
                        message = ""
                    },
                    enabled = enabled,
                    modifier = Modifier.padding(start = 10.dp),
                ) {
                    Icons.Filled.Send
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(40.dp),
                        tint = if (enabled)
                            MaterialTheme.colors.primary
                        else
                            MaterialTheme.colors.onBackground.copy(alpha = 0.3f),
                    )
                }
            }
        }
    }
}