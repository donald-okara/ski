package com.example.chat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.tables.Message

val dummyMessages = listOf(
    Message("1", "profile1", "Hey, how are you?", false),
    Message("2", "profile2", "I'm good, thanks! How about you?", true),
    Message("3", "profile3", "I'm doing great!", false),
    Message("4", "profile4", "That's good to hear!", true),
    Message("5", "profile5", "What have you been up to?", true),
    Message("6", "profile6", "Not much, just working.", false),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailsScreen(
    profileName: String,
    onProfileClick: (String) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chat with $profileName") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                items(dummyMessages.asReversed()) { message ->
                    ChatBubble(message, onProfileClick = {
                        onProfileClick(profileName)
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, onProfileClick: () -> Unit) {
    val bubbleColor = if (message.isFromMe) MaterialTheme.colorScheme.primary else Color.LightGray
    val textColor = if (message.isFromMe) Color.White else Color.Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier.width(250.dp).clip(RoundedCornerShape(16.dp))
                .background(bubbleColor).clickable(
                    onClick = onProfileClick
                ).padding(16.dp)
        ) {
            Text(text = message.text, color = textColor)
        }
    }
}