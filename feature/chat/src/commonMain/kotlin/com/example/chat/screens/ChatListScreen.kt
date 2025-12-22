package com.example.chat.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.tables.Chat

val dummyChats = listOf(
    Chat("1", "John Doe", "Hey, how are you?"),
    Chat("2", "Jane Smith", "Let's catch up later."),
    Chat("3", "Peter Jones", "See you tomorrow!"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(onChatItemClick: (Pair<String, String>) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chats") },
            )
        }
    ){ innerPadding ->
        LazyColumn(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            items(dummyChats) { chat ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChatItemClick(chat.id to chat.name) }
                        .padding(16.dp)
                ) {
                    Text(text = chat.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = chat.lastMessage, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}