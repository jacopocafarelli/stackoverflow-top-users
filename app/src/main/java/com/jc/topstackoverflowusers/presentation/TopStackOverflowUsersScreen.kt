package com.jc.topstackoverflowusers.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.presentation.model.ErrorType
import com.jc.topstackoverflowusers.presentation.model.TopUsersUiState
import com.jc.topstackoverflowusers.ui.theme.TopStackoverflowUsersTheme
import retrofit2.HttpException
import java.io.IOException

@Composable
fun TopStackOverflowUsersScreen(
    modifier: Modifier = Modifier,
    viewModel: TopStackoverflowUsersViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TopStackOverflowUsersScreen(uiState, { viewModel.onRetryClicked() }, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopStackOverflowUsersScreen(
    uiState: TopUsersUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar({ Text("Top StackOverFlow Users") }) }
    ) { innerPadding ->
        when (uiState) {
            is TopUsersUiState.Error -> {
                Error(
                    errorType = uiState.errorType,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize()
                )
            }

            TopUsersUiState.Loading -> {
                Loading(modifier = Modifier.fillMaxSize())
            }

            is TopUsersUiState.Success -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(uiState.users) { stackOverflowUser ->
                    StackOverflowUserItem(stackOverflowUser)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(modifier = Modifier.size(36.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            text = "Loading top users",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Error(
    errorType: ErrorType,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorType.toDisplayMessage(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onRetry) {
            Text(text = "Try Again")
        }
    }
}

@Composable
fun StackOverflowUserItem(stackOverflowUser: StackOverflowUser, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = stackOverflowUser.profileImageUrl,
            contentDescription = "Profile picture of ${stackOverflowUser.name}",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stackOverflowUser.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Reputation: ${formatReputation(stackOverflowUser.reputation)}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatReputation(reputation: Int): String {
    return when {
        reputation >= 1_000_000 -> String.format("%.1fM", reputation / 1_000_000.0)
        reputation >= 1_000 -> String.format("%.1fk", reputation / 1_000.0)
        else -> reputation.toString()
    }
}

@Composable
@Preview(showBackground = true)
fun TopStackOverflowUsersScreenPreview() {
    TopStackoverflowUsersTheme {
        TopStackOverflowUsersScreen(
            uiState = TopUsersUiState.Success(
                users = listOf(
                    StackOverflowUser(
                        id = 1,
                        name = "Test User 1",
                        profileImageUrl = "url 1",
                        reputation = 200
                    ),
                    StackOverflowUser(
                        id = 1,
                        name = "Test User 2",
                        profileImageUrl = "url 2",
                        reputation = 100
                    )
                )
            ),
            onRetry = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoadingPreview() {
    TopStackoverflowUsersTheme {
        Loading(modifier = Modifier.fillMaxSize())
    }
}

@Composable
@Preview(showBackground = true)
fun ErrorPreview() {
    TopStackoverflowUsersTheme {
        Error(errorType = ErrorType.NETWORK, onRetry = {}, modifier = Modifier.fillMaxSize())
    }
}

fun ErrorType.toDisplayMessage(): String {
    return when (this) {
        ErrorType.NETWORK -> "No internet connection. Please check your network and try again."
        ErrorType.SERVER -> "Server error occurred."
        else -> "An unexpected error occurred. Please try again."
    }
}