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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import model.Message
import model.timeText

@Composable
fun ChatView(chatViewModel: ChatViewModel) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        MessageListView(
            messages = chatViewModel.messagesFlow.collectAsState(persistentListOf()).value,
            username = chatViewModel.username.value,
        )
        Column {
            TypingUsersView(
                typingUsers = chatViewModel.typingUsers.collectAsState(persistentSetOf()).value,
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
    messages: ImmutableList<Message>,
    username: String?,
) {
//    println("Rendering message list for $username, last message: ${messages.firstOrNull()?.text}")
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
                items = messages
            ) { event ->
                MessageView(event, username)
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
fun TypingUsersView(typingUsers: Set<String>) {
    val text = if (typingUsers.isEmpty()) {
        ""
    } else if (typingUsers.size == 1) {
        "${typingUsers.single()} is typing"
    } else if (typingUsers.size == 2) {
        val (first, second) = typingUsers.toList()
        "$first and $second are typing"
    } else {
        val list = typingUsers.toList()
        val first = list.take(list.size - 1).joinToString()
        "$first, and ${list.last()} are typing"
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
    message: Message,
    username: String?
) {
    val isOwnMessage = message.username == username
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
                    text = message.username,
                    style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 5.dp, start = 5.dp, end = 15.dp),
                )
                Text(
                    text = message.text,
                    modifier = Modifier.padding(top = 2.dp, start = 5.dp, end = 15.dp)
                )
                Text(
                    text = message.timeText(),
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