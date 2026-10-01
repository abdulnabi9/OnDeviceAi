package com.abdul.aiondevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.WifiOff

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abdul.aiondevice.ui.theme.AiOnDeviceTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ChatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AiOnDeviceTheme {
                GemmaChatScreen(viewModel)
            }
        }
    }
}

@Composable
fun GemmaChatScreen(
    viewModel: ChatViewModel
) {

    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val isReady by viewModel.isReady.collectAsState()
    val status by viewModel.status.collectAsState()

    var input by remember {
        mutableStateOf("")
    }

    val listState = rememberLazyListState()

    /*
     * Automatically scroll to the latest message.
     */
    LaunchedEffect(
        messages.size,
        messages.lastOrNull()?.text
    ) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    Scaffold(

        topBar = {

            GemmaTopBar(
                isReady = isReady
            )
        },

        bottomBar = {

            if (isReady) {

                ChatInput(
                    value = input,

                    onValueChange = {
                        input = it
                    },

                    enabled = !isGenerating,

                    onSend = {

                        if (input.isNotBlank()) {

                            viewModel.send(input.trim())

                            input = ""
                        }
                    }
                )
            }
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (!isReady) {

                LoadingScreen(
                    status = status
                )

            } else {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    if (messages.isEmpty()) {

                        EmptyChat()

                    } else {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .imePadding(),

                            state = listState,

                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            ),

                            verticalArrangement = Arrangement.spacedBy(
                                12.dp
                            )
                        ) {

                            items(messages) { message ->

                                MessageBubble(
                                    message = message
                                )
                            }

                            if (isGenerating) {

                                item {

                                    GeneratingIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


/*
 * Top App Bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GemmaTopBar(
    isReady: Boolean
) {

    Column {

        TopAppBar(

            title = {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "Gemma"
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.size(10.dp)
                    )

                    Column {

                        Text(
                            text = "Gemma",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (isReady) {
                                "On-device AI"
                            } else {
                                "Loading model..."
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },

            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        HorizontalDivider()
    }
}


/*
 * Empty chat screen
 */
@Composable
fun EmptyChat() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {

            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = "Gemma is ready",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text(
                text = "Ask anything. Your conversation stays on this device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    ),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(
                        modifier = Modifier.size(6.dp)
                    )

                    Text(
                        text = "Works offline",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}



@Composable
fun LoadingScreen(
    status: String?
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                modifier = Modifier.size(42.dp)
            )

            Spacer(
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = "Preparing Gemma",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.size(6.dp)
            )

            Text(
                text = status ?: "Loading model...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


/*
 * Message bubble
 */
@Composable
fun MessageBubble(
    message: Chatmessage
) {

    val isUser = message.fromuser

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
            if (isUser) {
                Arrangement.End
            } else {
                Arrangement.Start
            }
    ) {

        Surface(

            modifier = Modifier.widthIn(
                max = 340.dp
            ),

            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),

            color =
                if (isUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
            ) {

                Text(
                    text = if (isUser) {
                        "You"
                    } else {
                        "Gemma"
                    },

                    style = MaterialTheme.typography.labelMedium,

                    fontWeight = FontWeight.Bold,

                    color =
                        if (isUser) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                )

                Spacer(
                    modifier = Modifier.size(5.dp)
                )

                Text(
                    text = message.text.ifEmpty {
                        "..."
                    },

                    style = MaterialTheme.typography.bodyLarge,

                    color =
                        if (isUser) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                )
            }
        }
    }
}



@Composable
fun GeneratingIndicator() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),

                verticalAlignment = Alignment.CenterVertically
            ) {

                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )

                Spacer(
                    modifier = Modifier.size(10.dp)
                )

                Text(
                    text = "Gemma is thinking...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}



@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    onSend: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding(),

        tonalElevation = 4.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                ),

            verticalAlignment = Alignment.Bottom
        ) {

            OutlinedTextField(

                value = value,

                onValueChange = onValueChange,

                enabled = enabled,

                modifier = Modifier.weight(1f),

                placeholder = {
                    Text("Message Gemma...")
                },

                maxLines = 5,

                shape = RoundedCornerShape(24.dp)
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            IconButton(

                onClick = onSend,

                enabled = enabled && value.isNotBlank(),

                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabled && value.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Send",

                    tint =
                        if (enabled && value.isNotBlank()) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                )
            }
        }
    }
}